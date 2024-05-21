package com.github.encryptsl.rewards.api.menu

import com.github.encryptsl.kmono.lib.api.ModernText
import com.github.encryptsl.rewards.Rewards
import com.github.encryptsl.rewards.api.ItemFactory
import com.github.encryptsl.rewards.common.extensions.convertFancyTime
import com.github.encryptsl.rewards.common.extensions.playSound
import com.github.encryptsl.rewards.common.hook.discordsrv.DiscordSrvException
import com.github.encryptsl.rewards.common.hook.discordsrv.DiscordSrvHook
import com.github.encryptsl.rewards.common.hook.kira.KiraDiscordException
import com.github.encryptsl.rewards.common.hook.kira.KiraDiscordHook
import dev.triumphteam.gui.paper.Gui
import dev.triumphteam.gui.paper.builder.item.ItemBuilder
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.entity.Player
import java.time.Duration

class OpenGUI(private val rewards: Rewards) {

    private val itemFactory: ItemFactory by lazy { ItemFactory() }
    private val kiraDiscordHook: KiraDiscordHook by lazy { KiraDiscordHook(rewards) }
    private val discordSrvHook: DiscordSrvHook by lazy { DiscordSrvHook(rewards) }

    fun openRewardGUI(player: Player) {

        if (!rewards.config.contains("gui.title"))
            return player.sendMessage(ModernText.miniModernText(rewards.config.getString("messages.gui.missing-title").toString()))

        if (!rewards.config.contains("gui.positions.rows"))
            return player.sendMessage(ModernText.miniModernText(rewards.config.getString("messages.gui.missing-rows").toString()))

        if (!rewards.config.contains("gui.rewards"))
            return player.sendMessage(ModernText.miniModernText(rewards.config.getString("messages.rewards.missing-rewards").toString()))

        if (!rewards.config.contains("time.pattern"))
            return player.sendMessage(ModernText.miniModernText(rewards.config.getString("messages.plugin.missing-time-pattern").toString()))

        val title = rewards.config.getString("gui.title").toString()
        val rows = rewards.config.getInt("gui.positions.rows")
        val pattern = rewards.config.getString("time.pattern").toString()

        val gui = Gui.of(rows)
            .title(ModernText.miniModernText(title))

        /*
        if (rewards.config.contains("gui.fill")) {
            if (rewards.config.contains("gui.fill.border")) {
                gui.filler.fillBorder(
                    GuiItem(
                        Material.valueOf(
                            rewards.config.getString("gui.fill.border").toString()
                        )
                    )
                )
            }
            if (rewards.config.contains("gui.fill.top")) {
                gui.filler.fillTop(
                    GuiItem(
                        Material.valueOf(
                            rewards.config.getString("gui.fill.top").toString()
                        )
                    )
                )
            }
            if (rewards.config.contains("gui.fill.bottom")) {
                gui.filler.fillBottom(
                    GuiItem(
                        Material.valueOf(
                            rewards.config.getString("gui.fill.bottom").toString()
                        )
                    )
                )
            }
            if (rewards.config.contains("gui.fill.all")) {
                gui.filler.fill(
                    GuiItem(
                        Material.valueOf(
                            rewards.config.getString("gui.fill.all").toString()
                        )
                    )
                )
            }
        }*/

        gui.component { component ->
            component.render { container, _ ->
                for (material in Material.entries) {
                    for (reward in rewards.config.getConfigurationSection("gui.rewards")?.getKeys(false)!!) {
                        if (!rewards.config.contains("gui.rewards.$reward.display.name"))
                            return@render player.sendMessage(
                                rewards.locale.translation("messages.rewards.error.missing-name", Placeholder.parsed("reward", reward))
                            )

                        if (!rewards.config.contains("gui.rewards.$reward.display.slot"))
                            return@render player.sendMessage(
                                rewards.locale.translation("messages.rewards.error.missing-slot", Placeholder.parsed("reward", reward))
                            )

                        if (!rewards.config.contains("gui.rewards.$reward.display.icon"))
                            return@render player.sendMessage(
                                rewards.locale.translation("messages.rewards.error.missing-icon", Placeholder.parsed("reward", reward))
                            )

                        if (!rewards.config.contains("gui.rewards.$reward.lore"))
                            return@render player.sendMessage(
                                rewards.locale.translation("messages.rewards.error.missing-lore", Placeholder.parsed("reward", reward)
                                ))

                        if (!rewards.config.contains("gui.rewards.$reward.commands"))
                            return@render player.sendMessage(
                                rewards.locale.translation("messages.rewards.error.missing-commands", Placeholder.parsed("reward", reward)
                                ))

                        if (rewards.config.getString("gui.rewards.$reward.display.icon").equals(material.name, true)) {
                            val name = rewards.config.getString("gui.rewards.$reward.display.name").toString()
                            val slot = rewards.config.getInt("gui.rewards.$reward.display.slot")
                            val lore = rewards.config.getStringList("gui.rewards.$reward.lore")
                            val commands = rewards.config.getStringList("gui.rewards.$reward.commands")
                            val cooldown = rewards.config.getInt("gui.rewards.$reward.requires.cooldown")
                            val hasCooldown = rewards.rewardsAPI.hasCooldown(player.uniqueId, reward)
                            val date = rewards.rewardsAPI.getRemainingDuration(player.uniqueId, reward)
                            val remaining = date?.let { convertFancyTime(it, pattern) }.toString()

                            val claimSound = rewards.config.getString("gui.click-sounds.claim.type").toString()
                            val claimSoundPitch = rewards.config.getString("gui.click-sounds.claim.pitch").toString().toFloat()
                            val claimSoundVolume = rewards.config.getString("gui.click-sounds.claim.volume").toString().toFloat()

                            val errorSound = rewards.config.getString("gui.click-sounds.error.type").toString()
                            val errorSoundPitch = rewards.config.getString("gui.click-sounds.error.pitch").toString().toFloat()
                            val errorSoundVolume = rewards.config.getString("gui.click-sounds.error.volume").toString().toFloat()

                            val availableAt = if (hasCooldown)
                                rewards.locale.translation("messages.rewards.available_at", Placeholder.parsed("available_at", remaining))
                            else
                                rewards.locale.translation("messages.rewards.is_available")

                            val guiItem = ItemBuilder.from(itemFactory.item(material, name, lore, availableAt)).asGuiItem { player, _ ->
                                if (rewards.config.contains("gui.rewards.$reward.requires.permissions")) {
                                    val permission = rewards.config.getString("gui.rewards.$reward.requires.permissions").toString()
                                    if (!player.hasPermission(permission)) {
                                        playSound(player, errorSound, errorSoundVolume, errorSoundPitch)
                                        return@asGuiItem player.sendMessage(rewards.locale.translation("messages.rewards.error.missing-permissions"))
                                    }
                                }

                                if (rewards.config.contains("gui.rewards.$reward.requires.discord")) {
                                    if (rewards.config.getBoolean("gui.rewards.$reward.requires.discord")) {
                                        try {
                                            if (!kiraDiscordHook.isLinked(player) && !discordSrvHook.isLinked(player)) {
                                                playSound(player, errorSound, errorSoundVolume, errorSoundPitch)
                                                return@asGuiItem player.sendMessage(rewards.locale.translation("messages.rewards.error.missing-discord-link"))
                                            }
                                        } catch (e : DiscordSrvException) {
                                            return@asGuiItem player.sendMessage(ModernText.miniModernText(e.message ?: e.localizedMessage))
                                        } catch (e : KiraDiscordException) {
                                            return@asGuiItem player.sendMessage(ModernText.miniModernText(e.message ?: e.localizedMessage))
                                        }
                                    }
                                }

                                if (hasCooldown) {
                                    playSound(player, errorSound, errorSoundVolume, errorSoundPitch)
                                    return@asGuiItem player.sendMessage(rewards.locale.translation("messages.rewards.error.claimed",
                                        Placeholder.parsed("available_at", remaining)
                                    ))
                                }

                                rewards.rewardsAPI.claimReward(player, reward, Duration.ofMinutes(cooldown.toLong()))
                                rewards.rewardsAPI.receiveReward(player, commands, reward)
                                playSound(player, claimSound, claimSoundVolume, claimSoundPitch)
                                player.sendMessage(rewards.locale.translation("messages.rewards.success.claim", TagResolver.resolver(
                                    Placeholder.parsed("available_at", remaining),
                                    Placeholder.parsed("reward", name)
                                )))
                                player.closeInventory()
                            }
                            container.set(slot, guiItem)
                        }
                    }
                }
            }
        }.build().open(player)
    }

}