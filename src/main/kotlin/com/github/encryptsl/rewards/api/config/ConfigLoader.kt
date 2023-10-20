package com.github.encryptsl.rewards.api.config

import org.bukkit.plugin.Plugin
import java.io.File

class ConfigLoader {

    fun createFromResources(configName: String, plugin: Plugin): ConfigLoader {
        val file = File(plugin.dataFolder, configName)
        if (!file.exists()) {
            plugin.saveResource(configName, false)
        } else {
            plugin.logger.info("Configuration $configName exist !")
        }
        return this
    }


}