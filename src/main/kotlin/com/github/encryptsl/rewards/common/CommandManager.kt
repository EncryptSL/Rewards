package com.github.encryptsl.rewards.common


import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.api.commands.AnnotationCommandRegister
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.SenderMapper
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.AnnotationParser
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.bukkit.CloudBukkitCapabilities
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.execution.ExecutionCoordinator
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.minecraft.extras.MinecraftExceptionHandler
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.paper.LegacyPaperCommandManager
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.suggestion.Suggestion
import com.github.encryptsl.rewards.Rewards
import com.github.encryptsl.rewards.commands.RewardCmd
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import java.util.concurrent.CompletableFuture

class CommandManager(private val rewards: Rewards) {
    private fun createCommandManager(): LegacyPaperCommandManager<CommandSender> {
        val executionCoordinatorFunction = ExecutionCoordinator.builder<CommandSender>().build()
        val mapperFunction = SenderMapper.identity<CommandSender>()
        val commandManager = LegacyPaperCommandManager(
            rewards,
            executionCoordinatorFunction,
            mapperFunction
        )
        if (commandManager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            commandManager.registerBrigadier()
            commandManager.brigadierManager().setNativeNumberSuggestions(false)
        } else if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            (commandManager as LegacyPaperCommandManager<*>).registerAsynchronousCompletions()
        }
        return commandManager
    }


    private fun createAnnotationParser(commandManager: LegacyPaperCommandManager<CommandSender>): AnnotationParser<CommandSender> {
        return AnnotationParser<CommandSender>(commandManager, CommandSender::class.java)
    }

    private fun registerMinecraftExceptionHandler(commandManager: LegacyPaperCommandManager<CommandSender>) {
        MinecraftExceptionHandler.createNative<CommandSender>()
            .defaultHandlers()
            .decorator { component ->
                ModernText.miniModernText(rewards.config.getString("prefix", "<red>[<bold>!</bold>]").toString())
                    .appendSpace()
                    .append(component)
            }
            .registerTo(commandManager)
    }

    private fun registerSuggestionProviders(commandManager: LegacyPaperCommandManager<CommandSender>) {
        commandManager.parserRegistry().registerSuggestionProvider("offlinePlayers") { _, _ ->
            CompletableFuture.completedFuture(Bukkit.getOfflinePlayers()
                .map { Suggestion.suggestion(it.name ?: it.uniqueId.toString()) }
            )
        }
    }

    fun registerCommands() {
        try {
            rewards.logger.info("Registering commands with Cloud Command Framework !")
            val commandManager = createCommandManager()
            registerMinecraftExceptionHandler(commandManager)
            registerSuggestionProviders(commandManager)
            val annotationParser = createAnnotationParser(commandManager)
            val register = AnnotationCommandRegister(rewards, annotationParser, commandManager)
            register.registerCommand(RewardCmd(rewards))
        } catch ( e : NoClassDefFoundError) {
            rewards.logger.error(e.message ?: e.localizedMessage)
        }
    }

}