package de.cypdashuhn.extendedInventoryPlugin

import de.cypdashuhn.extendedInventoryPlugin.testcommand.TestCommand
import de.cypdashuhn.extendedInventoryPlugin.testcommand.TestCommandCompleter
import org.bukkit.plugin.java.JavaPlugin

class OtherMain : JavaPlugin() {
    override fun onEnable() {
        this.getCommand("test")?.let {
            it.setExecutor(TestCommand())
            it.tabCompleter = TestCommandCompleter()
        }
    }
}