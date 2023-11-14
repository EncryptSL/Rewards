package com.github.encryptsl.rewards.common.database

import com.github.encryptsl.rewards.common.database.tables.RewardTable
import com.zaxxer.hikari.HikariDataSource
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseConnector : DatabaseConnectorProvider {

    override fun initConnect(jdbcHost: String, user: String, pass: String) {
        val config = HikariDataSource().apply {
            maximumPoolSize = 10
            jdbcUrl = jdbcHost
            username = user
            password = pass
        }

        Bukkit.getLogger().info("Database connecting...")
        Database.connect(config)
        Bukkit.getLogger().info("Database successfully connected.")

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(RewardTable)
        }
    }

}