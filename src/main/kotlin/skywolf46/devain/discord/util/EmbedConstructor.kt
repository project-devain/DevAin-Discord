package skywolf46.devain.discord.util

import arrow.core.Option
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.MessageEmbed
import java.awt.Color

class EmbedConstructor(placeholder: Map<String, String> = emptyMap()) {
    private val builder: EmbedBuilder = EmbedBuilder()
    private val placeholder = placeholder.toMutableMap()

    fun addPlaceholder(key: String, value: String): EmbedConstructor {
        placeholder[key] = value
        return this
    }

    fun withTitle(title: String): EmbedConstructor {
        builder.setTitle(title)
        return this
    }

    fun withDescription(description: String): EmbedConstructor {
        builder.setDescription(description)
        return this
    }

    fun withField(name: String, value: String): EmbedConstructor {
        builder.addField(name.replaceAllArgument(placeholder), value.replaceAllArgument(placeholder), false)
        return this
    }


    fun withInlineField(name: String, value: String): EmbedConstructor {
        builder.addField(name.replaceAllArgument(placeholder), value.replaceAllArgument(placeholder), true)
        return this
    }

    fun withBlankField(inline: Boolean = false): EmbedConstructor {
        builder.addBlankField(inline)
        return this
    }

    fun <T : Any> withNullableField(option: Option<T>, title: String, mapper: (T) -> String): EmbedConstructor {
        if (option.isNone())
            return this
        return withField(title, mapper(option.getOrNull()!!))
    }


    fun <T : Any> withNullableInlineField(option: Option<T>, title: String, mapper: (T) -> String): EmbedConstructor {
        if (option.isNone())
            return this
        return withInlineField(title, mapper(option.getOrNull()!!))
    }

    fun withPredicateField(option: Option<Boolean>, title: String, mapper: () -> String) : EmbedConstructor {
        if (option.getOrNull() != true)
            return this
        return withField(title, mapper())
    }

    fun withPredicateInlineField(option: Option<Boolean>, title: String, mapper: () -> String) : EmbedConstructor {
        if (option.getOrNull() != true)
            return this
        return withInlineField(title, mapper())
    }

    fun withFooter(text: String, iconUrl: String? = null): EmbedConstructor {
        builder.setFooter(text, iconUrl)
        return this
    }

    fun withImage(url: String): EmbedConstructor {
        builder.setImage(url)
        return this
    }

    fun withThumbnail(url: String): EmbedConstructor {
        builder.setThumbnail(url)
        return this
    }

    fun withAuthor(name: String, url: String? = null, iconUrl: String? = null): EmbedConstructor {
        builder.setAuthor(name, url, iconUrl)
        return this
    }

    fun withColor(r: Int, g: Int, b: Int): EmbedConstructor {
        builder.setColor(Color(r, g, b))
        return this
    }

    fun withColorCode(code: String): EmbedConstructor {
        builder.setColor(Color.decode(code))
        return this
    }

    fun construct() : MessageEmbed{
        return builder.build()
    }

    fun appendField(message: String): EmbedConstructor {
        return withField("Message", message)
    }
}