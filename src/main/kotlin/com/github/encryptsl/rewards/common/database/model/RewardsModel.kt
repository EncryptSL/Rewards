package com.github.encryptsl.rewards.common.database.model

import com.github.encryptsl.rewards.Rewards
import com.github.encryptsl.rewards.common.database.RewardsSQL
import com.github.encryptsl.rewards.common.database.tables.RewardTable
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration
import java.util.*

class RewardsModel(private val rewards: Rewards) : RewardsSQL {

    override fun firstClaim(player: Player, rewardType: String, duration: Duration) {
        rewards.rewardTasks.doAsync {
            transaction {
                RewardTable.insertIgnore {
                    it[username] = player.name
                    it[uuid] = player.uniqueId.toString()
                    it[reward_type] = rewardType
                    it[claimed_at] = java.time.Instant.now().plus(duration).toKotlinInstant()
                }
            }
        }
    }

    override fun claimReward(uuid: UUID, rewardType: String, duration: Duration) {
        rewards.rewardTasks.doAsync {
            transaction {
                RewardTable.update({ (RewardTable.uuid eq uuid.toString()) and (RewardTable.reward_type eq rewardType) }) {
                    it[claimed_at] = java.time.Instant.now().plus(duration).toKotlinInstant()
                }
            }
            rewards.logger.info("Success updated [UUID: ${uuid}, REWARD:$rewardType, ${java.time.Instant.now().plus(duration).toKotlinInstant()}]")
        }
    }

    override fun hasClaimReward(uuid: UUID, rewardType: String): Boolean = transaction {
        !RewardTable.select { (RewardTable.uuid eq uuid.toString()) and (RewardTable.reward_type eq rewardType) }
            .empty()
    }

    override fun hasCooldown(uuid: UUID, rewardType: String): Boolean = transaction {
        val cooldown =
            RewardTable.select { (RewardTable.uuid eq uuid.toString()) and (RewardTable.reward_type eq rewardType) }
                .firstOrNull()

        cooldown != null && Clock.System.now().toJavaInstant()
            .isBefore(cooldown[RewardTable.claimed_at].toJavaInstant())
    }

    override fun resetCooldown(uuid: UUID, rewardType: String) {
        rewards.rewardTasks.doAsync {
            transaction {
                RewardTable.update({ (RewardTable.uuid eq uuid.toString()) and (RewardTable.reward_type eq rewardType) }) {
                    it[claimed_at] = Instant.fromEpochMilliseconds(0)
                }
            }
        }
    }

    override fun getRemainingDate(uuid: UUID, rewardType: String): Date? = transaction {
        val query =
            RewardTable.select { (RewardTable.uuid eq uuid.toString()) and (RewardTable.reward_type eq rewardType) }
                .firstOrNull()

        if (query != null) Date.from(query[RewardTable.claimed_at].toJavaInstant()) else null
    }

    fun dumpDatabase(): MutableMap<String, List<String>> = transaction {
        RewardTable.selectAll().associate {
            it[RewardTable.uuid] to listOf(
                it[RewardTable.username],
                it[RewardTable.reward_type],
                it[RewardTable.claimed_at].toString()
            )
        }.toMutableMap()
    }

}