package commands

import de.CypDasHuhn.Rooster.commands.argument_constructors.CentralArgument
import de.CypDasHuhn.Rooster.commands.argument_constructors.CentralizedArgumentList
import de.CypDasHuhn.Rooster.commands.argument_constructors.errorMessage
import de.CypDasHuhn.Rooster.commands.argument_constructors.errorMessagePair
import de.CypDasHuhn.Rooster.database.PlayerManager.getPlayerByName
import de.CypDasHuhn.Rooster.database.PlayerManager.getPlayers
import de.CypDasHuhn.Rooster.database.PlayerManager.isAdmin
import de.CypDasHuhn.Rooster.database.PlayerManager.playerExists
import de.CypDasHuhn.Rooster.database.PlayerManager.updateDatabase

const val PLAYER_KEY = "player"

val adminCommandArguments = CentralizedArgumentList(CentralArgument(
    key = PLAYER_KEY,
    tabCompletions = { getPlayers().map { it.username } },
    errorMissing = errorMessage("specify_player"),
    isValidCompleter = { it.sender.isAdmin() },
    isValid = { argInfo ->
        if (!playerExists(argInfo.arg)) errorMessagePair("player_doesnt_exist", "player")
        Pair(true, null)
    },
    invoke = { (_, _, values) ->
        val playerName = values[PLAYER_KEY] as String
        val player = getPlayerByName(playerName) ?: return@CentralArgument

        player.isAdmin = !player.isAdmin
        player.updateDatabase()
    }
))