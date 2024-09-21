package de.cypdashuhn.extendedInventoryPlugin

import com.google.common.cache.CacheBuilder
import de.cypdashuhn.extendedInventoryPlugin.database.PlayerStateManager.insertStateIfMissing
import de.cypdashuhn.rooster.Rooster
import de.cypdashuhn.rooster.RoosterCache
import de.cypdashuhn.rooster.database.utility_tables.ItemManager
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager
import de.cypdashuhn.rooster.localization.DatabaseLocaleProvider
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.TimeUnit

class Main : JavaPlugin() {
    companion object {
        val cache = RoosterCache<String, Any>(CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES))
        val playerManager = PlayerManager()
        val itemManager = ItemManager()
    }

    override fun onEnable() {
        Rooster.playerJoin = { event ->
            event.player.insertStateIfMissing()
        }
        Rooster.localeProvider = DatabaseLocaleProvider(listOf("en", "de", "pl"), "en")

        Rooster.initialize(this)
    }
}