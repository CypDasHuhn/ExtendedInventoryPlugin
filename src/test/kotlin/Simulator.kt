import de.cypdashuhn.extendedInventoryPlugin.Main
import de.cypdashuhn.extendedInventoryPlugin.database.PositionManager
import de.cypdashuhn.rooster.Rooster
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager.Companion.dbPlayer
import de.cypdashuhn.rooster.localization.DatabaseLocaleProvider
import de.cypdashuhn.rooster.material.MaterialGroup
import de.cypdashuhn.rooster.material.MaterialSelector
import de.cypdashuhn.rooster.material.SelectorType
import de.cypdashuhn.rooster.simulator.Simulator
import de.cypdashuhn.rooster.util.createItem
import de.cypdashuhn.rooster_demo.interfaces.graph.GraphDataManager
import de.cypdashuhn.rooster_demo.interfaces.init
import de.cypdashuhn.rooster_demo.interfaces.scroll.ScrollDataManager
import org.bukkit.Material

fun main() {
    val playerManager = PlayerManager()
    Rooster.localeProvider = DatabaseLocaleProvider(listOf("en", "de", "pl"), "en")

    val materials = MaterialSelector.get(SelectorType.INCLUDE to MaterialGroup.IS_COLOR, SelectorType.REQUIRE to MaterialGroup.IS_FLOWER, SelectorType.EXCLUDE to MaterialGroup.IS_PINK)
    val materialsa = MaterialSelector().include(MaterialGroup.ALL).exclude(MaterialGroup.IS_NETHER).require(MaterialGroup.IS_BRICK)
    //val materials = MaterialSelector().include(MaterialGroup.IS_RED).get()
    println(materials.size)

    /*
    Simulator.startSimulator { player ->
        listOf(ScrollDataManager, GraphDataManager).init()

        PositionManager.Position.new {
            x = 0
            y = 0
            ownerId = event.player.dbPlayer()
            item = Main.itemManager.upsertItem(createItem(Material.STONE_SWORD))
        }
        PositionManager.Position.new {
            x = -2
            y = 1
            ownerId = event.player.dbPlayer()
            item = Main.itemManager.upsertItem(createItem(Material.ACACIA_LOG))
        }
        PositionManager.Position.new {
            x = 8
            y = 4
            ownerId = event.player.dbPlayer()
            item = Main.itemManager.upsertItem(createItem(Material.REDSTONE))
        }
        PositionManager.Position.new {
            x = -1
            y = -3
            ownerId = event.player.dbPlayer()
            item = Main.itemManager.upsertItem(createItem(Material.GLOWSTONE))
        }
    }*/
}