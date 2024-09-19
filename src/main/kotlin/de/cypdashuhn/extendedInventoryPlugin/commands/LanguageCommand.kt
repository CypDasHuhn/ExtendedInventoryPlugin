package commands

import de.CypDasHuhn.Rooster.commands.argument_constructors.ArgumentDetails
import de.CypDasHuhn.Rooster.commands.argument_constructors.CentralizedArgumentList
import de.CypDasHuhn.Rooster.commands.utility_argument_constructors.ListArgument
import de.CypDasHuhn.Rooster.database.PlayerManager
import de.CypDasHuhn.Rooster.database.PlayerManager.globalLanguage
import de.CypDasHuhn.Rooster.database.PlayerManager.language
import de.CypDasHuhn.Rooster.database.PlayerManager.setAndGetPlayerData
import de.CypDasHuhn.Rooster.database.PlayerManager.updateDatabase
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.transactions.transaction
import t
import tSend

const val LANGUAGE_KEY = "language"

var languageCommandArguments = CentralizedArgumentList(
    ListArgument.enum(
        enumClass = PlayerManager.Language::class,
        ignoreCase = true,
        key = LANGUAGE_KEY,
        errorMissingMessageKey = "specify_language",
        errorInvalidMessageKey = "not_valid_language",
        argKey = "lang",
        argumentDetails = ArgumentDetails(invoke = { (sender, _, values) ->
            val global = values[GENERAL_MODIFIER_KEY] as Boolean
            val lang = values[LANGUAGE_KEY] as PlayerManager.Language

            fun langChangeMessage(
                sender: CommandSender,
                language: PlayerManager.Language?,
                targetLang: PlayerManager.Language?,
                global: Boolean,
            ) {
                val scaleKey = if (global) "scale_global" else "scale_your"
                val langKey = "lang_${targetLang.toString().lowercase()}"
                sender.tSend(
                    "set_language",
                    Pair("scale", t(scaleKey, language)),
                    Pair("lang", t(langKey, language))
                )
            }

            if (global) {
                globalLanguage = lang
                val senderLang = sender.language()
                langChangeMessage(sender, senderLang, lang, true)
            } else {
                val player = setAndGetPlayerData(sender as Player)
                transaction { player.language = lang }
                player.updateDatabase()

                langChangeMessage(sender, lang, lang, false)
            }
        })
    ),
)
