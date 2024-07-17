package skywolf46.devain.discord.data.components

import arrow.core.None
import arrow.core.Option
import arrow.core.getOrElse
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.components.ItemComponent
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu

class SelectionComponent(
    val id: String,
    val list: List<SelectionValue>,
    val selectRange: IntRange = 1..1,
    val onClick: StringSelectInteractionEvent.() -> Unit
) : DiscordComponent<StringSelectInteractionEvent> {
    override fun build(): ItemComponent {
        return StringSelectMenu.create(getComponentId()).setRequiredRange(
            selectRange.first, selectRange.last
        ).addOptions(list.map { it.asSelectOption() }).build()
    }

    override fun getComponentId(): String {
        return id
    }

    override fun onTrigger(event: StringSelectInteractionEvent) {
        onClick(event)
    }

    data class SelectionValue(
        val key: String,
        val value: String,
        val description: Option<String> = None,
        val emoji: Option<Emoji> = None,
        val isDefault: Boolean = false
    ) {
        fun asSelectOption(): SelectOption {
            return SelectOption.of(value, key).applyDescription().applyEmoji().applyDescription()
        }

        private fun SelectOption.applyDescription(): SelectOption {
            return this@SelectionValue.description.map { withDescription(it) }.getOrElse { this }
        }

        private fun SelectOption.applyEmoji(): SelectOption {
            return this@SelectionValue.emoji.map { withEmoji(it) }.getOrElse { this }
        }

        private fun SelectOption.applyDefault(): SelectOption {
            return if (this@SelectionValue.isDefault) withDefault(true) else withDefault(false)
        }


    }

}