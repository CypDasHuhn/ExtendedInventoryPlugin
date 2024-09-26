import de.cypdashuhn.rooster.localization.DatabaseLocaleProvider
import de.cypdashuhn.rooster.localization.LocaleProvider
import de.cypdashuhn.rooster.simulator.RoosterSimulator

object MySimulator : RoosterSimulator() {
    override fun getLocaleProvider(): LocaleProvider = DatabaseLocaleProvider(listOf("en", "de", "pl"), "en")
}

fun main() {
    MySimulator.start()
}