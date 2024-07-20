package skywolf46.devain.discord.annotations

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class ParameterDoubleRange(
    val rangeStart: Double,
    val rangeEnd: Double,
    val fatalMessage: String = "Invalid range detected. Expected range is %d ~ %d"
)