package de.cypdashuhn.rooster_demo.interfaces.scroll

import de.cypdashuhn.rooster.simulator.Simulator
import de.cypdashuhn.rooster.ui.interfaces.ClickInfo
import de.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import de.cypdashuhn.rooster.ui.interfaces.constructors.indexed_content.ScrollInterface
import de.cypdashuhn.rooster.ui.items.InterfaceItem
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

@RoosterInterface
object TestScrollInterface : ScrollInterface<TestScrollInterface.TestScrollContext, ScrollData>(
    "TestScrollInterface",
    TestScrollContext::class,
    scrollDirection = ScrollDirection.LEFT_RIGHT
) {
    class TestScrollContext(
        position: Int = 0
    ) : ScrollContext(position)

    override fun contentCreator(data: ScrollData): Pair<ItemStack, (ClickInfo<TestScrollContext>) -> Unit> {
        val coloredWool = "${data.color.uppercase(Locale.getDefault())}_WOOL"
        val itemStack = ItemStack(Material.valueOf(coloredWool))

        return itemStack to { }
    }

    override fun getOtherItems(): List<InterfaceItem<TestScrollContext>> {
        return listOf()
    }

    override fun contentProvider(id: Int): ScrollData? {
        return ScrollDataManager.testDataById(id)
    }

    override fun getInventory(player: Player, context: TestScrollContext): Inventory {
        Simulator.interfaceName = "TestScrollInterface #${context.position + 1}"
        return Bukkit.createInventory(null, 6 * 9, "TestScrollInterface #${context.position + 1}")
    }

    override fun defaultContext(player: Player): TestScrollContext = TestScrollContext()
}