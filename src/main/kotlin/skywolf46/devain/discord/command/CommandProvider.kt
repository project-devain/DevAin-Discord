package skywolf46.devain.discord.command

import skywolf46.devain.discord.data.PredefinedCommandData

interface CommandProvider {
    fun provideCommands(): List<PredefinedCommandData>
}