package com.github.encryptsl.rewards.common.hook.kira

import com.github.encryptsl.kira.api.KiraAPI
import com.github.encryptsl.rewards.Rewards
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class KiraDiscordHook(private val rewards: Rewards) {

    private val pluginName = "KiraDiscord"

    fun setupKiraDiscord(): Boolean
    {
        return try {
            Class.forName("com.github.encryptsl.kira.KiraDiscord")
            true
        } catch (e : ClassNotFoundException) {
            false
        }
    }

    fun isLinked(player: Player): Boolean
    {
        if (!setupKiraDiscord())
            throw KiraDiscordException(rewards.locale.getMessage("messages.plugin.dependency-missing").replace("<dependency>", pluginName))

        val plugin = Bukkit.getPluginManager().getPlugin(pluginName)!!

        return KiraAPI(plugin).isPlayerLinked(player.uniqueId)
    }

}