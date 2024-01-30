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

    fun hookKiraDiscord() {
        if (isPluginInstalled("KiraDiscord")) {
            rewards.logger.info("###################################")
            rewards.logger.info("#         KiraDiscord Found       #")
            rewards.logger.info("#      You can now use discord    #")
            rewards.logger.info("#          linked rewards         #")
            rewards.logger.info("###################################")
            KiraDiscordHook(rewards).setupKiraDiscord()
        } else {
            rewards.logger.info("###################################")
            rewards.logger.info("#     KiraDiscord not Found       #")
            rewards.logger.info("###################################")
        }
    }

}