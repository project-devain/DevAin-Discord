package skywolf46.devain.discord.util

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.Event
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction
import skywolf46.devain.discord.MessageListenerLifespanManager
import skywolf46.devain.discord.data.components.DiscordComponent
import skywolf46.devain.discord.data.components.DiscordComponentRow
import skywolf46.devain.discord.data.lifecycle.Lifespan
import skywolf46.devain.discord.listeners.ActionListenerContainer

fun Message.queueEditComponents(
    listenerLifespan: Lifespan,
    removeIfExpired: Boolean = false,
    vararg components: DiscordComponentRow,
    callback: (Either<Throwable, Message>) -> Unit = {
        it.onLeft { exception -> exception.printStackTrace() }
    },
    lifespanManager: MessageListenerLifespanManager<Long> = MessageListenerLifespanManager.SHARED,
    actionListenerContainer: ActionListenerContainer = ActionListenerContainer.SHARED
) {
    editMessageComponents(components.map { it.asActionRow() }).queue({
        addObserver(
            idLong,
            listenerLifespan,
            if (removeIfExpired) it else null,
            components.toList(),
            actionListenerContainer,
            lifespanManager
        )

        callback(it.right())
    }, {
        actionListenerContainer.removeListener(idLong)
        lifespanManager.removeLifespanObserver(idLong)
        callback(it.left())
    })
}

fun Message.queueEditComponents(
    listenerLifespan: Lifespan,
    removeIfExpired: Boolean = false,
    vararg components: DiscordComponentRow,
    callback: (Either<Throwable, Message>) -> Unit = {
        it.onLeft { exception -> exception.printStackTrace() }
    }
) {
    queueEditComponents(
        listenerLifespan,
        removeIfExpired,
        components = components,
        callback = callback,
        lifespanManager = MessageListenerLifespanManager.SHARED,
        actionListenerContainer = ActionListenerContainer.SHARED
    )
}

fun Message.queueEditComponents(
    listenerLifespan: Lifespan,
    removeIfExpired: Boolean = false,
    vararg components: DiscordComponent<*>,
    callback: (Either<Throwable, Message>) -> Unit = {
        it.onLeft { exception -> exception.printStackTrace() }
    },
    lifespanManager: MessageListenerLifespanManager<Long> = MessageListenerLifespanManager.SHARED,
    actionListenerContainer: ActionListenerContainer = ActionListenerContainer.SHARED
) {
    queueEditComponents(
        listenerLifespan,
        removeIfExpired,
        DiscordComponentRow.of(*components),
        callback = callback,
        lifespanManager = lifespanManager,
        actionListenerContainer = actionListenerContainer
    )
}

fun Message.queueEditComponents(
    listenerLifespan: Lifespan,
    removeIfExpired: Boolean = false,
    vararg components: DiscordComponent<*>,
    callback: (Either<Throwable, Message>) -> Unit = {
        it.onLeft { exception -> exception.printStackTrace() }
    }
) {
    queueEditComponents(
        listenerLifespan,
        removeIfExpired,
        components = components,
        callback = callback,
        lifespanManager = MessageListenerLifespanManager.SHARED,
        actionListenerContainer = ActionListenerContainer.SHARED
    )
}


fun Message.queueReplyComponents(
    listenerLifespan: Lifespan,
    removeIfExpired: Boolean = false,
    vararg components: DiscordComponentRow,
    callback: (Either<Throwable, Message>) -> Unit = {
        it.onLeft { exception -> exception.printStackTrace() }
    },
    lifespanManager: MessageListenerLifespanManager<Long> = MessageListenerLifespanManager.SHARED,
    actionListenerContainer: ActionListenerContainer = ActionListenerContainer.SHARED
) {

    replyComponents(components.map { it.asActionRow() }).queue({
        addObserver(
            idLong,
            listenerLifespan,
            if (removeIfExpired) it else null,
            components.toList(),
            actionListenerContainer,
            lifespanManager
        )
        callback(it.right())
    }, {
        actionListenerContainer.removeListener(idLong)
        lifespanManager.removeLifespanObserver(idLong)
        callback(it.left())
    })
}

