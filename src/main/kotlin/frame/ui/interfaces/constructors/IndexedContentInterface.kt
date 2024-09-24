package frame.ui.interfaces.constructors

import de.cypdashuhn.rooster.ui.Context
import de.cypdashuhn.rooster.ui.Slot
import de.cypdashuhn.rooster.ui.interfaces.Interface
import de.cypdashuhn.rooster.ui.items.InterfaceItem
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import kotlin.reflect.KClass

abstract class IndexedContentInterface<ContextType : Context, IdType : Any, DataType : Any>(
    override val interfaceName: String,
    override val contextClass: KClass<ContextType>,
    open val contentArea: Pair<Pair<Int, Int>, Pair<Int, Int>>,
) : Interface<ContextType>(interfaceName, contextClass) {

    val contentAreaStartX by lazy { contentArea.first.first }
    val contentAreaStartY by lazy { contentArea.first.second }
    val contentAreaEndX by lazy { contentArea.second.first }
    val contentAreaEndY by lazy { contentArea.second.second }

    val contentXWidth by lazy { contentAreaEndX - contentAreaStartX + 1 }
    val contentYWidth by lazy { contentAreaEndY - contentAreaStartY + 1 }
    val contentXRange by lazy { contentAreaStartX..contentAreaEndY }
    val contentYRange by lazy { contentAreaStartX..contentAreaEndY }

    /** Returns the Offset, the Relative being the upper left corner. */
    fun offset(slot: Slot): Pair<Int, Int>? {
        require(
            contentArea.first.first in 0..8 && contentArea.first.second in 0..5 &&
                    contentArea.second.first >= contentArea.first.first && contentArea.second.second >= contentArea.first.second
        ) {
            "require valid content area. x 0-8, y 0-5, second always larger then first"
        }

        val x = (slot % 9) // 9 being inventory width
        val y = (slot / 9)

        return if (x in contentXRange && y in contentYRange) {
            x - contentAreaStartX to y - contentAreaStartY
        } else {
            null
        }
    }

    abstract override fun getInventory(player: Player, context: ContextType): Inventory
    abstract override fun getInterfaceItems(): List<InterfaceItem<ContextType>>
    abstract override fun defaultContext(player: Player): ContextType

    abstract fun slotToId(slot: Slot, context: ContextType, player: Player): IdType?
    abstract fun contentProvider(id: IdType): DataType?
    protected fun dataFromPosition(slot: Int, context: ContextType, player: Player): DataType? {
        val absoluteSlot = slotToId(slot, context, player) ?: return null
        return contentProvider(absoluteSlot)
    }

}