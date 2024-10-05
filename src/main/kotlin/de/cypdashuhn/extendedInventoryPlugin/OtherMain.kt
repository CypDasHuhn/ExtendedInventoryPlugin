package de.cypdashuhn.extendedInventoryPlugin

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import de.cypdashuhn.extendedInventoryPlugin.testcommand.TestCommand
import de.cypdashuhn.extendedInventoryPlugin.testcommand.TestCommandCompleter
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.plugin.java.JavaPlugin


class OtherMain : JavaPlugin() {
    override fun onEnable() {
        this.getCommand("test")?.let {
            it.setExecutor(TestCommand())
            it.tabCompleter = TestCommandCompleter()
        }
    }

    fun test() {
        val dispatcher = CommandDispatcher<Any>()

        // Register a simple /hello command

        val s = Commands.literal("give")
            .then(
                Commands.argument<String>("item", StringArgumentType.string())
                    .suggests { context: CommandContext<CommandSourceStack?>?, builder: SuggestionsBuilder? ->
                        context!!.input
                        builder?.buildFuture()
                    }
                    .executes { context: CommandContext<CommandSourceStack?>? ->
                        val item = StringArgumentType.getString(context, "item")
                        println("Giving item: $item")
                        1
                    }
            )

        dispatcher.register(
            s
        )
    }
}