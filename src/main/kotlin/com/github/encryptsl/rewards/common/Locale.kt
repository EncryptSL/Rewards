package com.github.encryptsl.rewards.common

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.rewards.Rewards
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

class Locale(private val rewards: Rewards) {

    fun translation(key: String, tagResolver: TagResolver): Component {
        return ModernText.miniModernText(getMessage(key), tagResolver)
    }

    fun translation(key: String): Component {
        return ModernText.miniModernText(getMessage(key))
    }

    fun getMessage(key: String): String {
        val prefix = rewards.config.getString("prefix").toString()
        return rewards.config.getString(key).toString().replace("<prefix>", prefix)
    }

}