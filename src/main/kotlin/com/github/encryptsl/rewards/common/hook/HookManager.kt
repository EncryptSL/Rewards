package com.github.encryptsl.rewards.common.hook

import com.github.encryptsl.rewards.Rewards
import com.github.encryptsl.rewards.common.hook.discordsrv.DiscordSrvHook
import com.github.encryptsl.rewards.common.hook.kira.KiraDiscordHook

class HookManager(private val rewards: Rewards) {
    fun hookDiscordSrv() {
        if (DiscordSrvHook(rewards).setupDiscordSrv()) {
            rewards.logger.info("DiscordSRV found, now you can use discord rewards.")
        } else {
            rewards.logger.info("DiscordSRV not found, you can't use discord rewards.")
        }
    }

    fun hookKiraDiscord() {
        if (KiraDiscordHook(rewards).setupKiraDiscord()) {
            rewards.logger.info("KiraDiscord found, now you can use discord rewards.")
        } else {
            rewards.logger.info("KiraDiscord not found, you can't use discord rewards.")
        }
    }

}