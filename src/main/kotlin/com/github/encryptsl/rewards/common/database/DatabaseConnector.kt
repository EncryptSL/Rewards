package com.github.encryptsl.rewards.common.database

import com.github.encryptsl.kmono.lib.api.database.DatabaseBuilder
import com.github.encryptsl.kmono.lib.api.database.DatabaseConnectorProvider
import com.github.encryptsl.rewards.Rewards
import com.github.encryptsl.rewards.common.database.tables.RewardTable
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseConnector(private val rewards: Rewards) : DatabaseConnectorProvider {

    override fun createConnection(jdbcHost: String, user: String, pass: String) {
        DatabaseBuilder.Builder()
            .setJdbc(jdbcHost)
            .setUser(user)
            .setPassword(pass)
            .setConnectionPoolName(rewards.javaClass.simpleName)
            .setConnectionPool(10)
            .setLogger(rewards.slF4JLogger)
            .setDatasource(HikariDataSource())
            .connect()

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(RewardTable)
        }
    }

}