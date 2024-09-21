package de.cypdashuhn.rooster.ui.items.constructors

import de.cypdashuhn.rooster.util.and
import de.cypdashuhn.rooster.ui.Context
import de.cypdashuhn.rooster.ui.InterfaceInfo
import de.cypdashuhn.rooster.ui.items.Condition
import de.cypdashuhn.rooster.ui.items.InterfaceItem
import de.cypdashuhn.rooster.ui.items.ItemStackCreator
import org.bukkit.inventory.ItemStack

open class PlaceholderItem<T : Context> : InterfaceItem<T> {
    constructor(
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStack: (InterfaceInfo<T>) -> ItemStack
    ) : super(condition, itemStack)

    constructor(
        condition: Condition<T>,
        itemStack: (InterfaceInfo<T>) -> ItemStack
    ) : super(condition(), itemStack)

    constructor(
        condition: Condition<T>,
        itemStack: ItemStackCreator<T>
    ) : super(condition(), itemStack())

    constructor(
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStack: ItemStackCreator<T>
    ) : super(condition, itemStack())

    constructor(
        slots: IntRange,
        condition: (InterfaceInfo<T>) -> Boolean = { true },
        itemStack: ItemStack
    ) : super({ it: InterfaceInfo<T> -> it.slot in slots } and condition, { itemStack })

    /* Constructors yet needed:
    * slots: IntRange, itemStack: ItemStackCreator<T>
    *
     * */
}