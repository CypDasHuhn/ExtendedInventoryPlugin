package de.cypdashuhn.rooster.ui.items

import de.cypdashuhn.rooster.ui.interfaces.ClickInfo
import de.cypdashuhn.rooster.ui.interfaces.Context
import de.cypdashuhn.rooster.ui.interfaces.InterfaceInfo
import de.cypdashuhn.rooster.util.infix_gate.and
import org.bukkit.inventory.ItemStack

open class InterfaceItem<T : Context> : BaseInterfaceItem<T> {
    constructor(
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
        action: (ClickInfo<T>) -> Unit = { },
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) : super(
        condition, itemStackCreator, action, priority
    )

    constructor(
        slot: Int,
        condition: (InterfaceInfo<T>) -> Boolean = { true },
        itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
        action: (ClickInfo<T>) -> Unit = { },
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) : super(
        { it: InterfaceInfo<T> -> it.slot == slot } and condition,
        itemStackCreator,
        action, priority
    )

    constructor(
        slot: IntRange,
        condition: (InterfaceInfo<T>) -> Boolean = { true },
        itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
        action: (ClickInfo<T>) -> Unit = { },
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) : super(
        { it: InterfaceInfo<T> -> it.slot in slot } and condition,
        itemStackCreator,
        action, priority
    )

    constructor(
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStack: ItemStack,
        action: (ClickInfo<T>) -> Unit = { },
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) : super(
        condition, { itemStack }, action, priority
    )

    constructor(
        slot: Int,
        condition: (InterfaceInfo<T>) -> Boolean = { true },
        itemStack: ItemStack,
        action: (ClickInfo<T>) -> Unit = { },
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) : super(
        { it: InterfaceInfo<T> -> it.slot == slot } and condition,
        { itemStack },
        action, priority
    )

    constructor(
        slot: IntRange,
        condition: (InterfaceInfo<T>) -> Boolean = { true },
        itemStack: ItemStack,
        action: (ClickInfo<T>) -> Unit = { },
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) : super(
        { it: InterfaceInfo<T> -> it.slot in slot } and condition,
        { itemStack },
        action, priority
    )

    constructor(
        condition: Condition<T>,
        itemStackCreator: ItemStackCreator<T>,
        action: (ClickInfo<T>) -> Unit = { },
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) : super(condition(), itemStackCreator(), action, priority)
}