import de.cypdashuhn.rooster.localization.LocaleProvider
import de.cypdashuhn.rooster.localization.SqlLocaleProvider
import de.cypdashuhn.rooster.simulator.RoosterSimulator

object MySimulator : RoosterSimulator() {
    override fun getLocaleProvider(): LocaleProvider = SqlLocaleProvider(listOf("en", "de", "pl"), "en")
}

fun main() {
    MySimulator.startTerminal()

    /*MySimulator.simulate {
        var result = CommandSimulator.completeCommand("/ei ")
        CommandSimulator.invokeCommand("/ei settings language de")

        val inventory = InterfaceSimulator.openInterface(TestScrollInterface)

        val newInventory = InterfaceSimulator.click(5 * 9 + 7, ClickType.LEFT)

        val s = ""
    }*/
}