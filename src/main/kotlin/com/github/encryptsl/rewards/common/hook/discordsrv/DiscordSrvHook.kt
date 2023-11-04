package com.github.encryptsl.rewards.common.hook.discordsrv

import com.github.encryptsl.rewards.Rewards
import github.scarsz.discordsrv.DiscordSRV
import github.scarsz.discordsrv.objects.managers.AccountLinkManager
import org.bukkit.entity.Player

class DiscordSrvHook(private val rewards: Rewards) {


    fun setupDiscordSrv(): Boolean
    {
        return rewards.pluginManager.getPlugin("DiscordSRV") != null
    }

    fun isLinked(player: Player): Boolean {
        if (setupDiscordSrv())
            throw DiscordSrvException(rewards.locale.getMessage(""))

        val accountLinkedManager: AccountLinkManager = DiscordSRV.getPlugin().accountLinkManager

        return accountLinkedManager.getDiscordId(player.uniqueId) != null
    }

}