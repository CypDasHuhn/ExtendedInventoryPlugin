package de.cypdashuhn.rooster.localization

import org.bukkit.entity.Player

abstract class LocaleProvider(open var locales: List<Language>, open var defaultLocale: Language) {

    protected abstract fun playerLanguage(player: Player): Language?

    abstract fun changeLanguage(player: Player, language: Language)

    abstract fun getGlobalLanguage(): Language

    abstract fun changeGlobalLanguage(language: Language)

    fun getLanguage(player: Player): Language {
        return playerLanguage(player) ?: getGlobalLanguage()
    }
}

typealias Language = String