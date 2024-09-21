package de.cypdashuhn.rooster.ui.interfaces.constructors

import de.cypdashuhn.rooster.ui.ClickInfo
import de.cypdashuhn.rooster.ui.Context
import de.cypdashuhn.rooster.ui.interfaces.Interface
import de.cypdashuhn.rooster.ui.items.InterfaceItem
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass


/**
 * A Scroll Interface abstraction. By default, it goes from top to bottom
 * and defines the content area to be from (0,0) to (8,5), which means
 * the first five rows are content and the last row is for controls.
 */
abstract class ScrollInterface<T : ScrollInterface.ScrollContext, K>(
    override val interfaceName: String,
    override val contextClass: KClass<T>,
    var scrollDirection: ScrollDirection = ScrollDirection.TOP_BOTTOM,
    var contentArea: Pair<Pair<Int, Int>, Pair<Int, Int>> = (0 to 0) to (8 to 5)
) : Interface<T>(interfaceName, contextClass) {
    enum class ScrollDirection {
        TOP_BOTTOM,
        LEFT_RIGHT
    }

    abstract class ScrollContext(
        var position: Int = 0
    ) : Context()

    private val rowSize: Int
        get() {
            return if (scrollDirection == ScrollDirection.LEFT_RIGHT) {
                contentArea.second.first - contentArea.first.first
            } else {
                contentArea.second.second - contentArea.first.second
            }
        }

    abstract fun contentProvider(absolutePosition: Int): K?

    // Create the ItemStack for each piece of data
    abstract fun contentCreator(data: K): Pair<ItemStack, (ClickInfo<T>) -> Unit>

    override fun getInterfaceItems(): List<InterfaceItem<T>> {
        val content = InterfaceItem<T>(
            condition = {
                if (!isInsideContentArea(it.slot)) return@InterfaceItem false

                val data = dataFromPosition(it.slot, it.context)

                return@InterfaceItem data != null
            },
            itemStackCreator = {
                val data = dataFromPosition(it.slot, it.context)!!
                return@InterfaceItem contentCreator(data).first
            },
            action = {
                val data = dataFromPosition(it.click.slot, it.context)!!
                contentCreator(data).second(it)
            }
        )

        return listOf(content)
    }

    fun isInsideContentArea(slot: Int): Boolean {
        val (x1, y1) = contentArea.first
        val (x2, y2) = contentArea.second

        val inventoryWidth = 9

        val slotX = slot % inventoryWidth
        val slotY = slot / inventoryWidth

        return (slotX in x1..x2) && (slotY in y1..y2)
    }

    fun slotToAreaCoordinate(slot: Int): Pair<Int, Int> {
        val inventoryWidth = 9

        val slotX = slot % inventoryWidth
        val slotY = slot / inventoryWidth

        val (x1, y1) = contentArea.first

        val relativeX = slotX - x1
        val relativeY = slotY - y1

        return if (scrollDirection == ScrollDirection.LEFT_RIGHT) relativeX to relativeY
        else relativeY to relativeX
    }

    fun dataFromPosition(slot: Int, context: T): K? {
        val relativeCoordinate = slotToAreaCoordinate(slot)
        val rowAmount = (relativeCoordinate.second + context.position) * rowSize
        return contentProvider(context.position * rowAmount + slot)
    }
}