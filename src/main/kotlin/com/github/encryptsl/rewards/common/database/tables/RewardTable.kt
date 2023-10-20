package com.github.encryptsl.rewards.common.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object RewardTable : Table("claimed_rewards") {
    private val id = integer("id").autoIncrement()
    val username = varchar("username", 36)
    val uuid = varchar("uuid", 36)
    val reward_type = varchar("reward_type", 36)
    val claimed_at = timestamp("claimed_at")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}