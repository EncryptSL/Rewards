package com.github.encryptsl.rewards.common.database

import org.bukkit.entity.Player
import java.time.Duration
import java.util.Date
import java.util.UUID

interface RewardsSQL {
    fun firstClaim(player: Player, rewardType: String, duration: Duration)
    fun claimReward(uuid: UUID, rewardType: String, duration: Duration)
    fun hasClaimReward(uuid: UUID, rewardType: String): Boolean
    fun hasCooldown(uuid: UUID, rewardType: String): Boolean
    fun resetCooldown(uuid: UUID, rewardType: String)
    fun getRemainingDate(uuid: UUID, rewardType: String): Date?
}