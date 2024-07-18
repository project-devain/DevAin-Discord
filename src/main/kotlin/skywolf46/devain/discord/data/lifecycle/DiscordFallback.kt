package skywolf46.devain.discord.data.lifecycle

import net.dv8tion.jda.api.events.Event
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class DiscordFallback {
    private val fallbacks = mutableMapOf<String, (Event) -> Unit>()

    private val lock = ReentrantReadWriteLock()

    fun <T : Event> expect(key: String, data: T) {
        lock.read {
            fallbacks[key]?.invoke(data)
        }
    }

    fun expectButton(event: ButtonInteractionEvent) {
        expect(event.componentId, event)
    }

    fun expectStringSelection(event: StringSelectInteractionEvent) {
        expect(event.componentId, event)
    }

    fun expectSelection(event: EntitySelectInteractionEvent) {
        expect(event.componentId, event)
    }


    fun <LISTENER : Event> fallback(name: String, fallback: (LISTENER) -> Unit) {
        lock.write {
            fallbacks[name] = fallback as (Event) -> Unit
        }
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

}