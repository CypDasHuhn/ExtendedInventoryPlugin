package de.cypdashuhn.extendedInventoryPlugin.commands

import de.cypdashuhn.rooster.Rooster
import de.cypdashuhn.rooster.commands.argument_constructors.ArgumentDetails
import de.cypdashuhn.rooster.commands.argument_constructors.CentralizedArgumentList
import de.cypdashuhn.rooster.commands.utility_argument_constructors.ListArgument
import de.cypdashuhn.rooster.localization.language
import de.cypdashuhn.rooster.localization.t
import de.cypdashuhn.rooster.localization.tSend
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

const val LANGUAGE_KEY = "language"

enum class Language {
    DE,
    EN,
    PL
}

var languageCommandArguments = CentralizedArgumentList(
    ListArgument.enum(
        enumClass = Language::class,
        ignoreCase = true,
        key = LANGUAGE_KEY,
        errorMissingMessageKey = "specify_language",
        errorInvalidMessageKey = "not_valid_language",
        argKey = "lang",
        argumentDetails = ArgumentDetails(invoke = { (sender, _, values) ->
            val global = values[GENERAL_MODIFIER_KEY] as Boolean
            val lang = values[LANGUAGE_KEY] as Language

            fun langChangeMessage(
                sender: CommandSender,
                language: String?,
                targetLang: Language?,
                global: Boolean,
            ) {
                val scaleKey = if (global) "scale_global" else "scale_your"
                val langKey = "lang_${targetLang.toString().lowercase()}"
                sender.tSend(
                    "set_language",
                    Pair("scale", t(scaleKey, language.toString())),
                    Pair("lang", t(langKey, language.toString()))
                )
            }

            if (global) {
                Rooster.localeProvider!!.changeGlobalLanguage(lang.toString())
                val senderLang = sender.language()
                langChangeMessage(sender, senderLang, lang, true)
            } else {
                Rooster.localeProvider!!.changeLanguage(sender as Player, lang.toString())

                langChangeMessage(sender, lang.toString(), lang, false)
            }
        })
    ),
)
