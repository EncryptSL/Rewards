package com.github.encryptsl.rewards.common.hook

import com.github.encryptsl.rewards.Rewards
import com.github.encryptsl.rewards.common.hook.discordsrv.DiscordSrvHook
import com.github.encryptsl.rewards.common.hook.kira.KiraDiscordHook

class HookManager(private val rewards: Rewards) {

    fun hookPlugins() {
        DiscordSrvHook(rewards).runIfSuccess {
            rewards.logger.info("DiscordSRV found, now you can use discord rewards.")
        }.runIfElse {
            rewards.logger.info("DiscordSRV not found, you can't use discord rewards.")
        }
        KiraDiscordHook(rewards).runIfSuccess {
            rewards.logger.info("KiraDiscord found, now you can use discord rewards.")
        }.runIfElse {
            rewards.logger.info("KiraDiscord not found, you can't use discord rewards.")
        }
    }
}