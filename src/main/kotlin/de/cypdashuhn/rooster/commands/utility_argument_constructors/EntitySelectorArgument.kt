package de.cypdashuhn.rooster.commands.utility_argument_constructors

import de.cypdashuhn.rooster.commands.argument_constructors.ArgumentDetails
import de.cypdashuhn.rooster.commands.argument_constructors.CentralArgument
import de.cypdashuhn.rooster.commands.argument_constructors.ErrorLambda
import de.cypdashuhn.rooster.util.location
import de.cypdashuhn.rooster.util.nearest
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/** Generally unfinished in every aspect. just ignore! */
@Suppress("unused")
object EntitySelectorArgument {
    enum class PlayerSelector(selectorCharacter: String, playerSelectorLambda: (CommandSender) -> List<Player>) {
        SELF("s", {
            if (it is Player) listOf(it)
            else listOf()
        }),
        NEAR("p", {
            if (it is Player) listOf(it)
            else {
                val location = it.location()
                if (location != null) {
                    listOf(location.world.players.nearest(location))
                } else listOf()
            }
        }),
        ALL("a", {
            val location = it.location()
            location?.world?.players ?: listOf()
        }),
        RANDOM("r", {
            val location = it.location()
            if (location != null) {
                listOf(location.world.players.random())
            } else listOf()
        });

        var selectorName: String
        var playerSelectorLambda: (CommandSender) -> List<Player>

        init {
            this.selectorName = "@$selectorCharacter"
            this.playerSelectorLambda = playerSelectorLambda
        }
    }

    fun playerSelector(
        key: String,
        errorMissing: ErrorLambda,
        argumentDetails: ArgumentDetails,
        playerSelectors: List<PlayerSelector> = PlayerSelector.values().toList(),
        selectionType: SelectionType = SelectionType.BOTH,
    ): CentralArgument {
        return CentralArgument(
            key = key,
            tabCompletions = { argInfo ->
                val selectables = mutableListOf<String>()
                if (selectionType.flags) {
                    selectables.addAll(playerSelectors
                        .filter {
                            // exclude @s if sender is not a player
                            if (argInfo.sender !is Player) it != PlayerSelector.SELF
                            else true
                        }
                        .map { it.selectorName })
                }
                if (selectionType.names) {
                    val location = argInfo.sender.location() ?: return@CentralArgument listOf()
                    selectables.addAll(location.world.players.map { it.name })
                }
                selectables
            },
            argumentDetails = argumentDetails,
            errorMissing = errorMissing,
            argumentHandler = { argInfo ->
                try {
                    val playerSelector = PlayerSelector.valueOf(argInfo.arg.lowercase())
                    return@CentralArgument playerSelector.playerSelectorLambda(argInfo.sender)
                } catch (e: EnumConstantNotPresentException) {
                    val location = argInfo.sender.location() ?: return@CentralArgument argInfo.arg
                    val player = location.world.players.first { it.name == argInfo.arg }
                    return@CentralArgument player
                }
            }
        )
    }

    enum class SelectionType(names: Boolean, flags: Boolean) {
        PLAYER_NAMES(true, false),
        PLAYER_FLAGS(false, true),
        BOTH(true, true);

        var names: Boolean
        var flags: Boolean

        init {
            this.names = names
            this.flags = flags
        }
    }

}

