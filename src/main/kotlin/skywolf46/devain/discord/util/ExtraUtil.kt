package skywolf46.devain.discord.util

fun <T: Any> T.mapTrue(boolean: Boolean, unit: () -> T) : T{
    if (boolean)
        return unit()
    return this
}

fun <T: Any> T.mapFalse(boolean: Boolean, unit: () -> T) : T{
    if (!boolean)
        return unit()
    return this
}