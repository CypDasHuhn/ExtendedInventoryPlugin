@file:Suppress("unused")

package de.cypdashuhn.rooster.localization

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import de.cypdashuhn.rooster.core.Rooster.cache
import de.cypdashuhn.rooster.core.Rooster.localeProvider
import de.cypdashuhn.rooster.core.config.RoosterOptions
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

object Localization {
    fun getLocalizedMessage(
        language: Language?,
        messageKey: String,
        vararg replacements: Pair<String, String?>
    ): String {
        val language = language ?: localeProvider.getGlobalLanguage()

        var message = cache.get("$language-$messageKey", null, {
            val resourcePath = "/locales/${language.lowercase()}.json"
            val inputStream = javaClass.getResourceAsStream(resourcePath)
                ?: throw FileNotFoundException("Resource not found: $resourcePath")

            val gson = Gson()
            val type = object : TypeToken<Map<String, String>>() {}.type
            val localization: Map<String, String> =
                gson.fromJson(InputStreamReader(inputStream, StandardCharsets.UTF_8), type)

            localization[messageKey] ?: "Message not found".also {
                RoosterOptions.Warnings.LOCALIZATION_MISSING_LOCALE.warn(messageKey to language)
            }
        }, 60, TimeUnit.SECONDS)

        for ((key, value) in replacements) {
            message = message.replace("\${$key}", value ?: "")
        }

        return message
    }
}

data class ReplacementKey(val key: String)

fun t(messageKey: String, language: Language?, vararg replacements: Pair<String, String?>): TextComponent {
    return Component.text(Localization.getLocalizedMessage(language, messageKey, *replacements))
}

fun t(messageKey: String, player: Player, vararg replacements: Pair<String, String?>): TextComponent {
    return player.t(messageKey, *replacements)
}

fun Player.t(messageKey: String, vararg replacements: Pair<String, String?>): TextComponent {
    return Component.text(
        Localization.getLocalizedMessage(
            localeProvider.getLanguage(this),
            messageKey,
            *replacements
        )
    )
}

fun tString(messageKey: String, language: Language?, vararg replacements: Pair<String, String?>): String {
    return Localization.getLocalizedMessage(language, messageKey, *replacements)
}

fun tString(messageKey: String, player: Player, vararg replacements: Pair<String, String?>): String {
    return player.tString(messageKey, *replacements)
}

fun Player.tString(messageKey: String, vararg replacements: Pair<String, String?>): String {
    return tString(messageKey, this.language(), *replacements)
}

fun Player.tOtherSignature(messageKey: String, vararg replacements: Pair<String, ReplacementKey>): TextComponent {
    return Component.text(
        Localization.getLocalizedMessage(
            localeProvider.getLanguage(this),
            messageKey,
            *replacements.map { it.first to this.tString(it.second.key) }.toTypedArray()
        )
    )
}

fun CommandSender.tSend(messageKey: String, vararg replacements: Pair<String, String?>) {
    this.sendMessage(t(messageKey, this.language(), *replacements))
}

fun CommandSender.language(): Language {
    return if (this is Player) localeProvider.getLanguage(this)
    else localeProvider.getGlobalLanguage()
}

fun CommandSender.locale(): Locale {
    return Locale(this.language())
}

class Locale(var language: Language?) {

    private val actualLocale: Language by lazy { language ?: localeProvider.getGlobalLanguage() }
    fun t(messageKey: String, vararg replacements: Pair<String, String?>): String {
        return Localization.getLocalizedMessage(actualLocale, messageKey, *replacements)
    }

    fun tSend(sender: CommandSender, messageKey: String, vararg replacements: Pair<String, String?>) {
        sender.sendMessage(t(messageKey, *replacements))
    }
}

