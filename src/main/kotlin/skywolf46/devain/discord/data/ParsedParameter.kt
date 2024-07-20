package skywolf46.devain.discord.data

import arrow.core.None
import arrow.core.Option
import arrow.core.none
import arrow.core.toOption
import net.dv8tion.jda.api.entities.Message.Attachment
import skywolf46.devain.discord.annotations.CommandParameter
import skywolf46.devain.discord.annotations.ParameterDoubleRange
import skywolf46.devain.discord.annotations.ParameterIntRange
import skywolf46.devain.discord.annotations.Required
import skywolf46.devain.discord.util.OptionClassConverter
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.valueParameters

class ParsedParameter(val cls: KClass<*>) {
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
                val required = field.findAnnotations(Required::class).isNotEmpty()
                val type = when (field.type.classifier as KClass<*>) {
                    Long::class -> OptionClassConverter.LONG
                    Double::class -> OptionClassConverter.DOUBLE
                    Float::class -> OptionClassConverter.FLOAT
                    String::class -> OptionClassConverter.STRING
                    Boolean::class -> OptionClassConverter.BOOLEAN
                    Int::class -> OptionClassConverter.INTEGER
                    Attachment::class -> OptionClassConverter.ATTACHMENT
                    else -> throw IllegalStateException("Class ${field.type.classifier} is unexpected for command parameter")
                }
                if (field.findAnnotations<ParameterIntRange>().isNotEmpty()) {
                    val range = field.findAnnotations<ParameterIntRange>().first()
                    parameters[field] = IntRangeRestrictedParameter(
                        required,
                        type,
                        data.name,
                        data.description,
                        range.rangeStart,
                        range.rangeEnd,
                        range.fatalMessage
                    )
                    continue
                }
                if (field.findAnnotations<ParameterDoubleRange>().isNotEmpty()) {
                    val range = field.findAnnotations<ParameterDoubleRange>().first()
                    parameters[field] = DoubleRangeRestrictedParameter(
                        required,
                        type,
                        data.name,
                        data.description,
                        range.rangeStart,
                        range.rangeEnd,
                        range.fatalMessage
                    )
                    continue
                }
                parameters[field] = ParsedParameterInfo(
                    required, type, data.name, data.description
                )
            }
        }
    }

    open class ParsedParameterInfo(
        val required: Boolean, val type: OptionClassConverter, val name: String, val description: String
    ) {
        open fun checkRestricted(value: Any?): Option<String> = None
    }

    open class IntRangeRestrictedParameter(
        required: Boolean,
        type: OptionClassConverter,
        name: String,
        description: String,
        val min: Int,
        val max: Int,
        val message: String
    ) : ParsedParameterInfo(required, type, name, description) {
        override fun checkRestricted(value: Any?): Option<String> {
            if(!required && value == null) return None
            if (value !is Int) return "Value is not integer".toOption()
            if (value < min || value > max) return message.toOption()
            return None
        }
    }

    open class DoubleRangeRestrictedParameter(
        required: Boolean,
        type: OptionClassConverter,
        name: String,
        description: String,
        val min: Double,
        val max: Double,
        val message: String
    ) : ParsedParameterInfo(required, type, name, description) {
        override fun checkRestricted(value: Any?): Option<String> {
            if(!required && value == null) return None
            if (value !is Double && value !is Float) return "Value is not double".toOption()
            val valueToCompare = (value as Number).toDouble()
            if (valueToCompare < min || valueToCompare > max) return message.toOption()
            return None
        }
    }
}