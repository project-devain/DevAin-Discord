package skywolf46.devain.discord.test

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import skywolf46.devain.discord.command.BasicDiscordCommand


class BasicHelloWorldCommand : BasicDiscordCommand() {
    override fun createCommandInfo(): Pair<String, CommandData> {
        return "hello-world" to Commands.slash("hello-world", "Hello world moment")
            .addOptions(OptionData(OptionType.STRING, "user-name", "닉네임", true))

    }

    override suspend fun onCommand(event: SlashCommandInteractionEvent) {
        event.defer { _, hook ->
            hook.sendMessage("Hello, world! Your name is ${event.getOption("user-name")?.asString}.").queue()
        }
    }

}