package skywolf46.devain.discord

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.requests.GatewayIntent
import skywolf46.devain.discord.command.BasicDiscordCommand
import skywolf46.devain.discord.command.EnhancedDiscordCommand
import skywolf46.devain.discord.data.lifecycle.DiscordFallback

class DiscordWrapper(val jda: JDA, fallback: DiscordFallback) {
    companion object {
        @JvmStatic
        fun withToken(
            token: String,
            fallback: DiscordFallback,
            vararg intent: GatewayIntent = GatewayIntent.values(),
            onInitialize: (JDABuilder) -> Unit = {}
        ): DiscordWrapper {
            return DiscordWrapper(
                JDABuilder.create(token, intent.toList()).apply(onInitialize).build().awaitReady(), fallback
            )
        }

        @JvmStatic
        fun builder(): Builder {
            return Builder()
        }
    }

    private val commandAdapter by lazy {
        DiscordCommandAdapter(jda, fallback)
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

    class Builder {
        private val commands = mutableListOf<BasicDiscordCommand>()
        private var fallback: DiscordFallback.Builder = DiscordFallback.builder()
        private var token: String? = null
        private var onInitialize: (JDABuilder) -> Unit = {}

        fun fallback(fallback: DiscordFallback.Builder): Builder {
            this.fallback = fallback
            return this
        }

        fun fallbackButton(key: String, fallback: (ButtonInteractionEvent) -> Unit): Builder {
            this.fallback.buttonFallback(key, fallback)
            return this
        }

        fun fallbackStringSelection(key: String, fallback: (StringSelectInteractionEvent) -> Unit): Builder {
            this.fallback.stringSelectionFallback(key, fallback)
            return this
        }

        fun fallbackSelection(key: String, fallback: (EntitySelectInteractionEvent) -> Unit): Builder {
            this.fallback.selectionFallback(key, fallback)
            return this
        }


        fun token(token: String): Builder {
            this.token = token
            return this
        }

        fun onInitialize(onInitialize: (JDABuilder) -> Unit): Builder {
            this.onInitialize = onInitialize
            return this
        }

        fun registerCommands(vararg command: BasicDiscordCommand): Builder {
            commands.addAll(command)
            return this
        }

        fun build(): DiscordWrapper {
            return withToken(
                token ?: throw IllegalStateException("Token is not provided."), fallback.build()
            ) {
                onInitialize.invoke(it)
            }.registerCommands(*commands.toTypedArray())
        }
    }
}