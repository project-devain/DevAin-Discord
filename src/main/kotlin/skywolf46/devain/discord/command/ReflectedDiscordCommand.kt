package skywolf46.devain.discord.command

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import skywolf46.devain.discord.data.CommandEvent
import skywolf46.devain.discord.util.ParsedParameter
import kotlin.reflect.KClass

abstract class ReflectedDiscordCommand<T : Any>(
    command: String, description: String = "제공된 명령어 설명이 존재하지 않습니다.", val parameterClass: KClass<T>
) : EnhancedDiscordCommand(command, description) {
    private val parsed = ParsedParameter(parameterClass)

    override fun modifyCommandData(options: SlashCommandData) {
        for ((_, data) in parsed.parameters) {
            options.addOption(
                data.type.option,
                data.name,
                data.description.replaceAllArgument(onCommandParameterDataRequested()),
                data.required
            )
        }
    }

    override suspend fun onCommand(event: SlashCommandInteractionEvent) {
        val constructed = parameterClass.constructors.first().callBy(parsed.parameters.mapValues {
            it.value.type.converter(event, it.value.name)
        }.filterValues { it != null })
        onParameterCommand(CommandEvent(event), constructed)
    }

    abstract suspend fun onParameterCommand(event: CommandEvent, data: T)

    open fun onCommandParameterDataRequested(): Map<String, String> = emptyMap()

    private fun String.replaceAllArgument(args: Map<String, String>): String {
        return (listOf(this to "") + args.toList()).reduce { acc, pair ->
            acc.first.replace("{${pair.first}}", pair.second) to ""
        }.first
    }
}

