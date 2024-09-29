package de.cypdashuhn.rooster.util

import net.kyori.adventure.text.TextComponent
import org.bukkit.Axis
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun List<Location>.nearest(location: Location): Location {
    return this.map { Pair(it, it.distance(location)) }.minByOrNull { it.second }!!.first
}

fun List<Player>.nearest(location: Location): Player {
    return this.map { Pair(it, it.location.distance(location)) }.minByOrNull { it.second }!!.first
}

fun createItem(
    material: Material,
    name: TextComponent? = null,
    description: List<TextComponent>? = null,
    amount: Int = 1,
    /** Not implemented */
    nbt: Any? = null,
): ItemStack {
    val item = ItemStack(material, amount)
    val itemMeta = item.itemMeta
    if (name != null) itemMeta.displayName(name)
    if (description != null) itemMeta.lore(description)
    item.itemMeta = itemMeta

    return item
}

fun ItemStack.modify(
    material: Material?,
    name: TextComponent? = null,
    description: List<TextComponent>? = null,
    amount: Int? = null,
    /** Not implemented */
    nbt: Any? = null,
): ItemStack {
    val itemMeta = this.itemMeta
    val item = if (material != null) ItemStack(material) else this
    if (amount != null) item.amount = amount
    if (name != null) itemMeta.displayName(name)
    if (description != null) itemMeta.lore(description)
    item.itemMeta = itemMeta

    return item
}

fun Location.value(axis: Axis): Double {
    return when (axis) {
        Axis.X -> this.x
        Axis.Y -> this.y
        Axis.Z -> this.z
    }
}

fun sameAxis(vararg axis: Axis): Boolean {
    val firstAxis = axis.first()
    axis.forEach {
        if (it != firstAxis) return false
    }
    return true
}