fun Message.queueReplyComponents(
    listenerLifespan: Lifespan,
    removeIfExpired: Boolean = false,
    vararg components: DiscordComponentRow,
    callback: (Either<Throwable, Message>) -> Unit = {
        it.onLeft { exception -> exception.printStackTrace() }
    }
) {
    queueReplyComponents(
        listenerLifespan,
        removeIfExpired,
        components = components,
        callback = callback,
        lifespanManager = MessageListenerLifespanManager.SHARED,
        actionListenerContainer = ActionListenerContainer.SHARED
    )
}

fun Message.queueReplyComponents(
    listenerLifespan: Lifespan,
    removeIfExpired: Boolean = false,
    vararg components: DiscordComponent<*>,
    callback: (Either<Throwable, Message>) -> Unit = {
        it.onLeft { exception -> exception.printStackTrace() }
    },
    lifespanManager: MessageListenerLifespanManager<Long> = MessageListenerLifespanManager.SHARED,
    actionListenerContainer: ActionListenerContainer = ActionListenerContainer.SHARED
) {
    queueReplyComponents(
        listenerLifespan,
        removeIfExpired,
        DiscordComponentRow.of(*components),
        callback = callback,
        lifespanManager = lifespanManager,
        actionListenerContainer = actionListenerContainer
    )
}

fun Message.queueReplyComponents(
    listenerLifespan: Lifespan,
    removeIfExpired: Boolean = false,
    vararg components: DiscordComponent<*>,
    callback: (Either<Throwable, Message>) -> Unit = {
        it.onLeft { exception -> exception.printStackTrace() }
    }
) {
    queueReplyComponents(
        listenerLifespan,
        removeIfExpired,
        components = components,
        callback = callback,
        lifespanManager = MessageListenerLifespanManager.SHARED,
        actionListenerContainer = ActionListenerContainer.SHARED
    )
}

fun WebhookMessageCreateAction<Message>.queueComponents(
    listenerLifespan: Lifespan,
    removeIfExpired: Boolean = false,
    vararg components: DiscordComponentRow,
    callback: (Either<Throwable, Message>) -> Unit = {
        it.onLeft { exception -> exception.printStackTrace() }
    },
    lifespanManager: MessageListenerLifespanManager<Long> = MessageListenerLifespanManager.SHARED,
    actionListenerContainer: ActionListenerContainer = ActionListenerContainer.SHARED
) {
    addComponents(components.map { it.asActionRow() }).queue({
        addObserver(
            it.idLong,
            listenerLifespan,
            if (removeIfExpired) it else null,
            components.toList(),
            actionListenerContainer,
            lifespanManager
        )
        callback(it.right())
    }, {
        callback(it.left())
    })
}

fun WebhookMessageCreateAction<Message>.queueComponents(
    listenerLifespan: Lifespan,
    removeIfExpired: Boolean = false,
    vararg components: DiscordComponentRow,
    callback: (Either<Throwable, Message>) -> Unit = {
        it.onLeft { exception -> exception.printStackTrace() }
    }
) {
    queueComponents(
        listenerLifespan,
        removeIfExpired,
        components = components,
        callback = callback,
        lifespanManager = MessageListenerLifespanManager.SHARED,
        actionListenerContainer = ActionListenerContainer.SHARED
    )
}

fun WebhookMessageCreateAction<Message>.queueComponents(
    listenerLifespan: Lifespan,
    removeIfExpired: Boolean = false,
    vararg components: DiscordComponent<*>,
    callback: (Either<Throwable, Message>) -> Unit = {
        it.onLeft { exception -> exception.printStackTrace() }
    },
    lifespanManager: MessageListenerLifespanManager<Long> = MessageListenerLifespanManager.SHARED,
    actionListenerContainer: ActionListenerContainer = ActionListenerContainer.SHARED
) {
    queueComponents(
        listenerLifespan,
        removeIfExpired,
        DiscordComponentRow.of(*components),
        callback = callback,
        lifespanManager = lifespanManager,
        actionListenerContainer = actionListenerContainer
    )
}

fun WebhookMessageCreateAction<Message>.queueComponents(
    listenerLifespan: Lifespan,
    removeIfExpired: Boolean = false,
    vararg components: DiscordComponent<*>,
    callback: (Either<Throwable, Message>) -> Unit = {
        it.onLeft { exception -> exception.printStackTrace() }
    }
) {
    queueComponents(
        listenerLifespan,
        removeIfExpired,
        components = components,
        callback = callback,
        lifespanManager = MessageListenerLifespanManager.SHARED,
        actionListenerContainer = ActionListenerContainer.SHARED
    )
}


