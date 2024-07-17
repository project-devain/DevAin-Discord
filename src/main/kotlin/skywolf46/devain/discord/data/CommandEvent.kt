package skywolf46.devain.discord.data

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

data class CommandEvent(
    val origin: SlashCommandInteractionEvent,
    val executedOn: Long = System.currentTimeMillis()
) {
    fun elapsed(): Long = (System.currentTimeMillis() - executedOn)
}