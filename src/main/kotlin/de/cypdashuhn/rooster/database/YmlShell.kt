package de.cypdashuhn.rooster.database

import de.cypdashuhn.rooster.Rooster
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

interface YmlOperations {
    val file: File
    var config: FileConfiguration

    fun saveConfig() {
        config.save(file)
    }

    fun changeConfig(action: () -> Unit) {
        action()
        saveConfig()
    }
}

class YmlShell(fileName: String) : YmlOperations {
    override val file = File(Rooster.plugin.dataFolder, fileName)
    override var config: FileConfiguration = YamlConfiguration.loadConfiguration(file)
}