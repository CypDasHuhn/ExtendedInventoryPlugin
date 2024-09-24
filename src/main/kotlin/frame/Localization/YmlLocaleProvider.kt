package frame.Localization

import de.cypdashuhn.rooster.Rooster
import de.cypdashuhn.rooster.localization.Language
import de.cypdashuhn.rooster.localization.LocaleProvider
import de.cypdashuhn.rooster.util.uuid
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

class YmlLocaleProvider(
    override var locales: List<Language>,
    override var defaultLocale: Language
) : LocaleProvider(locales, defaultLocale) {
    private val file = File(Rooster.plugin.dataFolder, "languages.yml")
    private var config: FileConfiguration = YamlConfiguration.loadConfiguration(file)
    private fun saveConfig() {
        config.save(file)
    }

    private val playerKey = "PlayerLanguages"
    private val generalKey = "GlobalLanguage"

    override fun playerLanguage(player: Player): Language? {
        val map = config.getMapList(playerKey) as MutableMap<String, String>
        return map[player.uuid()]
    }

    override fun changeLanguage(player: Player, language: Language) {
        val map = config.getMapList(playerKey) as MutableMap<String, String>
        map[player.uuid()] = language
        saveConfig()
        Rooster.plugin.saveConfig()
    }

    override fun getGlobalLanguage(): Language {
        config.getString(generalKey)?.let { return it } ?: return defaultLocale
    }

    override fun changeGlobalLanguage(language: Language) {
        config.set(generalKey, language)
        saveConfig()
    }
}