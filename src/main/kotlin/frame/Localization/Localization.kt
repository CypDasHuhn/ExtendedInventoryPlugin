@file:Suppress("unused")

package de.cypdashuhn.rooster.localization

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import de.cypdashuhn.rooster.Rooster
import de.cypdashuhn.rooster.Rooster.cache
import de.cypdashuhn.rooster.Rooster.localeProvider
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

object Localization {
    fun getLocalizedMessage(
        language: Language?,
        messageKey: String,
        vararg replacements: Pair<String, String?>
    ): String {
        requireNotNull(localeProvider) { "LocaleProvider not set" }
        val language = language ?: localeProvider!!.getGlobalLanguage()

        var message = cache.get("$language-$messageKey", null, {
            val resourcePath = "/locales/${language.lowercase()}.json"
            val inputStream = javaClass.getResourceAsStream(resourcePath)
                ?: throw FileNotFoundException("Resource not found: $resourcePath")

            val gson = Gson()
            val type = object : TypeToken<Map<String, String>>() {}.type
            val localization: Map<String, String> =
                gson.fromJson(InputStreamReader(inputStream, StandardCharsets.UTF_8), type)

            localization[messageKey] ?: "Message not found".also {
                Rooster.plugin.logger.warning("ROOSTER: Message not found: $messageKey, for language $language")
            }
        }, 60 * 1000)

        for ((key, value) in replacements) {
            message = message.replace("\${$key}", value ?: "")
        }

        return message
    }
}

fun t(messageKey: String, language: Language?, vararg replacements: Pair<String, String?>): String {
    return Localization.getLocalizedMessage(language, messageKey, *replacements)
}

fun t(messageKey: String, player: Player, vararg replacements: Pair<String, String?>): String {
    requireNotNull(localeProvider) { "LocaleProvider not set" }
    return Localization.getLocalizedMessage(localeProvider!!.getLanguage(player), messageKey, *replacements)
}

fun tComponent(messageKey: String, language: Language?, vararg replacements: Pair<String, String?>): TextComponent {
    return Component.text(Localization.getLocalizedMessage(language, messageKey, *replacements))
}

fun tComponent(messageKey: String, player: Player, vararg replacements: Pair<String, String?>): TextComponent {
    requireNotNull(localeProvider) { "LocaleProvider not set" }
    return Component.text(
        Localization.getLocalizedMessage(
            localeProvider!!.getLanguage(player),
            messageKey,
            *replacements
        )
    )
}

fun CommandSender.tSendWLanguage(messageKey: String, language: Language?, vararg replacements: Pair<String, String>) {
    this.sendMessage(t(messageKey, language, *replacements))
}

fun CommandSender.tSend(messageKey: String, vararg replacements: Pair<String, String?>) {
    this.sendMessage(t(messageKey, this.language(), *replacements))
}

fun CommandSender.language(): Language {
    requireNotNull(localeProvider) { "LocaleProvider not set" }
    return if (this is Player) localeProvider!!.getLanguage(this)
    else localeProvider!!.getGlobalLanguage()
}

class Locale(var language: Language?) {
    init {
        requireNotNull(localeProvider) { "LocaleProvider not set" }
    }

    private val actualLocale: Language by lazy { language ?: localeProvider!!.getGlobalLanguage() }
    fun t(messageKey: String, vararg replacements: Pair<String, String?>): String {
        return Localization.getLocalizedMessage(actualLocale, messageKey, *replacements)
    }

    fun tSend(sender: CommandSender, messageKey: String, vararg replacements: Pair<String, String?>) {
        sender.sendMessage(t(messageKey, *replacements))
    }
}

fun CommandSender.locale(): Locale {
    return Locale(this.language())
}
