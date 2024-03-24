package com.github.encryptsl.rewards.common


import com.github.encryptsl.rewards.Rewards
import com.github.encryptsl.rewards.api.objects.ModernText
import com.github.encryptsl.rewards.commands.RewardCmd
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.incendo.cloud.SenderMapper
import org.incendo.cloud.annotations.AnnotationParser
import org.incendo.cloud.bukkit.CloudBukkitCapabilities
import org.incendo.cloud.execution.ExecutionCoordinator
import org.incendo.cloud.minecraft.extras.MinecraftExceptionHandler
import org.incendo.cloud.paper.PaperCommandManager
import org.incendo.cloud.suggestion.Suggestion
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

    private fun registerMinecraftExceptionHandler(commandManager: PaperCommandManager<CommandSender>) {
        MinecraftExceptionHandler.createNative<CommandSender>()
            .defaultHandlers()
            .decorator { component ->
                ModernText.miniModernText(rewards.config.getString("prefix", "<red>[<bold>!</bold>]").toString())
                    .appendSpace()
                    .append(component)
            }
            .registerTo(commandManager)
    }

    private fun registerSuggestionProviders(commandManager: PaperCommandManager<CommandSender>) {
        commandManager.parserRegistry().registerSuggestionProvider("offlinePlayers") { _, _ ->
            CompletableFuture.completedFuture(Bukkit.getOfflinePlayers()
                .map { Suggestion.simple(it.name ?: it.uniqueId.toString()) }
            )
        }

        commandManager.parserRegistry().registerSuggestionProvider("rewards") { _, _ ->
            CompletableFuture.completedFuture(rewards.config.getConfigurationSection("gui.rewards")?.getKeys(false)?.map { Suggestion.simple(it) }!!)
        }
    }

    fun registerCommands() {
        try {
            rewards.logger.info("Registering commands with Cloud Command Framework !")
            val commandManager = createCommandManager()
            registerMinecraftExceptionHandler(commandManager)
            registerSuggestionProviders(commandManager)
            val annotationParser = createAnnotationParser(commandManager)
            annotationParser.parse(RewardCmd(rewards))
        } catch ( e : NoClassDefFoundError) {
            rewards.logger.error(e.message ?: e.localizedMessage)
        }
    }

}