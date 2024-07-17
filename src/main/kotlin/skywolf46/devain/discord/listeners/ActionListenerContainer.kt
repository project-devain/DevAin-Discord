package skywolf46.devain.discord.listeners

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class ActionListenerContainer {
    companion object {
        val SHARED = ActionListenerContainer()
    }

    private val listeners = mutableMapOf<Long, ActionListenersWrapper>()

    private val lock = ReentrantReadWriteLock()

    fun addListener(id: Long, wrapper: ActionListenersWrapper) {
        lock.write { listeners[id] = wrapper }
    }

    fun removeListener(id: Long) {
        lock.write { listeners.remove(id) }
    }

    fun trigger(id: Long, key: String, data: Any): Boolean {
        return lock.read {
            listeners[id]?.trigger(key, data) ?: false
        }
    }

    data class ActionListenersWrapper(private val map: Map<String, (Any) -> Unit>) {
        fun trigger(key: String, data: Any): Boolean {
            map[key]?.invoke(data) ?: return false
            return true
        }
    }
}