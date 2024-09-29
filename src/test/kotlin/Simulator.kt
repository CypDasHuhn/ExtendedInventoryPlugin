import de.cypdashuhn.rooster.localization.LocaleProvider
import de.cypdashuhn.rooster.localization.SqlLocaleProvider
import de.cypdashuhn.rooster.simulator.RoosterSimulator
import de.cypdashuhn.rooster.simulator.commands.CommandSimulator
import de.cypdashuhn.rooster.simulator.interfaces.InterfaceSimulator
import de.cypdashuhn.rooster_demo.interfaces.scroll.TestScrollInterface
import org.bukkit.event.inventory.ClickType

object MySimulator : RoosterSimulator() {
    override fun getLocaleProvider(): LocaleProvider = SqlLocaleProvider(listOf("en", "de", "pl"), "en")
}

fun main() {
    //MySimulator.startTerminal()

    MySimulator.simulate {
        var result = CommandSimulator.completeCommand("/ei ")
        CommandSimulator.invokeCommand("/ei settings language de")

        val inventory = InterfaceSimulator.openInterface(TestScrollInterface)

        val newInventory = InterfaceSimulator.click(5 * 9 + 7, ClickType.LEFT)

        val s = ""
    }
}