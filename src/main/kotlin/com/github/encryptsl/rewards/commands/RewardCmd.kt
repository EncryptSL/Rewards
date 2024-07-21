package com.github.encryptsl.rewards.commands


import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.kmono.lib.api.commands.AnnotationFeatures
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.annotations.*
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.paper.LegacyPaperCommandManager
import com.github.encryptsl.kmono.lib.dependencies.incendo.cloud.suggestion.Suggestion
import com.github.encryptsl.rewards.Rewards
import com.github.encryptsl.rewards.api.menu.OpenGUI
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.concurrent.CompletableFuture

@Suppress("UNUSED")
@CommandDescription("Provided by plugin Rewards")
class RewardCmd(private val rewards: Rewards) : AnnotationFeatures {

    private val rewardsMenu: OpenGUI by lazy { OpenGUI(rewards) }

    override fun registerFeatures(
        annotationParser: AnnotationParser<CommandSender>,
        commandManager: LegacyPaperCommandManager<CommandSender>
    ) {
        commandManager.parserRegistry().registerSuggestionProvider("rewards") { _, _ ->
            CompletableFuture
                .completedFuture(rewards.config.getConfigurationSection("gui.rewards")
                    ?.getKeys(false)?.map { Suggestion.suggestion(it) }!!)
        }
        annotationParser.parse(this)
    }


    @Command("reward|odmena")
    @Permission("reward.menu")
    fun onOpenRewardMenu(player: Player) {
        rewardsMenu.openRewardGUI(player)
    }


    @Command("rewards admin reset <player> <reward>")
    @Permission("rewards.admin.reset")
    fun onRewardsAdminReset(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "reward", suggestions = "rewards") reward: String
    ) {
        if (!rewards.rewardsAPI.hasClaimReward(target.uniqueId, reward))
            return commandSender.sendMessage(rewards.locale.translation("messages.plugin.player-not-exist", TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("reward", reward),
            )))

        rewards.rewardsAPI.resetCooldown(target.uniqueId, reward)

        commandSender.sendMessage(rewards.locale.translation("messages.rewards.success.reset", TagResolver.resolver(
            Placeholder.parsed("player", target.name.toString()),
            Placeholder.parsed("reward", reward),
        )))
    }

    @Command("rewards admin reload")
    @Permission("rewards.admin.reload")
    fun onConfigReload(commandSender: CommandSender) {
        rewards.reloadConfig()
        rewards.saveConfig()
        commandSender.sendMessage(rewards.locale.translation("messages.plugin.reload"))
    }

    @Command("rewards admin debug")
    @Permission("rewards.admin.debug")
    fun onDebug(commandSender: CommandSender) {
        commandSender.sendMessage(ModernText.miniModernText("<dark_red>DEBUG <red> %s".format(rewards.rewardModel.dumpDatabase())))
    }
}