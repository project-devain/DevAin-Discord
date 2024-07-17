package skywolf46.devain.discord.data.components

import net.dv8tion.jda.api.interactions.components.ActionRow

data class DiscordComponentRow(val components: List<DiscordComponent<*>>) {
    companion object {
        fun of(vararg components: DiscordComponent<*>) = DiscordComponentRow(components.toList())
    }
    fun asActionRow() = ActionRow.of(components.map { it.build() })
}