fun ReplyCallbackAction.queueComponents(
    listenerLifespan: Lifespan,
    removeIfExpired: Boolean = false,
    vararg components: DiscordComponentRow,
    callback: (Either<Throwable, InteractionHook>) -> Unit = {
        it.onLeft { exception -> exception.printStackTrace() }
    },
    lifespanManager: MessageListenerLifespanManager<Long> = MessageListenerLifespanManager.SHARED,
    actionListenerContainer: ActionListenerContainer = ActionListenerContainer.SHARED
) {
    addComponents(components.map { it.asActionRow() }).queue({
        it.retrieveOriginal().queue { message ->
            addObserver(
                message.idLong,
                listenerLifespan,
                if (removeIfExpired) message else null,
                components.toList(),
                actionListenerContainer,
                lifespanManager
            )
        }
        callback(it.right())
    }, {
        callback(it.left())
    })
}

fun ReplyCallbackAction.queueComponents(
    listenerLifespan: Lifespan,
    removeIfExpired: Boolean = false,
    vararg components: DiscordComponentRow,
    callback: (Either<Throwable, InteractionHook>) -> Unit = {
        it.onLeft { exception -> exception.printStackTrace() }
    }
) {
    queueComponents(
        listenerLifespan,
        removeIfExpired,
        components = components,
        callback = callback,
        lifespanManager = MessageListenerLifespanManager.SHARED,
        actionListenerContainer = ActionListenerContainer.SHARED
    )
}

fun ReplyCallbackAction.queueComponents(
    listenerLifespan: Lifespan,
    removeIfExpired: Boolean = false,
    vararg components: DiscordComponent<*>,
    callback: (Either<Throwable, InteractionHook>) -> Unit = {
        it.onLeft { exception -> exception.printStackTrace() }
    },
    lifespanManager: MessageListenerLifespanManager<Long> = MessageListenerLifespanManager.SHARED,
    actionListenerContainer: ActionListenerContainer = ActionListenerContainer.SHARED
) {
    queueComponents(
        listenerLifespan,
        removeIfExpired,
        DiscordComponentRow.of(*components),
        callback = callback,
        lifespanManager = lifespanManager,
        actionListenerContainer = actionListenerContainer
    )
}

fun ReplyCallbackAction.queueComponents(
    listenerLifespan: Lifespan,
    removeIfExpired: Boolean = false,
    vararg components: DiscordComponent<*>,
    callback: (Either<Throwable, InteractionHook>) -> Unit = {
        it.onLeft { exception -> exception.printStackTrace() }
    }
) {
    queueComponents(
        listenerLifespan,
        removeIfExpired,
        components = components,
        callback = callback,
        lifespanManager = MessageListenerLifespanManager.SHARED,
        actionListenerContainer = ActionListenerContainer.SHARED
    )
}

private fun addObserver(
    id: Long,
    lifeSpan: Lifespan,
    message: Message?,
    components: List<DiscordComponentRow>,
    actionListenerContainer: ActionListenerContainer,
    lifespanManager: MessageListenerLifespanManager<Long>
) {
    val listener =
        ActionListenerContainer.ActionListenersWrapper(components.flatMap { it.components as List<DiscordComponent<Event>> }
            .associate { component ->
                component.getComponentId() to { component.onTrigger(it as Event) }
            })

    actionListenerContainer.addListener(id, listener)
    lifespanManager.replaceLifespanObserver(id, lifeSpan) {
        actionListenerContainer.removeListener(id)
        message?.delete()?.queue()
    }

}

private fun addObserver(
    id: Long,
    lifeSpan: Lifespan,
    message: InteractionHook?,
    components: List<DiscordComponentRow>,
    actionListenerContainer: ActionListenerContainer,
    lifespanManager: MessageListenerLifespanManager<Long>
) {
    val listener =
        ActionListenerContainer.ActionListenersWrapper(components.flatMap { it.components as List<DiscordComponent<Event>> }
            .associate { component ->
                component.getComponentId() to { component.onTrigger(it as Event) }
            })

    actionListenerContainer.addListener(id, listener)
    lifespanManager.replaceLifespanObserver(id, lifeSpan) {
        actionListenerContainer.removeListener(id)
        message?.deleteOriginal()?.queue()
    }

}