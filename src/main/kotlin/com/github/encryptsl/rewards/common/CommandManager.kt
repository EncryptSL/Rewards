package com.github.encryptsl.rewards.common

import cloud.commandframework.SenderMapper
import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.bukkit.CloudBukkitCapabilities
import cloud.commandframework.execution.ExecutionCoordinator
import cloud.commandframework.paper.PaperCommandManager
import cloud.commandframework.suggestion.Suggestion
import com.github.encryptsl.rewards.Rewards
import com.github.encryptsl.rewards.commands.RewardCmd
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import java.util.concurrent.CompletableFuture

class CommandManager(private val rewards: Rewards) {
    private fun createCommandManager(): PaperCommandManager<CommandSender> {
        val executionCoordinatorFunction = ExecutionCoordinator.builder<CommandSender>().build()
        val mapperFunction = SenderMapper.identity<CommandSender>()
        val commandManager = PaperCommandManager(
            rewards,
            executionCoordinatorFunction,
            mapperFunction
        )
        if (commandManager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            commandManager.registerBrigadier()
            commandManager.brigadierManager().setNativeNumberSuggestions(false)
        }
        if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            (commandManager as PaperCommandManager<*>).registerAsynchronousCompletions()
        }
        return commandManager
    }


    private fun createAnnotationParser(commandManager: PaperCommandManager<CommandSender>): AnnotationParser<CommandSender> {
        return AnnotationParser(
            commandManager,
            CommandSender::class.java,
        )
    }
    private fun registerSuggestionProviders(commandManager: PaperCommandManager<CommandSender>) {
        commandManager.parserRegistry().registerSuggestionProvider("offlinePlayers") { _, input ->
            CompletableFuture.completedFuture(Bukkit.getOfflinePlayers()
                .filter { p -> p.name?.startsWith(input.input(), true) ?: false }
                .map { Suggestion.simple(it.name ?: it.uniqueId.toString()) }
            )
        }

        commandManager.parserRegistry().registerSuggestionProvider("rewards") { _, _ ->
            CompletableFuture.completedFuture(rewards.config.getConfigurationSection("gui.rewards")?.getKeys(false)?.map { Suggestion.simple(it) }!!)
        }
    }

    fun registerCommands() {
        rewards.logger.info("Registering commands with Cloud Command Framework !")
        val commandManager = createCommandManager()
        registerSuggestionProviders(commandManager)
        val annotationParser = createAnnotationParser(commandManager)
        annotationParser.parse(RewardCmd(rewards))
    }

}