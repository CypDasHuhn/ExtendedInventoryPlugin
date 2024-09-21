package de.cypdashuhn.extendedInventoryPlugin.commands

import de.cypdashuhn.extendedInventoryPlugin.action.jumpToPosition
import de.cypdashuhn.extendedInventoryPlugin.database.PositionManager
import de.cypdashuhn.extendedInventoryPlugin.database.RegisteredPositionManager
import de.cypdashuhn.rooster.commands.argument_constructors.ArgumentDetails
import de.cypdashuhn.rooster.commands.argument_constructors.CentralizedArgumentList
import de.cypdashuhn.rooster.commands.utility_argument_constructors.ListArgument
import de.cypdashuhn.rooster.commands.utility_argument_constructors.NumberArgument
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager.Companion.dbPlayer
import org.bukkit.entity.Player

enum class JumpToType {
    COORD,
    NAME
}

const val TYPE_COMMAND = "type"
const val NAME_COMMAND = "name"

val jumpToCommandArguments = CentralizedArgumentList(
    ListArgument.enum(
        enumClass = JumpToType::class,
        ignoreCase = true,
        key = TYPE_COMMAND,
        errorMissingMessageKey = "jumpto_type_specify",
        errorInvalidMessageKey = "jumpto_type_doesnt_exist",
        argumentDetails = ArgumentDetails(
            CentralizedArgumentList({ argInfo ->

                when (argInfo.values[TYPE_COMMAND] as JumpToType) {
                    JumpToType.NAME -> ListArgument.dbList(
                        entity = RegisteredPositionManager.RegisteredPosition,
                        displayField = RegisteredPositionManager.RegisteredPositions.name,
                        filter = { _, position ->
                            position.owner == (argInfo.sender as Player).dbPlayer()
                        },
                        key = NAME_COMMAND,
                        errorMissingMessageKey = "jumpto_name_specify",
                        errorInvalidMessageKey = "jumpto_name_invalid",
                        argumentDetails = ArgumentDetails(invoke = { (sender, _, values) ->
                            val positionName = values[NAME_COMMAND].toString()
                            val owner = ownerFromInfo(sender, values)
                            (sender as Player).jumpToPosition(owner, positionName)
                        })
                    )

                    JumpToType.COORD -> NumberArgument.xyCoordinates(
                        argumentDetails = ArgumentDetails(invoke = { (sender, _, values) ->
                            val position = PositionManager.PositionDTO(
                                values["X"] as Int,
                                values["Y"] as Int
                            )
                            val owner = ownerFromInfo(sender, values)
                            (sender as Player).jumpToPosition(owner, position)
                        })
                    )
                }
            })
        )
    )
)