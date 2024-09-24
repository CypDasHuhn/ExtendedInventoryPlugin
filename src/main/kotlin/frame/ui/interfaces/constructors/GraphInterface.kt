package de.cypdashuhn.rooster.ui.interfaces.constructors

import de.cypdashuhn.rooster.ui.Context
import de.cypdashuhn.rooster.ui.Slot
import de.cypdashuhn.rooster.ui.items.InterfaceItem
import frame.ui.interfaces.constructors.IndexedContentInterface
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import kotlin.reflect.KClass

/** Interface not finished, don't use! */
abstract class GraphInterface<ContextType : GraphInterface.GraphContext, DataType : Any>(
    override val interfaceName: String,
    override val contextClass: KClass<ContextType>,
    override val contentArea: Pair<Pair<Int, Int>, Pair<Int, Int>>
) : IndexedContentInterface<ContextType, Pair<Int, Int>, DataType>(interfaceName, contextClass, contentArea) {
    abstract class GraphContext(
        val position: Pair<Int, Int>
    ) : Context()

    abstract override fun getInventory(player: Player, context: ContextType): Inventory
    abstract override fun getInterfaceItems(): List<InterfaceItem<ContextType>>
    abstract override fun defaultContext(player: Player): ContextType

    override fun slotToId(slot: Slot, context: ContextType, player: Player): Pair<Int, Int>? {
        val (x, y) = offset(slot) ?: return null
        val (posX, posY) = context.position

        return x + posX to y + posY
    }
}