package skywolf46.devain.discord.annotations

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandParameter(val name: String, val description: String)