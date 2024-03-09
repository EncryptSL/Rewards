package com.github.encryptsl.rewards.common.hook

import com.github.encryptsl.rewards.Rewards
import com.github.encryptsl.rewards.common.hook.discordsrv.DiscordSrvHook
import com.github.encryptsl.rewards.common.hook.kira.KiraDiscordHook

class HookManager(private val rewards: Rewards) {

    /**
     * Method for check if plugin is installed
     * @param pluginName - String name of plugin is CaseSensitive
     * @return Boolean
     */
    private fun isPluginInstalled(pluginName: String): Boolean {
        return rewards.pluginManager.getPlugin(pluginName) != null
    }


    fun hookDiscordSrv() {
        if (isPluginInstalled("DiscordSRV") && DiscordSrvHook(rewards).setupDiscordSrv()) {
            rewards.logger.info("DiscordSRV found, now you can use discord rewards.")
        } else {
            rewards.logger.info("DiscordSRV not found, you can't use discord rewards.")
        }
    }

    fun hookKiraDiscord() {
        if (isPluginInstalled("KiraDiscord") && KiraDiscordHook(rewards).setupKiraDiscord()) {
            rewards.logger.info("KiraDiscord found, now you can use discord rewards.")
        } else {
            rewards.logger.info("KiraDiscord not found, you can't use discord rewards.")
        }
    }

}