import de.cypdashuhn.extendedInventoryPlugin.Main
import de.cypdashuhn.extendedInventoryPlugin.database.PositionManager
import de.cypdashuhn.rooster.Rooster
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager.Companion.dbPlayer
import de.cypdashuhn.rooster.localization.DatabaseLocaleProvider
import de.cypdashuhn.rooster.simulator.Simulator
import de.cypdashuhn.rooster.util.createItem
import org.bukkit.Material
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    val playerManager = PlayerManager()
    Rooster.localeProvider = DatabaseLocaleProvider(listOf("en", "de", "pl"), "en")
    Simulator.startSimulator { player ->
        transaction {
            PositionManager.Position.new {
                x = 0
                y = 0
                ownerId = player.dbPlayer()
                item = Main.itemManager.upsertItem(createItem(Material.STONE_SWORD))
            }
        }
    }
}