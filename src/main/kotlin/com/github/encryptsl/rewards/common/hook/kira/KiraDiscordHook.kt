package com.github.encryptsl.rewards.common.hook.kira

import com.github.encryptsl.kira.api.KiraAPI
import com.github.encryptsl.kmono.lib.api.hook.PluginHook
import com.github.encryptsl.rewards.Rewards
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class KiraDiscordHook(private val rewards: Rewards) : PluginHook("KiraDiscord") {

    private val pluginName = "KiraDiscord"

    fun isLinked(player: Player): Boolean
    {
        if (!isPluginEnabled())
            throw KiraDiscordException(rewards.locale.getMessage("messages.plugin.dependency-missing").replace("<dependency>", pluginName))

        val plugin = Bukkit.getPluginManager().getPlugin(pluginName)!!

        return KiraAPI(plugin).isPlayerLinked(player.uniqueId)
    }

}