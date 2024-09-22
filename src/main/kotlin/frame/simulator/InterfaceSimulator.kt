package de.cypdashuhn.rooster.simulator

import de.cypdashuhn.rooster.Rooster
import de.cypdashuhn.rooster.ui.Click
import de.cypdashuhn.rooster.ui.Context
import de.cypdashuhn.rooster.ui.InterfaceManager
import de.cypdashuhn.rooster.ui.interfaces.Interface
import de.cypdashuhn.rooster.util.createItem
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

object InterfaceSimulator {
    private fun Material.short(): String {
        val nameTokenized = this.name.split("_").toMutableList()
        var result = ""
        repeat(3) {
            result += nameTokenized.first().firstOrNull()
            if (nameTokenized.size == 1) {
                nameTokenized[0] = nameTokenized.first().substring(1)
            } else {
                nameTokenized.removeFirst()
            }
        }

        return result
    }

    enum class OpenInterfaceResults {
        OK,
        NAME_NOT_FOUND,
        CONTEXT_NOT_FITTING
    }

    fun parseOpening(command: String, player: Player) {
        val interfaceName = command.split(" ").first()

        val targetInterface =
            Rooster.registeredInterfaces
                .firstOrNull { it.interfaceName.equals(interfaceName, ignoreCase = true) } as Interface<Context>?

        if (targetInterface == null) {
            println("Unknown interface: $interfaceName")
            return
        }

        var contextCommand: String? = command.drop(interfaceName.length + 1).trim()
        if (contextCommand?.isEmpty() != false) contextCommand = null

        openInterface(targetInterface, contextCommand, player)
    }

    fun openInterface(
        targetInterface: Interface<Context>,
        contextString: String?,
        player: Player
    ): OpenInterfaceResults {
        var context: Context

        if (contextString == null) {
            context = targetInterface.defaultContext(player)
        } else {
            val (messageString, resultContext) = contextFromText(contextString, targetInterface)
            context = resultContext ?: targetInterface.defaultContext(player)
            if (resultContext == null) println(messageString)
        }

        val inventory = InterfaceManager.getInventory(targetInterface, context, player)

        Simulator.currentInventory = inventory
        Simulator.currentContext = context
        Simulator.currentInterface = targetInterface

        if (inventory.type == InventoryType.CHEST) {
            val rows = inventory.contents.size / 9

            for (row in 0 until rows) {
                for (slot in 0 until 9) {
                    val item = inventory.getItem(slot + row * 9) ?: createItem(Material.AIR)
                    val short = item.type.short()
                    print("[$short] ")
                }
                println("")
            }
        }

        return OpenInterfaceResults.OK
    }

    fun parseShow(command: String, player: Player) {
        var slot = parseSlot(command) ?: return

        if (Simulator.currentInventory == null) {
            println("No current inventory!")
            return
        }

        if (slot < 0 || slot >= Simulator.currentInventory!!.size) {
            println("Slot is out of bounds!")
            return
        }

        val item = Simulator.currentInventory?.getItem(slot) ?: createItem(Material.AIR)
        // print every detaill of item
        item.serialize().forEach { println(it) }
    }

    fun parseClick(command: String, player: Player) {
        var slot = parseSlot(command) ?: return

        if (Simulator.currentInventory == null) {
            println("No current inventory!")
            return
        }

        if (slot < 0 || slot >= Simulator.currentInventory!!.size) {
            println("Slot is out of bounds!")
            return
        }

        player.openInventory(Simulator.currentInventory!!)
        val event = InventoryClickEvent(player.openInventory, InventoryType.SlotType.CONTAINER, slot, ClickType.LEFT, InventoryAction.NOTHING )
        val item = Simulator.currentInventory!!.getItem(slot)
        val click = Click(event, player, item, item?.type, event.slot)
        InterfaceManager.click(click, event, Simulator.currentInterface!!, player)
    }

    fun parseSlot(command: String): Int? {
        var slot = command.split(" ").first().toIntOrNull()
        if (slot == null) {
            // alternative x-y parsing
            val xy = command.split(" ").last().split("-")

            if (xy.size != 2) {
                println("Invalid slot format!")
                return null
            }

            var x = xy[0].toIntOrNull()
            var y = xy[1].toIntOrNull()

            if (x == null || y == null) {
                println("Invalid slot format!")
                return null
            }

            x -= 1
            y -= 1
            slot = x+(y*9)
        }
        return slot
    }

    fun contextFromText(contextCommand: String, targetInterface: Interface<Context>): Pair<String, Context?> {
        var contextCommand = contextCommand
        if (contextCommand.startsWith("{") && contextCommand.endsWith("}")) {
            contextCommand = contextCommand.drop(1).dropLast(1).trim()

            // Parse field assignments from the command string
            val fieldAssignments = contextCommand
                .split(",")
                .map { it.trim() }
                .map { it.split("=").let { Pair(it[0].trim(), it[1].trim()) } }
                .map { (fieldName, fieldValue) ->
                    val field = targetInterface.contextClass.memberProperties
                        .firstOrNull { it.name.equals(fieldName, ignoreCase = true) }
                        ?: return@map null to null

                    val parsedValue = parseValue(field.returnType.classifier as? KClass<*>, fieldValue)
                    field to parsedValue
                }
                .filter { it.first != null && it.second != null }
                .toMap()

            val context =
                targetInterface.contextClass.primaryConstructor?.call()
                    ?: return "Failed to create context instance" to null

            fieldAssignments.forEach { (field, value) ->
                (field as? KMutableProperty1<Any, Any>)?.let {
                    it.isAccessible = true
                    it.call(context, value) // Use call for setting properties
                }
            }

            return "success" to context
        }
        return "no context" to null
    }

    private fun parseValue(type: KClass<*>?, value: String): Any? {
        if (value.startsWith("[") && value.endsWith("]")) {
            val elements = value.drop(1).dropLast(1)
                .split(",")
                .map { it.trim() }

            return when (type) {
                Int::class -> elements.mapNotNull { it.toIntOrNull() }
                Boolean::class -> elements.map { it.toBoolean() }
                Double::class -> elements.mapNotNull { it.toDoubleOrNull() }
                Float::class -> elements.mapNotNull { it.toFloatOrNull() }
                else -> elements
            }
        }

        return when (type) {
            Int::class -> value.toIntOrNull()
            Boolean::class -> value.toBoolean()
            Double::class -> value.toDoubleOrNull()
            Float::class -> value.toFloatOrNull()
            else -> value
        }
    }
}