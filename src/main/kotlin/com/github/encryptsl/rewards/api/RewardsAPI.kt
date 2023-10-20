package com.github.encryptsl.rewards.api

import com.github.encryptsl.rewards.Rewards
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.time.Duration
import java.util.*

class RewardsAPI(private val rewards: Rewards) : Reward {

    override fun claimReward(player: Player, rewardType: String, duration: Duration) {
        if (!hasClaimReward(player.uniqueId, rewardType)) {
            rewards.rewardModel.firstClaim(player, rewardType, duration)
        } else {
            rewards.rewardModel.claimReward(player.uniqueId, rewardType, duration)
        }
    }

    override fun receiveReward(player: Player, commands: List<String>) {
        commands.forEach { command ->
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                .replace("%player%", player.name)
                .replace("{player}", player.name)
            )
        }
    }

    override fun hasClaimReward(uuid: UUID, rewardType: String): Boolean {
        return rewards.rewardModel.hasClaimReward(uuid, rewardType)
    }

    override fun hasCooldown(uuid: UUID, rewardType: String): Boolean {
        return rewards.rewardModel.hasCooldown(uuid, rewardType)
    }

    override fun resetCooldown(uuid: UUID, rewardType: String) {
        rewards.rewardModel.resetCooldown(uuid, rewardType)
    }

    override fun getRemainingDuration(uuid: UUID, rewardType: String): Date? {
        return rewards.rewardModel.getRemainingDate(uuid, rewardType)
    }


}