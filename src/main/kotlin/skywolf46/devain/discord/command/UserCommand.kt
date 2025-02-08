package skywolf46.devain.discord.command

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionContextType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import skywolf46.devain.discord.data.CommandType
import skywolf46.devain.discord.data.PredefinedCommandData

interface UserCommand : CommandProvider{
    fun getUserCommandName(): String

    fun getUserCommandAliases(): List<String> = emptyList()

    fun modifyUserCommandArgs(args: List<PredefinedCommandData>) {}

    fun getCommandType() : CommandType

    fun onUserCommand(event: UserContextInteractionEvent) {
        // Wa sans
    }

    override fun provideCommands(): List<PredefinedCommandData> {
        val commands = (getUserCommandAliases() + getUserCommandName()).map {
            PredefinedCommandData(it, Commands.user(it).setIntegrationTypes(*getCommandType().type).setContexts(InteractionContextType.ALL))
        }
        modifyUserCommandArgs(commands)
        return commands
    }

}