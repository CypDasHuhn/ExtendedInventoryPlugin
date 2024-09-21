package de.cypdashuhn.rooster.ui.interfaces.constructors

import de.cypdashuhn.rooster.ui.Context
import de.cypdashuhn.rooster.ui.interfaces.Interface
import de.cypdashuhn.rooster.ui.items.InterfaceItem
import kotlin.reflect.KClass

/** Interface not finished, don't use! */
abstract class GraphInterface<T : GraphInterface.GraphContext>(
    override val interfaceName: String,
    override val contextClass: KClass<T>,
) : Interface<T>(interfaceName, contextClass) {
    abstract class GraphContext(
        val position: Pair<Int, Int>
    ) : Context()

    override fun getInterfaceItems(): List<InterfaceItem<T>> {
        TODO("Not yet implemented")
    }
}