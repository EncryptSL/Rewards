package com.github.encryptsl.rewards.commands


import com.github.encryptsl.rewards.Rewards
import com.github.encryptsl.rewards.api.menu.OpenGUI
import com.github.encryptsl.rewards.api.objects.ModernText
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

@Suppress("UNUSED")
@CommandDescription("Provided by plugin Rewards")
class RewardCmd(private val rewards: Rewards) {

    private val rewardsMenu: OpenGUI by lazy { OpenGUI(rewards) }

    @Command("reward|odmena")
    @Permission("reward.menu")
    fun onOpenRewardMenu(player: Player) {
        rewards.rewardTasks.doSync {
            rewardsMenu.openRewardGUI(player)
        }
    }


    @Command("rewards admin reset <player> <reward>")
    @Permission("rewards.admin.reset")
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

    @Command("rewards admin reload")
    @Permission("rewards.admin.reload")
    fun onConfigReload(commandSender: CommandSender) {
        rewards.reloadConfig()
        rewards.saveConfig()
        commandSender.sendMessage(ModernText.miniModernText(rewards.locale.getMessage("messages.plugin.reload")))
    }

    @Command("rewards admin debug")
    @Permission("rewards.admin.debug")
    fun onDebug(commandSender: CommandSender) {
        commandSender.sendMessage(ModernText.miniModernText("<dark_red>DEBUG <red> %s".format(rewards.rewardModel.dumpDatabase())))
    }

}