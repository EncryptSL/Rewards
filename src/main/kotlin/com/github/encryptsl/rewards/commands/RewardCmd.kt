package com.github.encryptsl.rewards.commands

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import com.github.encryptsl.rewards.Rewards
import com.github.encryptsl.rewards.api.menu.OpenGUI
import com.github.encryptsl.rewards.api.objects.ModernText
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Suppress("UNUSED")
@CommandDescription("Provided by plugin Rewards")
class RewardCmd(private val rewards: Rewards) {

    private val rewardsMenu: OpenGUI by lazy { OpenGUI(rewards) }

    @CommandMethod("rewards|odmeny")
    @CommandPermission("rewards.menu")
    fun onOpenRewardMenu(player: Player) {
        rewards.rewardTasks.doSync {
            rewardsMenu.openRewardGUI(player)
        }
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