import de.cypdashuhn.rooster.localization.DatabaseLocaleProvider
import de.cypdashuhn.rooster.localization.LocaleProvider
import de.cypdashuhn.rooster.simulator.RoosterSimulator
import de.cypdashuhn.rooster.simulator.commands.CommandSimulator

object MySimulator : RoosterSimulator() {
    override fun getLocaleProvider(): LocaleProvider = DatabaseLocaleProvider(listOf("en", "de", "pl"), "en")
}

fun main() {
    //MySimulator.startTerminal()

    MySimulator.simulate(preserveFolder = true) {
        var result = CommandSimulator.completeCommand("/ei ")
        CommandSimulator.invokeCommand("/ei settings language de")

        val s = ""
    }
}