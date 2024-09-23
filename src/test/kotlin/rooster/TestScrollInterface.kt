package rooster

import de.cypdashuhn.rooster.ui.ClickInfo
import de.cypdashuhn.rooster.ui.items.InterfaceItem
import frame.ui.interfaces.constructors.ScrollInterface
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class TestScrollInterface :
    ScrollInterface<TestScrollInterface.TestScrollContext, TestData>("TestScrollInterface", TestScrollContext::class) {
    class TestScrollContext(
        override var position: Int
    ) : ScrollContext()

    override fun contentCreator(data: TestData): Pair<ItemStack, (ClickInfo<TestScrollContext>) -> Unit> {
        val coloredWool = "${data.color}_WOOL"
        val itemStack = ItemStack(Material.valueOf(coloredWool))

        return itemStack to { }
    }

    override fun getOtherItems(): List<InterfaceItem<TestScrollContext>> {
        return listOf()
    }

    override fun contentProvider(id: Int): TestData? {
        return testDataById(id)
    }

    override fun defaultContext(player: Player): TestScrollContext = TestScrollContext(0)
}