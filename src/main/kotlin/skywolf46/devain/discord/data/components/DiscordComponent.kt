package skywolf46.devain.discord.data.components

import net.dv8tion.jda.api.events.Event
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.utils.messages.MessageCreateRequest

interface DiscordComponent<EVENT: Event> {
    fun build(): ItemComponent

    fun getComponentId() : String

    fun onTrigger(event: EVENT)
}