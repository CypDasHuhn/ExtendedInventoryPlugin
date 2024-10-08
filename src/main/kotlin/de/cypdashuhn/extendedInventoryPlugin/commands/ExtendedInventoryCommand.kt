package de.cypdashuhn.extendedInventoryPlugin.commands

import de.cypdashuhn.extendedInventoryPlugin.action.toggleItemMode
import de.cypdashuhn.rooster.commands.RoosterCommand
import de.cypdashuhn.rooster.commands.argument_constructors.ArgumentDetails
import de.cypdashuhn.rooster.commands.argument_constructors.ArgumentList
import de.cypdashuhn.rooster.commands.argument_constructors.RootArgument
import de.cypdashuhn.rooster.commands.argument_constructors.errorMessage
import de.cypdashuhn.rooster.commands.utility_argument_constructors.SimpleArgument
import de.cypdashuhn.rooster.commands.utility_argument_constructors.SimpleModifierArgument
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager.Companion.dbPlayer
import interfaces.ItemInterface
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

const val SETTINGS_COMMAND = "settings"
const val LANGUAGE_COMMAND_KEY = "language"
const val ADMIN_COMMAND_KEY = "admin"
const val INTERFACE_COMMAND_KEY = "interface"
const val JUMP_TO_COMMAND_KEY = "jumpTo"
const val GENERAL_MODIFIER_KEY = "-general"

fun isGeneral(values: HashMap<String, Any?>): Boolean {
    return values[GENERAL_MODIFIER_KEY] as Boolean
}

fun ownerFromInfo(sender: CommandSender, values: HashMap<String, Any?>): PlayerManager.DbPlayer? = if (isGeneral(values)) {
    null
} else {
    (sender as Player).dbPlayer()
}


@Suppress("unused")
@RoosterCommand
val extendedInventoryCommand = RootArgument(
    label = "ei",
    startingUnit = { sender ->
        if (sender !is Player) return@RootArgument false

        true
    },
    invoke = { (sender, _, _) ->
        (sender as Player).toggleItemMode()
    },
    errorMissingChildArg = errorMessage("extended_inventory_missing_child"),
    followingArguments = ArgumentList(
        SimpleModifierArgument.simple(
            name = GENERAL_MODIFIER_KEY
        ),
        SimpleArgument.simple(
            name = SETTINGS_COMMAND,
            argumentDetails = ArgumentDetails(
                errorMissingChildArg = errorMessage("specify_setting"),
                followingArguments = ArgumentList(
                    SimpleArgument.simple(
                        name = LANGUAGE_COMMAND_KEY,
                        argumentDetails = ArgumentDetails(followingArguments = languageCommandArguments)
                    ),
                    SimpleArgument.simple(
                        name = ADMIN_COMMAND_KEY,
                        argumentDetails = ArgumentDetails(followingArguments = adminCommandArguments)
                    )
                )
            )
        ),
        SimpleArgument.simple(
            name = INTERFACE_COMMAND_KEY,
            argumentDetails = ArgumentDetails(
                invoke = { (sender, _, values) ->
                    var currentContext = ItemInterface.getContext(
                        sender as Player,
                    )

                    val owner = ownerFromInfo(sender, values)
                    if (currentContext.ownerId != owner?.id?.value) { // reset context
                        currentContext = ItemInterface.ItemInterfaceContext(ownerId = owner?.id?.value)
                    }

                    ItemInterface.openInventory(sender, currentContext)
                }
            )
        ),
        SimpleArgument.simple(
            name = JUMP_TO_COMMAND_KEY,
            argumentDetails = ArgumentDetails(followingArguments = jumpToCommandArguments)
        )
    )
)