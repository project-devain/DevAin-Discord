package skywolf46.devain.discord.command

import arrow.core.Either
import arrow.core.identity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import org.koin.core.component.KoinComponent

/**
 * 기초적인 명령어 구현을 위한 유틸리티 추상 클래스, BasicDiscordCommand입니다.
 *
 * 이 클래스는 복잡한 JDA의 명령어 구현 대신, 추상 클래스를 이용하여 빠른 명령어 구현을 지원합니다.
 *
 * 명령어 구현에 대한 간단한 내용은 [skywolf46.devain.discord.test.BasicHelloWorldCommand]를 참고하세요.
 */
abstract class BasicDiscordCommand : KoinComponent {
    @OptIn(DelicateCoroutinesApi::class)
    fun triggerCommand(event: SlashCommandInteractionEvent) {
        GlobalScope.launch(Dispatchers.Default) {
            onCommand(event)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun triggerAutoComplete(event: CommandAutoCompleteInteractionEvent) {
        GlobalScope.launch(Dispatchers.Default) {
            onAutoComplete(event)
        }
    }

    protected open suspend fun onCommand(event: SlashCommandInteractionEvent) {

    }

    protected open suspend fun onAutoComplete(event: CommandAutoCompleteInteractionEvent) {

    }

    abstract fun createCommandInfo(): Pair<String, CommandData>

    @OptIn(DelicateCoroutinesApi::class)
    fun SlashCommandInteractionEvent.deferEmbed(
        isEphemeral: Boolean = false,
        unit: suspend (event: SlashCommandInteractionEvent, hook: InteractionHook) -> Either<String, MessageEmbed>
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
    fun SlashCommandInteractionEvent.deferMessage(
        isEphemeral: Boolean = false,
        unit: suspend (event: SlashCommandInteractionEvent, hook: InteractionHook) -> Either<String, String>
    ) {
        deferReply(isEphemeral).queue { hook ->
            GlobalScope.launch(Dispatchers.Default) {
                hook.sendMessage(unit(this@deferMessage, hook).fold(::identity, ::identity)).queue()
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun SlashCommandInteractionEvent.deferError(
        isEphemeral: Boolean = false,
        unit: suspend (event: SlashCommandInteractionEvent, hook: InteractionHook) -> Either<String, Unit>
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
    fun SlashCommandInteractionEvent.defer(
        isEphemeral: Boolean = false,
        unit: suspend (event: SlashCommandInteractionEvent, hook: InteractionHook) -> Unit
    ) {
        deferReply(isEphemeral).queue { hook ->
            GlobalScope.launch(Dispatchers.Default) {
                unit(this@defer, hook)
            }
        }
    }

}