package com.github.encryptsl.rewards.common

import com.github.encryptsl.rewards.Rewards

class Locale(private val rewards: Rewards) {

    fun getMessage(key: String): String {
        val prefix = rewards.config.getString("prefix").toString()

        return rewards.config.getString(key).toString().replace("<prefix>", prefix)
    }

}