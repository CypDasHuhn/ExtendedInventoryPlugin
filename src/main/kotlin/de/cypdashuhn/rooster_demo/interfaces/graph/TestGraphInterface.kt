package de.cypdashuhn.rooster_demo.interfaces.graph

import de.cypdashuhn.rooster.simulator.Simulator
import de.cypdashuhn.rooster.ui.interfaces.ClickInfo
import de.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import de.cypdashuhn.rooster.ui.interfaces.constructors.indexed_content.GraphInterface
import de.cypdashuhn.rooster.ui.items.InterfaceItem
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

@RoosterInterface
object TestGraphInterface :
    GraphInterface<TestGraphInterface.TestGraphContext, GraphData>("TestGraphInterface", TestGraphContext::class) {
    class TestGraphContext : GraphContext()

    override fun defaultContext(player: Player): TestGraphContext = TestGraphContext()

    override fun getOtherItems(): List<InterfaceItem<TestGraphContext>> {
        return listOf()
    }

    override fun contentProvider(id: Pair<Int, Int>): GraphData? {
        return GraphDataManager.getByCoord(id)
    }

    override fun getInventory(player: Player, context: TestGraphContext): Inventory {
        Simulator.interfaceName = "TestGraphInterface #${context.position.first}-${context.position.second}"
        return Bukkit.createInventory(
            null,
            6 * 9,
            "TestGraphInterface #${context.position.first}-${context.position.second}"
        )
    }

    override fun contentCreator(data: GraphData): Pair<ItemStack, (ClickInfo<TestGraphContext>) -> Unit> {
        val coloredWool = "${data.color.uppercase(Locale.getDefault())}_WOOL"
        val itemStack = ItemStack(Material.valueOf(coloredWool))
        return itemStack to {}
    }
}