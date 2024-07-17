package skywolf46.devain.discord.data.lifecycle

import java.util.concurrent.TimeUnit

data class Lifespan(val expiration: Long) {
    companion object {
        val FOREVER = Lifespan(-1L)

        fun seconds(sec: Long) = Lifespan(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(sec))

        fun minutes(min: Long) = Lifespan(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(min))

        fun hours(hour: Long) = Lifespan(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(hour))

        fun days(day: Long) = Lifespan(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(day))
    }

    fun isExpired() = expiration != -1L && System.currentTimeMillis() >= expiration
}