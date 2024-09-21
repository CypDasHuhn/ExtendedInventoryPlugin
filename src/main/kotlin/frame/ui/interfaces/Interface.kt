package de.cypdashuhn.rooster.ui.interfaces

import de.cypdashuhn.rooster.Rooster.interfaceContextProvider
import de.cypdashuhn.rooster.ui.ClickInfo
import de.cypdashuhn.rooster.ui.Context
import de.cypdashuhn.rooster.ui.InterfaceManager
import de.cypdashuhn.rooster.ui.items.InterfaceItem
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import kotlin.reflect.KClass

/**
 * An Instance of Interface is a model of a UI component. It's main
 * ingredient is [getInterfaceItems], which get resolved dynamically. The
 * field [interfaceName] is the key connected to the particular Interface.
 */
abstract class Interface<T : Context>(
    open val interfaceName: String,
    open val contextClass: KClass<T>,
    val cancelEvent: (ClickInfo<T>) -> Boolean = { true },
    val ignorePlayerInventory: Boolean = true,
    val ignoreEmptySlots: Boolean = true
) {
    val items
        get() = getInterfaceItems()

    open fun getInventory(player: Player, context: T): Inventory {
        return Bukkit.createInventory(null, 9, Component.text(interfaceName))
    }

    abstract fun getInterfaceItems(): List<InterfaceItem<T>>
    abstract fun defaultContext(player: Player): T

    @Suppress("unused")
    fun openInventory(player: Player, context: T) {
        InterfaceManager.openTargetInterface(player, this, context)
    }

    fun getContext(player: Player): T {
        return interfaceContextProvider.getContext(player, this) ?: defaultContext(player)
    }
}