package com.github.encryptsl.rewards.common

import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.arguments.parser.ParserParameters
import cloud.commandframework.arguments.parser.StandardParameters
import cloud.commandframework.bukkit.CloudBukkitCapabilities
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator
import cloud.commandframework.meta.CommandMeta
import cloud.commandframework.paper.PaperCommandManager
import com.github.encryptsl.rewards.Rewards
import com.github.encryptsl.rewards.commands.RewardCmd
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import java.util.function.Function

class CommandManager(private val rewards: Rewards) {
    private fun createCommandManager(): PaperCommandManager<CommandSender> {
        val executionCoordinatorFunction = AsynchronousCommandExecutionCoordinator.builder<CommandSender>().build()
        val mapperFunction = Function.identity<CommandSender>()
        val commandManager = PaperCommandManager(
            rewards,
            executionCoordinatorFunction,
            mapperFunction,
            mapperFunction
        )
        if (commandManager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            commandManager.registerBrigadier()
            commandManager.brigadierManager()?.setNativeNumberSuggestions(false)
        }
        if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            (commandManager as PaperCommandManager<*>).registerAsynchronousCompletions()
        }
        return commandManager
    }


    private fun createAnnotationParser(commandManager: PaperCommandManager<CommandSender>): AnnotationParser<CommandSender> {
        val commandMetaFunction = Function<ParserParameters, CommandMeta> { p: ParserParameters ->
            CommandMeta.simple() // Decorate commands with descriptions
                .with(CommandMeta.DESCRIPTION, p[StandardParameters.DESCRIPTION, "No Description"])
                .build()
        }
        return AnnotationParser(
            commandManager,
            CommandSender::class.java,
            commandMetaFunction /* Mapper for command meta instances */
        )
    }
    private fun registerSuggestionProviders(commandManager: PaperCommandManager<CommandSender>) {
        commandManager.parserRegistry().registerSuggestionProvider("offlinePlayers") { _, input ->
            Bukkit.getOfflinePlayers().toList()
                .filter { p ->
                    p.name?.startsWith(input, ignoreCase = true) ?: false
                }
                .mapNotNull { it.name }
        }

        commandManager.parserRegistry().registerSuggestionProvider("rewards") { _, _ ->
            rewards.config.getConfigurationSection("gui.rewards")
                ?.getKeys(false)?.mapNotNull { a -> a.toString() } ?: emptyList()
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