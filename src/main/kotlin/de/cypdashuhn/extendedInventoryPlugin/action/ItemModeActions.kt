package action

import Main.Companion.cache
import database.OwnerManager
import database.PositionManager
import database.RegisteredPositionManager
import database.RegisteredPositionManager.positionFromName
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

object ItemModeActions {
    fun getItemsAtPosition(owner: OwnerManager.Owner, position: PositionManager.Position): List<ItemStack?> {
        val items = mutableListOf<ItemStack?>()
        for (i in -4..4) {
            val currentPosition = PositionManager.PositionDTO(position.x + i, position.y)
            items.add(currentPosition.position(owner)?.item?.itemStack)
        }

        return items
    }


}

fun Player.jumpToPosition(owner: OwnerManager.Owner, positionDTO: PositionManager.PositionDTO) {
    TODO("Implement Item Inventory Replacement")
}

fun Player.jumpToPosition(owner: OwnerManager.Owner, name: String) {
    val position = positionFromName(this, name)
    this.jumpToPosition(owner, position!!)
}

fun Player.jumpToPosition(owner: OwnerManager.Owner, position: RegisteredPositionManager.RegisteredPosition) {
    this.jumpToPosition(owner, position.position.toDTO())
}

fun Player.toggleItemMode() {

}

const val ROOSTER_PLAYER_INVENTORY_CACHE_KEY = "ROOSTER_PLAYER_INVENTORY_CACHE_KEY"
fun CommandSender.setCachedPlayerInventory(items: Array<ItemStack>) {
    cache.set(ROOSTER_PLAYER_INVENTORY_CACHE_KEY, this, items)
}

fun getCachedPlayerInventory(): Array<ItemStack> {
    return (cache.get(ROOSTER_PLAYER_INVENTORY_CACHE_KEY) as Array<ItemStack>? ?: emptyArray<ItemStack>())
}

