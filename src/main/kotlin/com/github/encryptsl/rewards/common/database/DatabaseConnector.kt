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

    override fun dataSource(): HikariDataSource {
        return HikariDataSource()
    }

    override fun initConnect(jdbcHost: String, user: String, pass: String) {

        DatabaseBuilder.Builder()
            .setJdbc(jdbcHost)
            .setUser(user)
            .setPassword(pass)
            .setConnectionPool(10)
            .setLogger(rewards.slF4JLogger)
            .setDatasource(dataSource())
            .connect()

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(RewardTable)
        }
    }

}