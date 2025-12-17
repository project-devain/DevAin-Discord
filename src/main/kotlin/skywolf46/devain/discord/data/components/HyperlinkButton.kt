package skywolf46.devain.discord.data.components

import arrow.core.Either
import arrow.core.getOrElse
import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

data class HyperlinkButton(
    val emoji: Either<Emoji, String>,
    val url: String,
) : DiscordComponent<ButtonInteractionEvent> {
    override fun getComponentId(): String {
        return "none"
    }

    override fun build(): ActionRowChildComponent {
        return emoji.map {
            Button.link(url, it)
        }.getOrElse { Button.link(url, it) }
    }

    override fun onTrigger(event: ButtonInteractionEvent) {
        // Do nothing
    }
}