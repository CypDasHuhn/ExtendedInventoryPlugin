import de.cypdashuhn.rooster.Rooster
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager
import de.cypdashuhn.rooster.localization.DatabaseLocaleProvider
import de.cypdashuhn.rooster.simulator.Simulator
import de.cypdashuhn.rooster_demo.interfaces.graph.GraphDataManager
import de.cypdashuhn.rooster_demo.interfaces.init
import de.cypdashuhn.rooster_demo.interfaces.scroll.ScrollDataManager

fun main() {
    val playerManager = PlayerManager()
    Rooster.localeProvider = DatabaseLocaleProvider(listOf("en", "de", "pl"), "en")

    Simulator.startSimulator { player ->
        listOf(ScrollDataManager, GraphDataManager).init()
    }
}