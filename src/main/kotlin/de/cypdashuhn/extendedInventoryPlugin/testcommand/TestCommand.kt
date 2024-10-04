package de.cypdashuhn.extendedInventoryPlugin.testcommand

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class TestCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if (args?.getOrNull(0)?.startsWith("t") == true) {
            sender.sendMessage("lol!")
            return true
        } else return false
    }
}

class TestCommandCompleter : TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>?
    ): MutableList<String>? {
        return if (args?.getOrNull(0)?.startsWith("t") == true) {
            mutableListOf("test", "test2", "test3")
        } else null
    }

}