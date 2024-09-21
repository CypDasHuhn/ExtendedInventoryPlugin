package de.cypdashuhn.extendedInventoryPlugin.commands

import de.cypdashuhn.extendedInventoryPlugin.Main.Companion.playerManager
import de.cypdashuhn.rooster.commands.argument_constructors.CentralArgument
import de.cypdashuhn.rooster.commands.argument_constructors.CentralizedArgumentList
import de.cypdashuhn.rooster.commands.argument_constructors.errorMessage
import de.cypdashuhn.rooster.commands.argument_constructors.errorMessagePair
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager

const val PLAYER_KEY = "player"

val adminCommandArguments = CentralizedArgumentList(CentralArgument(
    key = PLAYER_KEY,
    tabCompletions = { playerManager.players().map { it.name } },
    errorMissing = errorMessage("specify_player"),
    isValidCompleter = { true }, // TODO: Add actuall Admin functionallity
    isValid = { argInfo ->
        if (playerManager.playerByName(argInfo.arg) == null) errorMessagePair("player_doesnt_exist", "player")
        Pair(true, null)
    },
    invoke = { (_, _, values) ->
        val playerName = values[PLAYER_KEY] as String
        val player = playerManager.playerByName(playerName) ?: return@CentralArgument

        // TODO: Add actuall Admin functionallity
    }
))