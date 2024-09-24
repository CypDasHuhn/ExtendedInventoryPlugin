package de.cypdashuhn.rooster.ui.items.constructors

import de.cypdashuhn.rooster.ui.interfaces.ClickInfo
import de.cypdashuhn.rooster.ui.interfaces.Context
import de.cypdashuhn.rooster.ui.items.Condition
import de.cypdashuhn.rooster.ui.items.InterfaceItem
import de.cypdashuhn.rooster.ui.items.ItemStackCreator

open class ContextModifierItem<T : Context> : InterfaceItem<T> {
    protected companion object {
        fun <T : Context> contextModifierAction(
            contextModifier: (ClickInfo<T>) -> T,
            furtherAction: (ClickInfo<T>) -> Unit
        ): (ClickInfo<T>) -> Unit {
            return {
                furtherAction(it)
                val context = contextModifier(it)
                it.clickedInterface.openInventory(it.click.player, context)
            }
        }
    }

    fun changeContextModifierAction(
        contextModifier: (ClickInfo<T>) -> T,
        furtherAction: (ClickInfo<T>) -> Unit = { }
    ): ContextModifierItem<T> {
        this.action = contextModifierAction(contextModifier, furtherAction)
        return this
    }

    constructor(
        condition: Condition<T>,
        itemStack: ItemStackCreator<T>,
        contextModifier: (ClickInfo<T>) -> T,
        furtherAction: (ClickInfo<T>) -> Unit = { }
    ) : super(condition, itemStack, action = contextModifierAction(contextModifier, furtherAction))

    /*
    * This class needs more constructors yet!
    * Basically the Condition and ItemStackCreator classes, but split up and combined matrix-like.
    *  */
}