package frame.ui.interfaces.constructors

import de.cypdashuhn.rooster.listeners.ClickState
import de.cypdashuhn.rooster.listeners.hasClicks
import de.cypdashuhn.rooster.ui.ClickInfo
import de.cypdashuhn.rooster.ui.Context
import de.cypdashuhn.rooster.ui.Slot
import de.cypdashuhn.rooster.ui.items.Condition
import de.cypdashuhn.rooster.ui.items.InterfaceItem
import de.cypdashuhn.rooster.ui.items.ItemStackCreator
import de.cypdashuhn.rooster.ui.items.constructors.ContextModifierItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass

abstract class ScrollInterface<ContextType : ScrollInterface.ScrollContext, DataType : Any>(
    override val interfaceName: String,
    override val contextClass: KClass<ContextType>,
    var scrollDirection: ScrollDirection = ScrollDirection.TOP_BOTTOM,
    override var contentArea: Pair<Pair<Int, Int>, Pair<Int, Int>> = (0 to 0) to (8 to 5)
) : IndexedContentInterface<ContextType, Int, DataType>(interfaceName, contextClass, contentArea) {
    enum class ScrollDirection {
        TOP_BOTTOM,
        LEFT_RIGHT
    }

    abstract class ScrollContext(
        open var position: Int = 0
    ) : Context()

    private val rowSize: Int
        get() = if (scrollDirection == ScrollDirection.LEFT_RIGHT) contentXWidth else contentYWidth

    abstract override fun getInventory(player: Player, context: ContextType): Inventory
    abstract override fun defaultContext(player: Player): ContextType

    abstract fun contentCreator(data: DataType): Pair<ItemStack, (ClickInfo<ContextType>) -> Unit>

    private fun contentItem() = InterfaceItem<ContextType>(
        condition = {
            if (offset(it.slot) != null) return@InterfaceItem false

            val data = dataFromPosition(it.slot, it.context, it.player)

            return@InterfaceItem data != null
        },
        itemStackCreator = {
            val data = dataFromPosition(it.slot, it.context, it.player)!!
            return@InterfaceItem contentCreator(data).first
        },
        action = {
            val data = dataFromPosition(it.click.slot, it.context, it.click.player)!!
            contentCreator(data).second(it)
        }
    )

    private fun clickInArea() = InterfaceItem<ContextType>(
        condition = {
            if (offset(it.slot) != null) return@InterfaceItem false
            val dataExists = dataFromPosition(it.slot, it.context, it.player) != null
            !dataExists
        },
        itemStackCreator = { ItemStack(Material.AIR) },
        action = {},
        priority = { -1 }
    )

    private fun scroller() = ContextModifierItem<ContextType>(
        condition = Condition(5 * 9 + 8),
        itemStack = ItemStackCreator(Material.COMPASS),
        contextModifier = { clickInfo ->
            clickInfo.context.also { context ->
                var scrollAmount = if (clickInfo.event.hasClicks(ClickState.SHIFT_CLICK)) 5 else 1
                if (clickInfo.event.hasClicks(ClickState.LEFT_CLICK)) scrollAmount *= -1

                context.position += scrollAmount
                if (context.position < 0) context.position = 0
            }
        }
    )

    open fun modifiedContentItem(item: InterfaceItem<ContextType>): InterfaceItem<ContextType> = item
    open fun clickInArea(item: InterfaceItem<ContextType>): InterfaceItem<ContextType> = item
    abstract fun getOtherItems(): List<InterfaceItem<ContextType>>

    final override fun getInterfaceItems(): List<InterfaceItem<ContextType>> {
        val list = mutableListOf(
            modifiedContentItem(contentItem()),
            clickInArea(clickInArea()),
            scroller()
        )

        list.addAll(getOtherItems())

        return list
    }


    override fun slotToId(slot: Slot, context: ContextType, player: Player): Int? {
        val (x, y) = offset(slot) ?: return null
        return if (scrollDirection == ScrollDirection.LEFT_RIGHT)
            x + (y + context.position) * contentYWidth
        else y + (x + context.position) * contentXWidth
    }
}