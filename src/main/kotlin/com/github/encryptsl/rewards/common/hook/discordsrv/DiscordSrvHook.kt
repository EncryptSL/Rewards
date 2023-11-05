package com.github.encryptsl.rewards.common.hook.discordsrv

import com.github.encryptsl.rewards.Rewards
import github.scarsz.discordsrv.DiscordSRV
import github.scarsz.discordsrv.objects.managers.AccountLinkManager
import org.bukkit.entity.Player

class DiscordSrvHook(private val rewards: Rewards) {


    fun setupDiscordSrv(): Boolean
    {
        return try {
            Class.forName("github.scarsz.discordsrv.DiscordSRV")
            true
        } catch (e : ClassNotFoundException) {
            false
        }
    }

    fun isLinked(player: Player): Boolean {
        if (!setupDiscordSrv())
            throw DiscordSrvException(rewards.locale.getMessage("messages.plugin.discordsrv-missing"))

        val accountLinkedManager: AccountLinkManager = DiscordSRV.getPlugin().accountLinkManager ?: throw DiscordSrvException(rewards.locale.getMessage("messages.plugin.discordsrv-missing"))

        return accountLinkedManager.getDiscordId(player.uniqueId) != null
    }

}