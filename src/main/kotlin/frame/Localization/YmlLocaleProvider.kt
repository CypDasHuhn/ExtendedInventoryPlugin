package frame.Localization

import de.cypdashuhn.rooster.Rooster
import de.cypdashuhn.rooster.localization.Language
import de.cypdashuhn.rooster.localization.LocaleProvider
import de.cypdashuhn.rooster.util.uuid
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player

class YmlLocaleProvider(
    override var locales: List<Language>,
    override var defaultLocale: Language
) : LocaleProvider(locales, defaultLocale) {
    var config: FileConfiguration = Rooster.plugin.config
    fun map() = config.getMapList("PlayerLanguages") as MutableMap<String, String>

    override fun playerLanguage(player: Player): Language? {
        val map = map()
        return map[player.uuid()]
    }

    override fun changeLanguage(player: Player, language: Language) {
        val map = map()
        map[player.uuid()] = language
        Rooster.p
    }

    override fun getGlobalLanguage(): Language {
        TODO("Not yet implemented")
    }

    override fun changeGlobalLanguage(language: Language) {
        TODO("Not yet implemented")
    }

}