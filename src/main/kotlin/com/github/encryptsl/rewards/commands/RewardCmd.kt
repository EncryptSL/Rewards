package com.github.encryptsl.rewards.commands

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.rewards.Rewards
import com.github.encryptsl.rewards.api.menu.OpenGUI
import com.github.encryptsl.rewards.api.objects.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin Rewards")
class RewardCmd(private val rewards: Rewards) {

    private val rewardsMenu: OpenGUI by lazy { OpenGUI(rewards) }

    @CommandMethod("reward|odmena")
    @CommandPermission("reward.menu")
    fun onOpenRewardMenu(player: Player) {
        rewards.rewardTasks.doSync {
            rewardsMenu.openRewardGUI(player)
        }
    }


    @CommandMethod("rewards admin reset <player> <reward>")
    @CommandPermission("rewards.admin.reset")
    fun onRewardsAdminReset(
        commandSender: CommandSender,
        @Argument(value = "player", suggestions = "offlinePlayers") target: OfflinePlayer,
        @Argument(value = "reward", suggestions = "rewards") reward: String
    ) {
        if (!rewards.rewardsAPI.hasClaimReward(target.uniqueId, reward))
            commandSender.sendMessage(ModernText.miniModernText(rewards.locale.getMessage("messages.plugin.player-not-exist-"), TagResolver.resolver(
                Placeholder.parsed("player", target.name.toString()),
                Placeholder.parsed("reward", reward),
            )))

        rewards.rewardsAPI.resetCooldown(target.uniqueId, reward)

        commandSender.sendMessage(ModernText.miniModernText(rewards.locale.getMessage("messages.rewards.success.reset"), TagResolver.resolver(
            Placeholder.parsed("player", target.name.toString()),
            Placeholder.parsed("reward", reward),
        )))
    }

    @CommandMethod("rewards admin reload")
    @CommandPermission("rewards.admin.reload")
    fun onConfigReload(commandSender: CommandSender) {
        rewards.reloadConfig()
        rewards.saveConfig()
        commandSender.sendMessage(ModernText.miniModernText(rewards.locale.getMessage("messages.plugin.reload")))
    }

    @CommandMethod("rewards admin debug")
    @CommandPermission("rewards.admin.debug")
    fun onDebug(commandSender: CommandSender) {
        commandSender.sendMessage(ModernText.miniModernText("<dark_red>DEBUG <red> %s".format(rewards.rewardModel.dumpDatabase())))
    }

}