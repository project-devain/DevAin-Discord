package skywolf46.devain.discord.command

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionContextType
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.build.Commands
import skywolf46.devain.discord.data.CommandType
import skywolf46.devain.discord.data.PredefinedCommandData

interface MessageCommand : CommandProvider {
    fun getMessageCommandName(): String

    fun getMessageCommandAlias(): List<String> = emptyList()

    fun modifyMessageCommands(args: List<PredefinedCommandData>) {}

    fun onMessageCommand(event: MessageContextInteractionEvent) {
        // Wa sans
    }

    fun getCommandType(): CommandType

    override fun provideCommands(): List<PredefinedCommandData> {
        val commands = (getMessageCommandAlias() + getMessageCommandName()).map {
            PredefinedCommandData(
                it, Commands.message(it).setIntegrationTypes(
                    *getCommandType().type
                ).setContexts(InteractionContextType.ALL)
            )
        }
        modifyMessageCommands(commands)
        return commands
    }
}