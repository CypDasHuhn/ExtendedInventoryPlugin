package de.cypdashuhn.rooster.simulator

import be.seeseemelk.mockbukkit.MockBukkit
import de.cypdashuhn.rooster.Rooster
import de.cypdashuhn.rooster.ui.Context
import de.cypdashuhn.rooster.ui.interfaces.Interface
import org.bukkit.inventory.Inventory

object Simulator {
    var currentInventory: Inventory? = null
    var currentContext: Context? = null
    var currentInterface: Interface<Context>? = null

    fun startSimulator() {
        isSimulating = true

        val server = MockBukkit.mock()

        val plugin = MockBukkit.createMockPlugin()
        Rooster.initialize(plugin)

        var player = server.addPlayer()
        Rooster.playerManager!!.playerLogin(player)

        println("Welcome to the Input Simulator. Type commands to simulate input. Type 'exit' to quit.")

        while (true) {
            print("> ")
            val input = readlnOrNull() ?: continue

            values.clear()

            val command = input.split(" ").firstOrNull()
            val args = input.substring((command?.length ?: -1)+1)

            when (command) {
                "exit" -> {
                    println("Exiting the simulator.")
                    break
                }
                "complete" -> {
                    CommandSimulator.commandComplete(args, player)
                }

                "invoke" -> {
                    CommandSimulator.commandInvoke(args, player)
                }

                "open" -> {
                    InterfaceSimulator.parseOpening(args, player)
                }

                "show" -> {
                    InterfaceSimulator.parseShow(args, player)
                }

                "click" -> {
                    InterfaceSimulator.parseClick(args, player)
                }

                else -> println("Unknown command: $input")
            }
        }
    }

    private var isSimulating = false
    fun nonTest(block: () -> Unit) {
        if (!isSimulating) {
            block()
        }
    }

    fun onlyTest(block: () -> Unit) {
        if (isSimulating) {
            block()
        }
    }

    val values = mutableMapOf<String, Any>()

    var error: String
        get() {
            return values["error"] as String? ?: "no-error"
        }
        set(error: String) {
            onlyTest {
                values["error"] = error
            }
        }

    var path: String
        get() {
            return values["path"] as String? ?: "no-path"
        }
        set(path: String) {
            onlyTest {
                values["path"] = path
            }
        }

    fun printValues() {
        println("values: ")
        values.forEach { (key, value) ->
            println("# Key: $key | Value: $value")
        }
    }
}