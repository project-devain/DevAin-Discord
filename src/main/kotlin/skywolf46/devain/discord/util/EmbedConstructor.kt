package skywolf46.devain.discord.util

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.interactions.InteractionHook
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.requests.restaction.MessageEditAction
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction
import skywolf46.devain.discord.data.components.DiscordComponent
import skywolf46.devain.discord.data.lifecycle.Lifespan
import java.awt.Color

class EmbedConstructor(val lifespan: Lifespan) {
    private val builder: EmbedBuilder = EmbedBuilder()
    private val builtComponents = mutableListOf<List<DiscordComponent<*>>>()

    fun withTitle(title: String): EmbedConstructor {
        builder.setTitle(title)
        return this
    }

    fun withDescription(description: String): EmbedConstructor {
        builder.setDescription(description)
        return this
    }

    fun withField(name: String, value: String, inline: Boolean = false): EmbedConstructor {
        builder.addField(name, value, inline)
        return this
    }

    fun withBlankField(inline: Boolean = false): EmbedConstructor {
        builder.addBlankField(inline)
        return this
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

    fun withComponentRow(vararg component: DiscordComponent<*>): EmbedConstructor {
        builtComponents.add(component.toList())
        return this
    }

    fun reply(hook: InteractionHook) : WebhookMessageCreateAction<Message>{
        return hook.sendMessageEmbeds(builder.build()).apply {
            addComponents(builtComponents.map {
                ActionRow.of(
                    *it.map { component -> component.build() }.toTypedArray()
                )
            })
        }
    }

    fun modify(message: Message) : MessageEditAction {
        return message.editMessageComponents(builtComponents.map {
            ActionRow.of(
                *it.map { component -> component.build() }.toTypedArray()
            )
        })
    }
}