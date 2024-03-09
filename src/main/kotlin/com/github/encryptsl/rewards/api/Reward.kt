package com.github.encryptsl.rewards.api

import org.bukkit.entity.Player
import java.time.Duration
import java.util.*

interface Reward {
    /**
     * Executed when player claim reward
     * @param player Player
     * @param rewardType String
     * @param duration Duration
     * @see Player
     * @see Duration
     */
    fun claimReward(player: Player, rewardType: String, duration: Duration)
    /**
     * Executed when player receive reward
     * @param player Player
     * @param commands List<String>
     * @param rewardType String
     * @see commands
     * @see Player
     */
    fun receiveReward(player: Player, commands: List<String>, rewardType: String)
    /**
     * Control if player has claimed type of reward
     * @param uuid UUID
     * @param rewardType String
     * @see Player.getUniqueId
     * @return Boolean
     */
    fun hasClaimReward(uuid: UUID, rewardType: String): Boolean
    /**
     * Control if player has cooldown on type of reward
     * @param uuid UUID
     * @param rewardType String
     * @see Player.getUniqueId
     * @return Boolean
     */
    fun hasCooldown(uuid: UUID, rewardType: String): Boolean
    /**
     * Method resets player cooldown on type of reward.
     * @param uuid UUID
     * @param rewardType String
     * @see Player.getUniqueId
     */
    fun resetCooldown(uuid: UUID, rewardType: String)
    /**
     * Method returning time of remaining duration.
     * @param uuid UUID
     * @param rewardType String
     * @see Player.getUniqueId
     * @return Date?
     */
    fun getRemainingDuration(uuid: UUID, rewardType: String): Date?
}