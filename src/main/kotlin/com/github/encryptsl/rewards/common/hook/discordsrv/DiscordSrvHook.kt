package com.github.encryptsl.rewards.common.hook.discordsrv

import com.github.encryptsl.kmono.lib.api.hook.PluginHook
import com.github.encryptsl.rewards.Rewards
import github.scarsz.discordsrv.DiscordSRV
import github.scarsz.discordsrv.objects.managers.AccountLinkManager
import org.bukkit.entity.Player

class DiscordSrvHook(private val rewards: Rewards) : PluginHook("DiscordSRV") {

    private val pluginName = "DiscordSRV"

    fun isLinked(player: Player): Boolean {
        if (!isPluginEnabled())
            throw DiscordSrvException(rewards.locale.getMessage("messages.plugin.dependency-missing").replace("<dependency>", pluginName) )

        val accountLinkedManager: AccountLinkManager = DiscordSRV.getPlugin().accountLinkManager ?: throw DiscordSrvException(rewards.locale.getMessage("messages.plugin.dependency-missing").replace("<dependency>", pluginName))

        return accountLinkedManager.getDiscordId(player.uniqueId) != null
    }

}