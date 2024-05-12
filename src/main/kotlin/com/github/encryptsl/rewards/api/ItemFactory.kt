package com.github.encryptsl.rewards.api

import com.github.encryptsl.kmono.lib.api.ModernText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class ItemFactory {

    fun item(material: Material, displayName: String, lore: List<String>, availableAt: Component): ItemStack {
        val itemStack = ItemStack(material, 1)
        val itemMeta = itemStack.itemMeta

        itemMeta.displayName(ModernText.miniModernText(displayName))

        if (lore.isNotEmpty()) {
            val newList: MutableList<Component> = ArrayList()
            for (loreItem in lore) {
                newList.add(ModernText.miniModernText(loreItem, TagResolver.resolver(
                    Placeholder.component("cooldown", availableAt),
                )))
            }
            itemMeta.lore(newList)
        }

        itemStack.setItemMeta(itemMeta)
        return itemStack
    }

}