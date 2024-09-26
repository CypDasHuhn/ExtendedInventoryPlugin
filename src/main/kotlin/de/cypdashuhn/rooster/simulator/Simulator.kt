package de.cypdashuhn.rooster.simulator

import be.seeseemelk.mockbukkit.MockBukkit
import de.cypdashuhn.rooster.Rooster
import de.cypdashuhn.rooster.simulator.commands.CommandSimulator
import de.cypdashuhn.rooster.simulator.interfaces.InterfaceSimulator
import de.cypdashuhn.rooster.ui.interfaces.Context
import de.cypdashuhn.rooster.ui.interfaces.Interface
import net.kyori.adventure.text.Component
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.Inventory

object Simulator {
    var currentInventory: Inventory? = null
    var currentContext: Context? = null
    var currentInterface: Interface<Context>? = null

    fun startSimulator(roosterSimulator: RoosterSimulator) {
        isSimulating = true

        val server = MockBukkit.mock()

        val plugin = MockBukkit.createMockPlugin()

        roosterSimulator.beforeInitialize()
        Rooster.dynamicTables.addAll(Rooster.registeredDemoTables)
        roosterSimulator.initializeRooster(plugin)

        roosterSimulator.onInitialize()

        var player = server.addPlayer()

        val event = PlayerJoinEvent(player, Component.empty())
        roosterSimulator.beforePlayerJoin(event)
        Rooster.playerManager?.playerLogin(player)
        roosterSimulator.onPlayerJoin(event)

        println("Welcome to the Input Simulator. Type commands to simulate input. Type 'exit' to quit.")

        while (true) {
            print("> ")
            val input = readlnOrNull() ?: continue

            values.clear()

            val command = input.split(" ").firstOrNull()
            val args = input.substring((command?.length ?: -1) + 1)

            try {
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
            } catch (e: Exception) {
                e.printStackTrace()
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

    var interfaceName: String
        get() {
            return values["interfaceName"] as String? ?: ""
        }
        set(interfaceName: String) {
            onlyTest {
                values["interfaceName"] = interfaceName
            }
        }

    fun printValues() {
        println("values: ")
        values.forEach { (key, value) ->
            println("# Key: $key | Value: $value")
        }
    }
}