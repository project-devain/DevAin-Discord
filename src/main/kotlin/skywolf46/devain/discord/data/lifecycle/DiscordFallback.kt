package skywolf46.devain.discord.data.lifecycle

import net.dv8tion.jda.api.events.Event
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent

class DiscordFallback(private val fallbacks: Map<String, (Event) -> Unit>) {
    companion object {
        @JvmStatic
        fun builder(): Builder {
            return Builder()
        }
    }

    fun <T : Event> expect(key: String, data: T) {
        fallbacks[key]?.invoke(data)
    }

    fun expectButton(event: ButtonInteractionEvent) {
        fallbacks[event.componentId]?.invoke(event)
    }

    fun expectStringSelection(event: StringSelectInteractionEvent) {
        fallbacks[event.componentId]?.invoke(event)
    }

    fun expectSelection(event: EntitySelectInteractionEvent) {
        fallbacks[event.componentId]?.invoke(event)
    }

    class Builder {
        private val fallbacks = mutableMapOf<String, (Event) -> Unit>()

        fun <LISTENER : Event> fallback(name: String, fallback: (LISTENER) -> Unit) {
            fallbacks[name] = fallback as (Event) -> Unit
        }

        fun buttonFallback(key: String, fallback: (ButtonInteractionEvent) -> Unit) {
            fallback(key, fallback)
        }

        fun stringSelectionFallback(key: String, fallback: (StringSelectInteractionEvent) -> Unit) {
            fallback(key, fallback)
        }

        fun selectionFallback(key: String, fallback: (EntitySelectInteractionEvent) -> Unit) {
            fallback(key, fallback)
        }

        fun build(): DiscordFallback {
            return DiscordFallback(fallbacks)
        }
    }
}