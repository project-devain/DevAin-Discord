package skywolf46.devain.discord

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.events.message.MessageDeleteEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import skywolf46.devain.discord.command.BasicDiscordCommand
import skywolf46.devain.discord.command.EnhancedDiscordCommand
import skywolf46.devain.discord.data.lifecycle.DiscordFallback
import skywolf46.devain.discord.listeners.ActionListenerContainer

class DiscordCommandAdapter(
    private val jda: JDA,
    private val interactionFallback: DiscordFallback,
    private val actionListenerContainer: ActionListenerContainer = ActionListenerContainer.SHARED,
    private val lifespanManager: MessageListenerLifespanManager<Long> = MessageListenerLifespanManager.SHARED
) : ListenerAdapter() {
    private val commandRegistry = mutableMapOf<String, BasicDiscordCommand>()
    private val modalRegistry = mutableMapOf<String, EnhancedDiscordCommand>()

    fun registerCommands(vararg command: BasicDiscordCommand) {
        val updateAction = jda.updateCommands()
        for (x in command) {
            val commandData = x.createCommandInfo()
            commandRegistry[commandData.first] = x
            updateAction.addCommands(commandData.second)
            if (x is EnhancedDiscordCommand) {
                x.modalId.onSome {
                    modalRegistry[it] = x
                }
            }
        }
        updateAction.queue()
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        commandRegistry[event.name]?.triggerCommand(event)
    }

    override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {
        commandRegistry[event.name]?.triggerAutoComplete(event)
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
        if(!actionListenerContainer.trigger(event.messageIdLong, event.componentId, event)) {
            interactionFallback.expectStringSelection(event)
        }
    }

    override fun onMessageDelete(event: MessageDeleteEvent) {
        lifespanManager.removeLifespanObserver(event.messageIdLong)
        actionListenerContainer.removeListener(event.messageIdLong)
    }
}