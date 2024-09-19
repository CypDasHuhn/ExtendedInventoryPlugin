package commands

import action.toggleItemMode
import database.OwnerManager
import database.OwnerManager.generalOwner
import database.OwnerManager.owner
import de.CypDasHuhn.Rooster.commands.RoosterCommand
import de.CypDasHuhn.Rooster.commands.argument_constructors.ArgumentDetails
import de.CypDasHuhn.Rooster.commands.argument_constructors.ArgumentList
import de.CypDasHuhn.Rooster.commands.argument_constructors.RootArgument
import de.CypDasHuhn.Rooster.commands.argument_constructors.errorMessage
import de.CypDasHuhn.Rooster.commands.utility_argument_constructors.SimpleArgument
import de.CypDasHuhn.Rooster.commands.utility_argument_constructors.SimpleModifierArgument
import de.CypDasHuhn.Rooster.database.PlayerManager.dbPlayer
import de.CypDasHuhn.Rooster.database.PlayerManager.setAndGetPlayerData
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

fun ownerFromInfo(sender: CommandSender, values: HashMap<String, Any?>): OwnerManager.Owner = if (isGeneral(values)) {
    generalOwner
} else {
    (sender as Player).dbPlayer().owner()
}

@Suppress("unused")
@RoosterCommand
val extendedInventoryCommand = RootArgument(
    label = "ei",
    startingUnit = { sender ->
        if (sender !is Player) return@RootArgument false

        setAndGetPlayerData(sender)
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
                    if (currentContext.owner.id.value != owner.id.value) { // reset context
                        currentContext = ItemInterface.ItemInterfaceContext(ownerId = owner.id.value)
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