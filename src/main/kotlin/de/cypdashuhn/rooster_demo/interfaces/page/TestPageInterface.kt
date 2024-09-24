package de.cypdashuhn.rooster_demo.interfaces.page

import de.cypdashuhn.rooster.ui.interfaces.constructors.PageInterface
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

object TestPageInterface :
    PageInterface<TestPageInterface.TestPageContext>("TestPageInterface", TestPageContext::class) {
    class TestPageContext(override var page: Int) : PageContext(page)

    override fun initializePages(): List<Page<TestPageContext>> {
        TODO("Not yet implemented")
    }

    override fun getInventory(player: Player, context: TestPageContext): Inventory {
        TODO("Not yet implemented")
    }

    override fun defaultContext(player: Player): TestPageContext {
        TODO("Not yet implemented")
    }
}