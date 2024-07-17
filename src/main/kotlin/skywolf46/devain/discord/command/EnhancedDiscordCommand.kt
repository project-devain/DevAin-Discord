package skywolf46.devain.discord.command

import arrow.core.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.commands.CommandAutoCompleteInteraction
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.modals.Modal
import net.dv8tion.jda.api.utils.FileUpload
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.withLock
import kotlin.concurrent.write

abstract class EnhancedDiscordCommand(
    val command: String,
    val descrption: String = "제공된 명령어 설명이 존재하지 않습니다.",
    val modalId: Option<String> = None
) : BasicDiscordCommand() {

    private val userModalHandler = mutableMapOf<Long, suspend (ModalInteractionEvent) -> Unit>()

    private val userModalHandlerLock = ReentrantLock()

    private val completions = mutableMapOf<String, suspend (CommandAutoCompleteInteraction) -> List<String>>()

    private val completionLock = ReentrantReadWriteLock()

    fun createModal(modalTitle: String, builder: (Modal.Builder) -> Unit): Modal {
        return modalId.getOrElse {
            throw IllegalStateException("이 명령어에는 모달 ID가 등록되지 않았습니다. 생성자에서 모달 ID를 지정하세요.")
        }.let {
            Modal.create(it, modalTitle).apply(builder)
        }.build()
    }

    protected fun SlashCommandInteraction.listenModal(modal: Modal, onModal: suspend (ModalInteractionEvent) -> Unit) {
        userModalHandlerLock.withLock {
            userModalHandler[user.idLong] = onModal
        }
        replyModal(modal).queue()
    }

    protected open fun modifyCommandData(options: SlashCommandData) {
        // Do nothing
    }

    @OptIn(DelicateCoroutinesApi::class)
    open fun onModal(event: ModalInteractionEvent) {
        userModalHandlerLock.withLock {
            userModalHandler.remove(event.user.idLong)
        }?.apply {
            GlobalScope.launch(Dispatchers.Default) {
                invoke(event)
            }
        }
    }

    @Deprecated("Use ReplyCallAction#queueComponent instead; All action replaced with single-call lambda and fallback action")
    open fun onButtonClicked(event: ButtonInteractionEvent) {
        // Do nothing
    }

    protected fun SlashCommandData.addCompletableOption(
        name: String,
        description: String,
        required: Boolean,
        completion: suspend (CommandAutoCompleteInteraction) -> List<String>
    ): SlashCommandData {
        addOption(OptionType.STRING, name, description, required, true)
        completionLock.write {
            completions[name] = completion
        }
        return this
    }


    override suspend fun onAutoComplete(event: CommandAutoCompleteInteractionEvent) {
        if (event.focusedOption.name in completions) {
            event.replyChoiceStrings(
                completionLock.read {
                    completions[event.focusedOption.name]!!
                }.invoke(event)
                    .filter { it.startsWith(event.focusedOption.value) }
                    .let { it.subList(0, it.size.coerceAtMost(25)) })
                .queue()
        }
    }

    fun createCommandData(): SlashCommandData {
        return Commands.slash(command, descrption).apply {
            modifyCommandData(this)
        }
    }

    override fun createCommandInfo(): Pair<String, CommandData> {
        return command to createCommandData()
    }

    protected fun box(string: String, prefix: String = ""): String {
        return "```$prefix\n$string```"
    }

    fun InteractionHook.sendMessageOrEmbed(embedThreshold: Int, text: String, embed: (EmbedBuilder) -> Unit) {
        if (text.length >= 2000) {
            sendFiles(FileUpload.fromData(text.toByteArray(), "result.txt")).queue()
            return
        }
        if (text.length >= embedThreshold) {
            sendMessage(text).queue()
            return
        }
        sendMessageEmbeds(EmbedBuilder().apply(embed).build()).queue()
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun ModalInteractionEvent.deferEmbed(
        isEphemeral: Boolean = false,
        unit: suspend (event: ModalInteractionEvent, hook: InteractionHook) -> Either<String, MessageEmbed>
    ) {
        deferReply(isEphemeral).queue { hook ->
            GlobalScope.launch(Dispatchers.Default) {
                unit(this@deferEmbed, hook).onLeft {
                    hook.sendMessage(it).queue()
                }.onRight {
                    hook.sendMessageEmbeds(it).queue()
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun ModalInteractionEvent.deferMessage(
        isEphemeral: Boolean = false,
        unit: suspend (event: ModalInteractionEvent, hook: InteractionHook) -> Either<String, String>
    ) {
        deferReply(isEphemeral).queue { hook ->
            GlobalScope.launch(Dispatchers.Default) {
                hook.sendMessage(unit(this@deferMessage, hook).fold(::identity, ::identity)).queue()
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun ModalInteractionEvent.deferError(
        isEphemeral: Boolean = false,
        unit: suspend (event: ModalInteractionEvent, hook: InteractionHook) -> Either<String, Unit>
    ) {
        deferReply(isEphemeral).queue { hook ->
            GlobalScope.launch(Dispatchers.Default) {
                unit(this@deferError, hook).onLeft {
                    hook.sendMessage(it).queue()
                }
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun ModalInteractionEvent.defer(
        isEphemeral: Boolean = false,
        unit: suspend (event: ModalInteractionEvent, hook: InteractionHook) -> Unit
    ) {
        deferReply(isEphemeral).queue { hook ->
            GlobalScope.launch(Dispatchers.Default) {
                unit(this@defer, hook)
            }
        }
    }
}