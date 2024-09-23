package de.cypdashuhn.rooster.ui.items

import de.cypdashuhn.rooster.ui.ClickInfo
import de.cypdashuhn.rooster.ui.Context
import de.cypdashuhn.rooster.ui.InterfaceInfo
import org.bukkit.inventory.ItemStack

abstract class BaseInterfaceItem<T : Context>(
    var condition: (InterfaceInfo<T>) -> Boolean,
    var itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
    var action: (ClickInfo<T>) -> Unit,
    var priority: (InterfaceInfo<T>) -> Int
)