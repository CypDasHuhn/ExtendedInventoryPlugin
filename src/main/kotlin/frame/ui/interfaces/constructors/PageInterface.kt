package de.cypdashuhn.rooster.ui.interfaces.constructors

import de.cypdashuhn.rooster.util.and
import de.cypdashuhn.rooster.listeners.ClickState
import de.cypdashuhn.rooster.listeners.hasClicks
import de.cypdashuhn.rooster.ui.Context
import de.cypdashuhn.rooster.ui.interfaces.Interface
import de.cypdashuhn.rooster.ui.items.Condition
import de.cypdashuhn.rooster.ui.items.InterfaceItem
import de.cypdashuhn.rooster.ui.items.ItemStackCreator
import de.cypdashuhn.rooster.ui.items.constructors.ContextModifierItem
import org.bukkit.Material
import kotlin.reflect.KClass

/** Generally unfinished in every aspect. just ignore! */
@Suppress("unused")
abstract class PageInterface<T : PageInterface.PageContext>(
    override val interfaceName: String,
    override val contextClass: KClass<T>,
    /* Value between 1-5 (last row is bottom bar) */
    private val contentRowAmount: Int = 5
) : Interface<T>(interfaceName, contextClass) {
    open class PageContext(
        var page: Int
    ) : Context()

    data class Page<T : Context>(val page: Int, val items: List<InterfaceItem<T>>)

    val bottomBar
        get() = contentRowAmount * 9

    abstract fun initializePages(): List<Page<T>>

    val pageTurner = ContextModifierItem<T>(
        Condition(bottomBar + 0),
        ItemStackCreator(Material.ARROW, ""),
        contextModifier = { clickInfo ->
            clickInfo.context.also {
                if (clickInfo.event.hasClicks(ClickState.LEFT_CLICK)) it.page += 1
                else it.page -= 1
            }
        }
    )

    val forwardPageTurner = pageTurner.changeContextModifierAction(contextModifier = { clickInfo ->
        clickInfo.context.also { it.page += 1 }
    }).also { it.condition = Condition<T>(bottomBar + 8)() }

    val backwardsPageTurned = pageTurner.changeContextModifierAction(contextModifier = { clickInfo ->
        clickInfo.context.also { it.page -= 1 }
    })

    open fun customizePageTurner(item: InterfaceItem<T>): InterfaceItem<T> {
        return item
    }

    override fun getInterfaceItems(): List<InterfaceItem<T>> {
        val baseItems = mutableListOf<InterfaceItem<T>>()

        baseItems.addAll(
            initializePages().map { page ->
                page.items.onEach { item ->
                    item.condition = item.condition and { it.context.page == page.page }
                }
            }.flatten()
        )

        baseItems.add(customizePageTurner(pageTurner))

        return baseItems
    }
}