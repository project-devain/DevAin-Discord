package skywolf46.devain.discord.command

import arrow.core.Either
import arrow.core.identity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import org.koin.core.component.KoinComponent
import skywolf46.devain.discord.data.CommandType
import skywolf46.devain.discord.data.PredefinedCommandData

/**
 * 기초적인 명령어 구현을 위한 유틸리티 추상 클래스, BasicDiscordCommand입니다.
 *
 * 이 클래스는 복잡한 JDA의 명령어 구현 대신, 추상 클래스를 이용하여 빠른 명령어 구현을 지원합니다.
 *
 * 명령어 구현에 대한 간단한 내용은 [skywolf46.devain.discord.test.BasicHelloWorldCommand]를 참고하세요.
 */
abstract class BasicDiscordCommand : KoinComponent, CommandProvider {
    @OptIn(DelicateCoroutinesApi::class)
    fun triggerCommand(event: SlashCommandInteractionEvent) {
        GlobalScope.launch(Dispatchers.Default) {
            onCommand(event)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun triggerAutoComplete(event: CommandAutoCompleteInteractionEvent) {
        GlobalScope.launch(Dispatchers.Default) {
            onAutoComplete(event)
        }
    }

    protected open suspend fun onCommand(event: SlashCommandInteractionEvent) {

    }

    protected open suspend fun onAutoComplete(event: CommandAutoCompleteInteractionEvent) {

    }

    open fun getCommandType(): CommandType = CommandType.HYBRID

}