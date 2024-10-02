package de.cypdashuhn.rooster_demo.interfaces.page

import de.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import de.cypdashuhn.rooster.ui.interfaces.constructors.PageInterface
import de.cypdashuhn.rooster.ui.items.InterfaceItem
import de.cypdashuhn.rooster.util.createItem
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

@RoosterInterface
object TestPageInterface :
    PageInterface<TestPageInterface.TestPageContext>("TestPageInterface", TestPageContext::class) {
    class TestPageContext(override var page: Int) : PageContext(page)

    override fun initializePages(): List<Page<TestPageContext>> {
        return listOf(
            Page(
                0, listOf(
                    InterfaceItem(
                        slot = 4,
                        itemStackCreator = { createItem(Material.GRAY_STAINED_GLASS_PANE, Component.text("")) }
                    ),
                    InterfaceItem(
                        slot = 15,
                        itemStackCreator = { createItem(Material.GRAY_STAINED_GLASS_PANE, Component.text("")) }
                    ),
                    InterfaceItem(
                        slot = 22,
                        itemStackCreator = { createItem(Material.GRAY_STAINED_GLASS_PANE, Component.text("")) }
                    ),
                    InterfaceItem(
                        slot = 38,
                        itemStackCreator = { createItem(Material.GRAY_STAINED_GLASS_PANE, Component.text("")) }
                    ),
                    InterfaceItem(
                        slot = 52,
                        itemStackCreator = { createItem(Material.GRAY_STAINED_GLASS_PANE, Component.text("")) }
                    )
                )
            ),
            Page(1, listOf(
                InterfaceItem(
                    slot = 4,
                    itemStackCreator = { createItem(Material.GRAY_STAINED_GLASS_PANE, Component.text("")) }
                ),
                InterfaceItem(
                    slot = 12,
                    itemStackCreator = { createItem(Material.GRAY_STAINED_GLASS_PANE, Component.text("")) }
                ),
                InterfaceItem(
                    slot = 32,
                    itemStackCreator = { createItem(Material.GRAY_STAINED_GLASS_PANE, Component.text("")) }
                ),
                InterfaceItem(
                    slot = 41,
                    itemStackCreator = { createItem(Material.GRAY_STAINED_GLASS_PANE, Component.text("")) }
                ),
                InterfaceItem(
                    slot = 46,
                    itemStackCreator = { createItem(Material.GRAY_STAINED_GLASS_PANE, Component.text("")) }
                )
            )),
            Page(2, listOf(
                InterfaceItem(
                    slot = 1,
                    itemStackCreator = { createItem(Material.GRAY_STAINED_GLASS_PANE, Component.text("")) }
                ),
                InterfaceItem(
                    slot = 14,
                    itemStackCreator = { createItem(Material.GRAY_STAINED_GLASS_PANE, Component.text("")) }
                ),
                InterfaceItem(
                    slot = 29,
                    itemStackCreator = { createItem(Material.GRAY_STAINED_GLASS_PANE, Component.text("")) }
                ),
                InterfaceItem(
                    slot = 35,
                    itemStackCreator = { createItem(Material.GRAY_STAINED_GLASS_PANE, Component.text("")) }
                ),
                InterfaceItem(
                    slot = 49,
                    itemStackCreator = { createItem(Material.GRAY_STAINED_GLASS_PANE, Component.text("")) }
                )
            ))
        )
    }

    override fun getInventory(player: Player, context: TestPageContext): Inventory {
        return Bukkit.createInventory(null, 9 * 6, "TestPageInterface")
    }

    override fun defaultContext(player: Player): TestPageContext {
        return TestPageContext(0)
    }
}