package de.cypdashuhn.extendedInventoryPlugin.action

import de.cypdashuhn.extendedInventoryPlugin.database.PositionManager
import de.cypdashuhn.extendedInventoryPlugin.database.RegisteredPositionManager
import de.cypdashuhn.extendedInventoryPlugin.database.RegisteredPositionManager.positionFromName
import de.cypdashuhn.extendedInventoryPlugin.Main.Companion.cache
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object ItemModeActions {
    fun getItemsAtPosition(owner: PlayerManager.DbPlayer?, position: PositionManager.Position): List<ItemStack?> {
        val items = mutableListOf<ItemStack?>()
        for (i in -4..4) {
            val currentPosition = PositionManager.PositionDTO(position.x + i, position.y)
            items.add(currentPosition.position(owner)?.item?.itemStack)
        }

        return items
    }


}

fun Player.jumpToPosition(owner: PlayerManager.DbPlayer?, positionDTO: PositionManager.PositionDTO) {
    TODO("Implement Item Inventory Replacement")
}

fun Player.jumpToPosition(owner: PlayerManager.DbPlayer?, name: String) {
    val position = positionFromName(this, name)
    this.jumpToPosition(owner, position!!)
}

fun Player.jumpToPosition(owner: PlayerManager.DbPlayer?, position: RegisteredPositionManager.RegisteredPosition) {
    this.jumpToPosition(owner, position.position.toDTO())
}

fun Player.toggleItemMode() {

}

const val ROOSTER_PLAYER_INVENTORY_CACHE_KEY = "ROOSTER_PLAYER_INVENTORY_CACHE_KEY"
fun CommandSender.setCachedPlayerInventory(items: Array<ItemStack>) {
    cache.put(ROOSTER_PLAYER_INVENTORY_CACHE_KEY, this, items)
}

fun getCachedPlayerInventory(): Array<ItemStack> {
    return (cache.getIfPresent(ROOSTER_PLAYER_INVENTORY_CACHE_KEY) as Array<ItemStack>? ?: emptyArray<ItemStack>())
}

