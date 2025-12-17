package skywolf46.devain.discord.data.components

import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent
import net.dv8tion.jda.api.events.Event

interface DiscordComponent<EVENT: Event> {
    fun build(): ActionRowChildComponent

    fun getComponentId() : String

    fun onTrigger(event: EVENT)
}