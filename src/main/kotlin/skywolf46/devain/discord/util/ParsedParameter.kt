package skywolf46.devain.discord.util

import arrow.core.Option
import arrow.core.none
import arrow.core.toOption
import skywolf46.devain.discord.annotations.CommandParameter
import skywolf46.devain.discord.annotations.Required
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.valueParameters

class ParsedParameter (val cls: KClass<*>){
    val parameters = mutableMapOf<KParameter, ParsedParameterInfo>()

    init {
        verify().onSome {
            throw RuntimeException(it)
        }
        inspect()
    }

    fun verify(): Option<String> {
        for (field in cls.constructors.first().valueParameters) {
            if (!field.isOptional) {
                if (field.findAnnotations(CommandParameter::class).isEmpty()) {
                    return "Cannot use field \"${field.name}\" as parameter constructor : Non-optional field must have @Require and @CommandParameter annotation".toOption()
                }
                if (field.findAnnotations(Required::class).isEmpty()) {
                    return "Cannot use field \"${field.name}\" as parameter constructor : Non-optional field must have @Require and @CommandParameter annotation".toOption()
                }
            }
        }
        return none()
    }

    fun inspect() {
        for (field in cls.constructors.first().valueParameters) {
            val data = field.findAnnotations(CommandParameter::class).firstOrNull()
            if (data != null) {
                parameters[field] = ParsedParameterInfo(
                    field.findAnnotations(Required::class).isNotEmpty(), when (field.type.classifier as KClass<*>) {
                        Long::class -> OptionClassConverter.LONG
                        Double::class -> OptionClassConverter.DOUBLE
                        Float::class -> OptionClassConverter.FLOAT
                        String::class -> OptionClassConverter.STRING
                        Boolean::class -> OptionClassConverter.BOOLEAN
                        Int::class -> OptionClassConverter.INTEGER
                        else -> throw IllegalStateException("Class ${field.type.classifier} is unexpected for command parameter")
                    }, data.name, data.description
                )
            }
        }
    }

    data class ParsedParameterInfo(
        val required: Boolean, val type: OptionClassConverter, val name: String, val description: String
    )
}