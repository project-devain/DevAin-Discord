package skywolf46.devain.discord.util

import arrow.core.Either
import arrow.core.identity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback

@OptIn(DelicateCoroutinesApi::class)
fun <T: IReplyCallback> T.deferEmbed(
    isEphemeral: Boolean = false,
    unit: suspend (event: T, hook: InteractionHook) -> Either<String, MessageEmbed>
) {
    deferReply(isEphemeral).queue { hook ->
        GlobalScope.launch(Dispatchers.Default) {
            unit(this@deferEmbed, hook).onLeft {
                hook.sendMessage(it).queue()
            }.onRight {
                hook.sendMessageEmbeds(it).queue()
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun <T: IReplyCallback> T.deferMessage(
    isEphemeral: Boolean = false,
    unit: suspend (event: T, hook: InteractionHook) -> Either<String, String>
) {
    deferReply(isEphemeral).queue { hook ->
        GlobalScope.launch(Dispatchers.Default) {
            hook.sendMessage(unit(this@deferMessage, hook).fold(::identity, ::identity)).queue()
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun <T: IReplyCallback> T.deferError(
    isEphemeral: Boolean = false,
    unit: suspend (event: T, hook: InteractionHook) -> Either<String, Unit>
) {
    deferReply(isEphemeral).queue { hook ->
        GlobalScope.launch(Dispatchers.Default) {
            unit(this@deferError, hook).onLeft {
                hook.sendMessage(it).queue()
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun <T: IReplyCallback> T.defer(
    isEphemeral: Boolean = false,
    unit: suspend (event: T, hook: InteractionHook) -> Unit
) {
    deferReply(isEphemeral).queue { hook ->
        GlobalScope.launch(Dispatchers.Default) {
            unit(this@defer, hook)
        }
    }
}