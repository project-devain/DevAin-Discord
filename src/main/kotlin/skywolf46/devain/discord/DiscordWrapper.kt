package skywolf46.devain.discord

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import skywolf46.devain.discord.command.BasicDiscordCommand
import skywolf46.devain.discord.command.EnhancedDiscordCommand

class DiscordWrapper(val jda: JDA) {
    companion object {
        @JvmStatic
        fun withToken(
            token: String, vararg intent: GatewayIntent = GatewayIntent.values(), onInitialize: (JDABuilder) -> Unit = {}
        ): DiscordWrapper {
            return DiscordWrapper(
                JDABuilder.create(token, GatewayIntent.values().toList()).apply(onInitialize).build().awaitReady()
            )
        }
    }

    private val commandAdapter by lazy {
        DiscordCommandAdapter(jda)
    }

    private val commands = mutableListOf<BasicDiscordCommand>()


    fun registerCommands(vararg command: BasicDiscordCommand): DiscordWrapper {
        commands.addAll(command)
        return this
    }

    fun registerCommands(vararg command: EnhancedDiscordCommand): DiscordWrapper {
        commands.addAll(command)
        return this
    }

    fun finishSetup() {
        jda.addEventListener(commandAdapter)
        commandAdapter.registerCommands(*commands.toTypedArray())
    }
}