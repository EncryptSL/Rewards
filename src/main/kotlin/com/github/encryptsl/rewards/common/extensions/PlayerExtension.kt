package com.github.encryptsl.rewards.common.extensions

import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.entity.Player

fun playSound(player: Player, type: String, volume: Float, pitch: Float) {
    player.playSound(Sound.sound().type(Key.key(type)).volume(volume).pitch(pitch).build())
}