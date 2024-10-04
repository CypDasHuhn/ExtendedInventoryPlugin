package de.cypdashuhn.rooster.ui.items

import de.cypdashuhn.rooster.ui.interfaces.Context
import de.cypdashuhn.rooster.ui.interfaces.InterfaceInfo
import de.cypdashuhn.rooster.util.appendNumber
import de.cypdashuhn.rooster.util.infix_gate.and
import org.bukkit.inventory.ItemStack

class ItemInterface<T : Context> {
    val anonymousKey = "anonymous"
    val slotKey = "slot"
    val conditionMap: MutableMap<String, (InterfaceInfo<T>) -> Boolean>

    fun addCondition(condition: (InterfaceInfo<T>) -> Boolean, name: String = anonymousKey) {
        val newName = if (conditionMap.contains(name)) appendNumber(name) else name
        conditionMap[newName] = condition
    }

    fun removeCondition(name: String? = null) {
        if (name == null) {
            conditionMap.clear()
        } else {
            conditionMap.remove(name)
        }
    }

    fun setCondition(condition: (InterfaceInfo<T>) -> Boolean, name: String? = null) {
        if (name == null) {
            conditionMap.clear()
            conditionMap[anonymousKey] = condition
        } else {
            conditionMap[name] = condition
        }
    }

    /** Returns true if the condition was found, false if not. */
    fun overrideCondition(
        name: String = anonymousKey,
        transformedCondition: ((InterfaceInfo<T>) -> Boolean) -> (InterfaceInfo<T>) -> Boolean
    ): Boolean {
        return if (conditionMap.contains(name)) {
            conditionMap[name] = transformedCondition(conditionMap[name]!!)
            true
        } else false
    }

    val totalCondition: (InterfaceInfo<T>) -> Boolean
        get() {
            return conditionMap.values.reduce { acc, condition -> acc and condition }
        }
    val itemStackCreator: (InterfaceInfo<T>) -> ItemStack
    val priority: (InterfaceInfo<T>) -> Int
    val action: (InterfaceInfo<T>) -> Unit

    constructor(
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
        action: (InterfaceInfo<T>) -> Unit = {},
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) {
        conditionMap = mutableMapOf(anonymousKey to condition)
        this.itemStackCreator = itemStackCreator
        this.priority = priority
        this.action = action
    }

    constructor(
        slot: Int,
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
        action: (InterfaceInfo<T>) -> Unit = {},
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) {
        conditionMap = mutableMapOf(anonymousKey to condition, slotKey to { it.slot == slot })
        this.itemStackCreator = itemStackCreator
        this.priority = priority
        this.action = action
    }

    constructor(
        slots: IntRange,
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
        action: (InterfaceInfo<T>) -> Unit = {},
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) {
        conditionMap = mutableMapOf(anonymousKey to condition, slotKey to { it.slot in slots })
        this.itemStackCreator = itemStackCreator
        this.priority = priority
        this.action = action
    }

    constructor(
        slots: List<Int>,
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
        action: (InterfaceInfo<T>) -> Unit = {},
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) {
        conditionMap = mutableMapOf(anonymousKey to condition, slotKey to { it.slot in slots })
        this.itemStackCreator = itemStackCreator
        this.priority = priority
        this.action = action
    }

    constructor(
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStack: ItemStack,
        action: (InterfaceInfo<T>) -> Unit = {},
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) {
        conditionMap = mutableMapOf(anonymousKey to condition)
        this.itemStackCreator = { itemStack }
        this.priority = priority
        this.action = action
    }

    constructor(
        slot: Int,
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStack: ItemStack,
        action: (InterfaceInfo<T>) -> Unit = {},
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) {
        conditionMap = mutableMapOf(anonymousKey to condition, slotKey to { it.slot == slot })
        this.itemStackCreator = { itemStack }
        this.priority = priority
        this.action = action
    }

    constructor(
        slots: IntRange,
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStack: ItemStack,
        action: (InterfaceInfo<T>) -> Unit = {},
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) {
        conditionMap = mutableMapOf(anonymousKey to condition, slotKey to { it.slot in slots })
        this.itemStackCreator = { itemStack }
        this.priority = priority
        this.action = action
    }

    constructor(
        slots: List<Int>,
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStack: ItemStack,
        action: (InterfaceInfo<T>) -> Unit = {},
        priority: (InterfaceInfo<T>) -> Int = { 0 }
    ) {
        conditionMap = mutableMapOf(anonymousKey to condition, slotKey to { it.slot in slots })
        this.itemStackCreator = { itemStack }
        this.priority = priority
        this.action = action
    }

}