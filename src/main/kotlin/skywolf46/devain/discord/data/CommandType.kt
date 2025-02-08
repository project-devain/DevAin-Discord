package skywolf46.devain.discord.data

import net.dv8tion.jda.api.interactions.IntegrationType

enum class CommandType(vararg val type: IntegrationType) {
    USER(IntegrationType.USER_INSTALL),
    GUILD(IntegrationType.GUILD_INSTALL),
    HYBRID(IntegrationType.USER_INSTALL, IntegrationType.GUILD_INSTALL)
}