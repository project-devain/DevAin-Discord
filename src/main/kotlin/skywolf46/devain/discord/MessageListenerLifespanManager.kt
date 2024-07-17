package skywolf46.devain.discord

import arrow.atomic.AtomicBoolean
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import skywolf46.devain.discord.data.lifecycle.Lifespan
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class MessageListenerLifespanManager<KEY: Any> {
    companion object {
        val SHARED = create<Long>().apply {
            startMonitor()
        }

        fun <T: Any> create(): MessageListenerLifespanManager<T> {
            return MessageListenerLifespanManager()
        }
    }
    val workScope = CoroutineScope(SupervisorJob() + Dispatchers.IO.limitedParallelism(1))

    val unregisterScope = CoroutineScope(SupervisorJob() + Dispatchers.Default.limitedParallelism(1))

    private val shutdown = AtomicBoolean(false)

    private val list = mutableMapOf<KEY, LifespanData>()

    private val lock = ReentrantLock()

    fun startMonitor() {
        val unregisterTransporter = Channel<List<LifespanData>>()

        workScope.launch {
            while (!shutdown.get()) {
                val expired = mutableListOf<LifespanData>()
                lock.withLock {
                    val iterator = list.iterator()
                    while (iterator.hasNext()) {
                        val next = iterator.next()
                        if (next.value.lifespan.isExpired()) {
                            iterator.remove()
                            expired.add(next.value)
                        }
                    }
                }
                if (expired.isNotEmpty()) unregisterTransporter.send(expired)
                delay(500L)
            }
        }

        unregisterScope.launch {
            while (!shutdown.get()) {
                val data = unregisterTransporter.receive()
                for (x in data) {
                    x.unregistrar()
                }
                delay(500L)
            }
        }
    }

    fun replaceLifespanObserver(
        id: KEY, lifespan: Lifespan, unregistrar: () -> Unit
    ) {
        lock.withLock {
            list[id] = LifespanData(lifespan, unregistrar)
        }
    }

    fun removeLifespanObserver(id: KEY) {
        lock.withLock {
            list.remove(id)
        }
    }



    data class LifespanData(
        val lifespan: Lifespan, val unregistrar: () -> Unit
    )
}