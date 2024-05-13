package com.github.encryptsl.rewards.common.database.model

import com.github.encryptsl.rewards.Rewards
import com.github.encryptsl.rewards.common.database.RewardsSQL
import com.github.encryptsl.rewards.common.database.tables.RewardTable
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toKotlinInstant
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Duration
import java.util.*
import java.util.concurrent.CompletableFuture

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
        }
    }

    override fun hasClaimReward(uuid: UUID, rewardType: String): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()
        val boolean = transaction {
            !RewardTable.select(RewardTable.uuid, RewardTable.reward_type)
                .where((RewardTable.uuid eq uuid.toString()) and (RewardTable.reward_type eq rewardType))
                .empty()
        }
        future.completeAsync { boolean }
        return future
    }

    override fun hasCooldown(uuid: UUID, rewardType: String): CompletableFuture<Boolean>  {
        val future = CompletableFuture<Boolean>()
        val boolean = transaction {
            val cooldown =
                RewardTable.select(
                    RewardTable.claimed_at,
                    RewardTable.reward_type,
                    RewardTable.uuid
                ).where((RewardTable.uuid eq uuid.toString()) and (RewardTable.reward_type eq rewardType)).firstOrNull()

            return@transaction cooldown != null && Clock.System.now().toJavaInstant().isBefore(cooldown[RewardTable.claimed_at].toJavaInstant())
        }
        future.completeAsync { boolean }
        return future
    }

    override fun resetCooldown(uuid: UUID, rewardType: String) {
        rewards.rewardTasks.doAsync {
            transaction {
                RewardTable.update({ (RewardTable.uuid eq uuid.toString()) and (RewardTable.reward_type eq rewardType) }) {
                    it[claimed_at] = Clock.System.now()
                }
            }
        }
    }

    override fun getRemainingDate(uuid: UUID, rewardType: String): CompletableFuture<Date?> {
        val future = CompletableFuture<Date?>()
        val date = transaction {
            val query =
                RewardTable.select(
                    RewardTable.uuid,
                    RewardTable.reward_type,
                    RewardTable.claimed_at
                ).where((RewardTable.uuid eq uuid.toString()) and (RewardTable.reward_type eq rewardType)).firstOrNull()

            return@transaction Date.from(query?.get(RewardTable.claimed_at)?.toJavaInstant())
        }
        future.completeAsync { date }
        return future
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