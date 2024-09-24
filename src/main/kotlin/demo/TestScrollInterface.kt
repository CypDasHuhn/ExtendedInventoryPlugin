package demo

import de.cypdashuhn.rooster.simulator.Simulator
import de.cypdashuhn.rooster.ui.ClickInfo
import de.cypdashuhn.rooster.ui.RoosterInterface
import de.cypdashuhn.rooster.ui.items.InterfaceItem
import frame.ui.interfaces.constructors.ScrollInterface
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

@RoosterInterface
object TestScrollInterface : ScrollInterface<TestScrollInterface.TestScrollContext, TestData>("TestScrollInterface", TestScrollContext::class) {
    class TestScrollContext(
        position: Int = 0
    ) : ScrollContext(position)

    override fun contentCreator(data: TestData): Pair<ItemStack, (ClickInfo<TestScrollContext>) -> Unit> {
        val coloredWool = "${data.color}_WOOL"
        val itemStack = ItemStack(Material.valueOf(coloredWool))

        return itemStack to { }
    }

    override fun getOtherItems(): List<InterfaceItem<TestScrollContext>> {
        return listOf()
    }

    override fun contentProvider(id: Int): TestData? {
        return TestDataManager.testDataById(id)
    }

    override fun getInventory(player: Player, context: TestScrollContext): Inventory {
        Simulator.interfaceName = "TestScrollInterface"
        return Bukkit.createInventory(null, 6 * 9, "TestScrollInterface")
    }

    override fun defaultContext(player: Player): TestScrollContext = TestScrollContext()
}