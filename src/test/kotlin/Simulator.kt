import de.cypdashuhn.rooster.Rooster
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager
import de.cypdashuhn.rooster.localization.DatabaseLocaleProvider
import de.cypdashuhn.rooster.simulator.Simulator
import demo.TestData
import demo.TestDataManager

fun main() {
    val playerManager = PlayerManager()
    Rooster.localeProvider = DatabaseLocaleProvider(listOf("en", "de", "pl"), "en")
    Simulator.startSimulator { player ->
        val possibleColors = listOf("red", "green", "blue", "yellow", "lime", "purple")
        repeat(50) {
            val randomColor = possibleColors.random()

            TestDataManager.addTestData(TestData(0, "testName$it", randomColor))
        }
    }
}