package de.cypdashuhn.rooster.ui.items

import de.cypdashuhn.rooster.ui.interfaces.ClickInfo
import de.cypdashuhn.rooster.ui.interfaces.Context
import de.cypdashuhn.rooster.ui.interfaces.InterfaceInfo
import org.bukkit.inventory.ItemStack

abstract class BaseInterfaceItem<T : Context>(
    var condition: (InterfaceInfo<T>) -> Boolean,
    var itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
    var action: (ClickInfo<T>) -> Unit,
    var priority: (InterfaceInfo<T>) -> Int
)