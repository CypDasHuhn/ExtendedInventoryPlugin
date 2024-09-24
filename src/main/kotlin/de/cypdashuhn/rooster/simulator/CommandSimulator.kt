package de.cypdashuhn.rooster.simulator

import de.cypdashuhn.rooster.Rooster
import de.cypdashuhn.rooster.commands.ArgumentParser
import de.cypdashuhn.rooster.commands.Completer.withStarting
import org.bukkit.entity.Player

object CommandSimulator {
    fun commandInvoke(command: String, player: Player) {
        val result = command(command, ArgumentParser.CommandParseType.Invocation, player)

        if (result.success) {
            result.invocationLambda()
            Simulator.printValues()
        } else {
            println("An error occurred!")
            println("# ${Simulator.error}")
        }
    }

    fun commandComplete(command: String, player: Player) {
        val result = command(command, ArgumentParser.CommandParseType.TabCompleter, player)

        println("Success: ${result.success}")
        if (result.success) {
            val lastArg = command.split(" ").last()
            println("Completions: ")
            result.tabCompleterList.withStarting(lastArg).forEach {
                println("# $it")
            }
        }
    }

    fun command(command: String, type: ArgumentParser.CommandParseType, player: Player): ArgumentParser.ReturnResult {
        val (label, args) = commandTokenized(command)

        if (!Rooster.rootArguments.any { it.label == label }) {
            println("Unknown command: $command")
            return ArgumentParser.ReturnResult()
        }

        return ArgumentParser.parse(player, label, args, type)
    }

    fun commandTokenized(command: String): Pair<String, Array<String>> {
        var command = command
        if (command.startsWith("/")) {
            command = command.substring(1)
        }
        val parts = command.split(" ")
        val label = parts.firstOrNull() ?: ""
        val args = parts.drop(1).toMutableList()

        return label to args.toTypedArray()
    }
}