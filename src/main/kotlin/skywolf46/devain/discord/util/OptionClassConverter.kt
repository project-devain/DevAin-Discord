package skywolf46.devain.discord.util

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType

enum class OptionClassConverter(
    val option: OptionType, val converter: SlashCommandInteractionEvent.(String) -> Any?
) {
    STRING(OptionType.STRING, {
        getOption(it)?.asString
    }),
    LONG(OptionType.INTEGER, {
        getOption(it)?.asLong
    }),
    BOOLEAN(OptionType.BOOLEAN, {
        getOption(it)?.asBoolean
    }),
    DOUBLE(OptionType.NUMBER, {
        getOption(it)?.asDouble
    }),
    FLOAT(OptionType.NUMBER, {
        getOption(it)?.asDouble?.toFloat()
    }),
    INTEGER(OptionType.INTEGER, {
        getOption(it)?.asInt
    }),
    ATTACHMENT(OptionType.ATTACHMENT, {
        getOption(it)?.asAttachment
    })
}