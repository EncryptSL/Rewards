package com.github.encryptsl.rewards.common.hook

import com.github.encryptsl.rewards.Rewards
import com.github.encryptsl.rewards.common.hook.discordsrv.DiscordSrvHook

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
        if (isPluginInstalled("DiscordSRV")) {
            rewards.logger.info("###################################")
            rewards.logger.info("#         DiscordSRV Found        #")
            rewards.logger.info("#      You can now use discord    #")
            rewards.logger.info("#          linked rewards         #")
            rewards.logger.info("###################################")
            DiscordSrvHook(rewards).setupDiscordSrv()
        } else {
            rewards.logger.info("###################################")
            rewards.logger.info("#      DiscordSRV not Found       #")
            rewards.logger.info("###################################")
        }
    }

}