package com.github.encryptsl.rewards.common.hook.kira

import com.github.encryptsl.kira.KiraPlugin
import com.github.encryptsl.rewards.Rewards
import org.bukkit.entity.Player

class KiraDiscordHook(private val rewards: Rewards) {

    fun setupKiraDiscord(): Boolean
    {
        return try {
            Class.forName("github.scarsz.discordsrv.DiscordSRV")
            true
        } catch (e : ClassNotFoundException) {
            false
        }
    }

    fun isLinked(player: Player): Boolean
    {
        if (!setupKiraDiscord())
            throw KiraDiscordException(rewards.locale.getMessage("messages.plugin.discordsrv-missing"))

        return KiraPlugin().getAPI().isPlayerLinked(player.uniqueId)
    }

}