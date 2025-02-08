package skywolf46.devain.discord

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import skywolf46.devain.discord.command.*
import skywolf46.devain.discord.data.lifecycle.DiscordFallback
import skywolf46.devain.discord.listeners.ActionListenerContainer

class DiscordCommandAdapter(
    private val jda: JDA,
    private val interactionFallback: DiscordFallback,
    private val actionListenerContainer: ActionListenerContainer = ActionListenerContainer.SHARED,
    private val lifespanManager: MessageListenerLifespanManager<Long> = MessageListenerLifespanManager.SHARED
) : ListenerAdapter() {
    private val slashCommandRegistry = mutableMapOf<String, BasicDiscordCommand>()
    private val userCommandRegistry = mutableMapOf<String, UserCommand>()
    private val messageCommandRegistry = mutableMapOf<String, MessageCommand>()
    private val modalRegistry = mutableMapOf<String, EnhancedDiscordCommand>()

    fun prepareRegisterCommands(vararg command: BasicDiscordCommand) {
        for (x in command) {
            val commandData = x.provideCommands()
            for (extractedCommand in commandData) {
                slashCommandRegistry[extractedCommand.command] = x
                if (x is EnhancedDiscordCommand) {
                    x.modalId.onSome {
                        modalRegistry[it] = x
                    }
                }
            }
        }
    }

    fun prepareUserCommands(vararg command: UserCommand) {
        for (x in command) {
            val commandData = x.provideCommands()
            for (extractedCommand in commandData) {
                println("Registering user command ${extractedCommand.command}")
                userCommandRegistry[extractedCommand.command] = x
            }
        }
    }

    fun prepareMessageCommands(vararg command: MessageCommand) {
        for (x in command) {
            val commandData = x.provideCommands()
            for (extractedCommand in commandData) {
                println("Registering message command ${extractedCommand.command}")
                messageCommandRegistry[extractedCommand.command] = x
            }
        }
    }

    fun finalizeRegistration() {
        println("Finalizing registration")
        val action = jda.updateCommands()
        for (x in userCommandRegistry.values) {
            println("Registering user command ${x.provideCommands().map { it.data.name }}")
            action.addCommands(x.provideCommands().map { it.data })
        }
        for (x in messageCommandRegistry.values) {
            kotlin.runCatching {
                println("Class: ${x::class.java.name}")
                println("Registering message command ${x.provideCommands().map { it.data.name }}")
                action.addCommands(x.provideCommands().map { it.data })
            }.onFailure {
                it.printStackTrace()
            }
        }
        for (x in slashCommandRegistry.values) {
            println("Registering command ${x.provideCommands().map { it.data.name }}")
            action.addCommands(x.provideCommands().map { it.data })
        }
        action.queue()
    }



    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        slashCommandRegistry[event.name]?.triggerCommand(event)
    }

    override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
        slashCommandRegistry[event.name]?.triggerAutoComplete(event)
    }

    override fun onModalInteraction(event: ModalInteractionEvent) {
        modalRegistry[event.interaction.modalId]?.onModal(event)
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        if (!actionListenerContainer.trigger(event.messageIdLong, event.componentId, event)) {
            interactionFallback.expectButton(event)
        }
    }

    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        if (!actionListenerContainer.trigger(event.messageIdLong, event.componentId, event)) {
            interactionFallback.expectStringSelection(event)
        }
    }

    override fun onMessageDelete(event: MessageDeleteEvent) {
        lifespanManager.removeLifespanObserver(event.messageIdLong)
        actionListenerContainer.removeListener(event.messageIdLong)
    }

    override fun onMessageContextInteraction(event: MessageContextInteractionEvent) {
        println("Message context interaction: ${event.name}")
        messageCommandRegistry[event.name]?.onMessageCommand(event)
    }

    override fun onUserContextInteraction(event: UserContextInteractionEvent) {
        println("User context interaction: ${event.name}")
        userCommandRegistry[event.name]?.onUserCommand(event)
    }
}