package skywolf46.devain.discord.data.components

import net.dv8tion.jda.api.components.actionrow.ActionRowChildComponent
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.buttons.ButtonStyle
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
data class TextButton(
    val id: String,
    val label: String,
    val style: ButtonStyle,
    val emoji: Emoji? = null,
    val onClick: ButtonInteractionEvent.() -> Unit
) : DiscordComponent<ButtonInteractionEvent> {
    override fun getComponentId(): String {
        return id
    }

    override fun build(): ActionRowChildComponent {
        return if (emoji == null) Button.of(style, getComponentId(), label) else Button.of(
            style, getComponentId(), label, emoji
        )
    }

    override fun onTrigger(event: ButtonInteractionEvent) {
        onClick(event)
    }
}