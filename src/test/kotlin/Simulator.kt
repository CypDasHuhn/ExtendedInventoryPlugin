import de.cypdashuhn.rooster.Rooster
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager
import de.cypdashuhn.rooster.localization.DatabaseLocaleProvider
import de.cypdashuhn.rooster.simulator.Simulator

fun main() {
    val playerManager = PlayerManager()
    Rooster.localeProvider = DatabaseLocaleProvider(listOf("en", "de", "pl"), "en")
    Simulator.startSimulator()
}