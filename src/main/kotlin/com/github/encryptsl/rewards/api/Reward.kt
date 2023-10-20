package com.github.encryptsl.rewards.api

import org.bukkit.entity.Player
import java.time.Duration
import java.util.*

interface Reward {
    fun claimReward(player: Player, rewardType: String, duration: Duration)
    fun receiveReward(player: Player, commands: List<String>)
    fun hasClaimReward(uuid: UUID, rewardType: String): Boolean
    fun hasCooldown(uuid: UUID, rewardType: String): Boolean
    fun resetCooldown(uuid: UUID, rewardType: String)
    fun getRemainingDuration(uuid: UUID, rewardType: String): Date?
}