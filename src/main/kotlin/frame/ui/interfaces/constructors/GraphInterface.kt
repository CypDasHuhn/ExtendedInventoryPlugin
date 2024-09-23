package de.cypdashuhn.rooster.ui.interfaces.constructors

import de.cypdashuhn.rooster.ui.Context
import de.cypdashuhn.rooster.ui.Slot
import frame.ui.interfaces.constructors.IndexedContentInterface
import org.bukkit.entity.Player
import kotlin.reflect.KClass

/** Interface not finished, don't use! */
abstract class GraphInterface<T : GraphInterface.GraphContext, DataType : Any>(
    override val interfaceName: String,
    override val contextClass: KClass<T>,
    override val contentArea: Pair<Pair<Int, Int>, Pair<Int, Int>>
) : IndexedContentInterface<T, Pair<Int, Int>, DataType>(interfaceName, contextClass, contentArea) {
    abstract class GraphContext(
        val position: Pair<Int, Int>
    ) : Context()

    override fun slotToId(slot: Slot, context: T, player: Player): Pair<Int, Int>? {
        val (x, y) = offset(slot) ?: return null
        val (posX, posY) = context.position

        return x + posX to y + posY
    }
}