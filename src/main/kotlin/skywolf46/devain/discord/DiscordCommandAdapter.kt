package skywolf46.devain.discord

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import skywolf46.devain.discord.command.BasicDiscordCommand
import skywolf46.devain.discord.command.EnhancedDiscordCommand

class DiscordCommandAdapter(private val jda: JDA) : ListenerAdapter() {
    private val commandRegistry = mutableMapOf<String, BasicDiscordCommand>()
    private val buttonRegistry = mutableMapOf<String, EnhancedDiscordCommand>()
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
                x.implementedButtons.forEach { btn ->
                    buttonRegistry[btn] = x
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
        buttonRegistry[event.componentId]?.onButtonClicked(event)
    }
}