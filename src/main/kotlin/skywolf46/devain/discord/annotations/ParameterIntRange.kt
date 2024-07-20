package skywolf46.devain.discord.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class ParameterIntRange(
    val rangeStart: Int,
    val rangeEnd: Int,
    val fatalMessage: String = "Invalid range detected. Expected range is %d ~ %d"
)