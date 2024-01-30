package com.github.encryptsl.rewards

import com.github.encryptsl.rewards.api.RewardsAPI
import com.github.encryptsl.rewards.api.config.ConfigLoader
import com.github.encryptsl.rewards.common.CommandManager
import com.github.encryptsl.rewards.common.Locale
import com.github.encryptsl.rewards.common.database.DatabaseConnector
import com.github.encryptsl.rewards.common.database.model.RewardsModel
import com.github.encryptsl.rewards.common.hook.HookManager
import com.github.encryptsl.rewards.common.tasks.RewardTasks
import org.bukkit.plugin.java.JavaPlugin

class Rewards : JavaPlugin() {

    private val commandManager: CommandManager by lazy { CommandManager(this) }
    private val configLoader: ConfigLoader by lazy { ConfigLoader() }
    private val hookManager: HookManager by lazy { HookManager(this) }


    val rewardTasks: RewardTasks by lazy { RewardTasks(this) }
    val rewardModel: RewardsModel by lazy { RewardsModel(this) }
    val rewardsAPI: RewardsAPI by lazy { RewardsAPI(this) }
    val locale: Locale by lazy { Locale(this) }

    val scheduler = server.scheduler
    val logger = slF4JLogger
    val pluginManager = server.pluginManager

    override fun onLoad() {
        configLoader
            .createFromResources("config.yml", this)
            .createFromResources("database.db", this)

        DatabaseConnector().initConnect(
            config.getString("database.host") ?: "jdbc:sqlite:plugins/Rewards/database.db",
            config.getString("database.username") ?: "root",
            config.getString("database.password") ?: "admin"
        )
    }

    override fun onEnable() {
        commandManager.registerCommands()
        hookRegistration()
        logger.info("Plugins Rewards is loaded !")
    }

    override fun onDisable() {
        logger.info("Plugins Rewards is disabled !")
    }

    private fun hookRegistration() {
        hookManager.hookDiscordSrv()
        hookManager.hookKiraDiscord()
    }

}