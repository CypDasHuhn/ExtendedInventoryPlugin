package commands

import action.jumpToPosition
import database.OwnerManager.owner
import database.PositionManager
import database.RegisteredPositionManager
import de.CypDasHuhn.Rooster.commands.argument_constructors.ArgumentDetails
import de.CypDasHuhn.Rooster.commands.argument_constructors.CentralizedArgumentList
import de.CypDasHuhn.Rooster.commands.utility_argument_constructors.ListArgument
import de.CypDasHuhn.Rooster.commands.utility_argument_constructors.NumberArgument
import de.CypDasHuhn.Rooster.database.PlayerManager.dbPlayer
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
                            position.owner == (argInfo.sender as Player).dbPlayer().owner()
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