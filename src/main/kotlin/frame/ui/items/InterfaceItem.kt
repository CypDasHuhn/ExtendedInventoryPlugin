package de.cypdashuhn.rooster.ui.items

import de.cypdashuhn.rooster.util.and
import de.cypdashuhn.rooster.ui.ClickInfo
import de.cypdashuhn.rooster.ui.Context
import de.cypdashuhn.rooster.ui.InterfaceInfo
import org.bukkit.inventory.ItemStack

open class InterfaceItem<T : Context> : BaseInterfaceItem<T> {
    constructor(
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
        action: (ClickInfo<T>) -> Unit = { }
    ) : super(
        condition, itemStackCreator, action
    )

    constructor(
        slot: Int,
        condition: (InterfaceInfo<T>) -> Boolean = { true },
        itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
        action: (ClickInfo<T>) -> Unit = { }
    ) : super(
        { it: InterfaceInfo<T> -> it.slot == slot } and condition,
        itemStackCreator,
        action
    )

    constructor(
        slot: IntRange,
        condition: (InterfaceInfo<T>) -> Boolean = { true },
        itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
        action: (ClickInfo<T>) -> Unit = { }
    ) : super(
        { it: InterfaceInfo<T> -> it.slot in slot } and condition,
        itemStackCreator,
        action
    )

    constructor(
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStack: ItemStack,
        action: (ClickInfo<T>) -> Unit = { }
    ) : super(
        condition, { itemStack }, action
    )

    constructor(
        slot: Int,
        condition: (InterfaceInfo<T>) -> Boolean = { true },
        itemStack: ItemStack,
        action: (ClickInfo<T>) -> Unit = { }
    ) : super(
        { it: InterfaceInfo<T> -> it.slot == slot } and condition,
        { itemStack },
        action
    )

    constructor(
        slot: IntRange,
        condition: (InterfaceInfo<T>) -> Boolean = { true },
        itemStack: ItemStack,
        action: (ClickInfo<T>) -> Unit = { }
    ) : super(
        { it: InterfaceInfo<T> -> it.slot in slot } and condition,
        { itemStack },
        action
    )

    constructor(
        condition: Condition<T>,
        itemStackCreator: ItemStackCreator<T>,
        action: (ClickInfo<T>) -> Unit = { }
    ) : super(condition(), itemStackCreator(), action)
}