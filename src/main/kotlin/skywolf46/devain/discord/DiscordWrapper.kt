package skywolf46.devain.discord

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.requests.GatewayIntent
import skywolf46.devain.discord.command.*
import skywolf46.devain.discord.data.lifecycle.DiscordFallback

class DiscordWrapper(val jda: JDA, val fallback: DiscordFallback = DiscordFallback()) {
    companion object {
        @JvmStatic
        fun withToken(
            token: String,
            vararg intent: GatewayIntent = GatewayIntent.values(),
            onInitialize: (JDABuilder) -> Unit = {},
            fallback: DiscordFallback = DiscordFallback()
        ): DiscordWrapper {
            return DiscordWrapper(
                JDABuilder.create(token, intent.toList()).apply(onInitialize).build().awaitReady(), fallback
            )
        }
    }

    private val commandAdapter by lazy {
        DiscordCommandAdapter(jda, fallback)
    }

    fun registerCommands(vararg command: BasicDiscordCommand): DiscordWrapper {
        commandAdapter.prepareRegisterCommands(*command)
        return this
    }

    fun registerCommands(vararg command: EnhancedDiscordCommand): DiscordWrapper {
        commandAdapter.prepareRegisterCommands(*command)
        return this
    }

    fun fallbackButton(key: String, fallback: (ButtonInteractionEvent) -> Unit): DiscordWrapper {
        this.fallback.buttonFallback(key, fallback)
        return this
    }

    fun fallbackStringSelection(key: String, fallback: (StringSelectInteractionEvent) -> Unit): DiscordWrapper {
        this.fallback.stringSelectionFallback(key, fallback)
        return this
    }

    fun fallbackSelection(key: String, fallback: (EntitySelectInteractionEvent) -> Unit): DiscordWrapper {
        this.fallback.selectionFallback(key, fallback)
        return this
    }

    fun registerUserCommands(vararg command: UserCommand) : DiscordWrapper {
        commandAdapter.prepareUserCommands(*command)
        return this
    }

    fun registerMessageCommands(vararg command: MessageCommand): DiscordWrapper {
        commandAdapter.prepareMessageCommands(*command)
        return this
    }

    fun finishSetup() {
        jda.addEventListener(commandAdapter)
        commandAdapter.finalizeRegistration()
    }
}