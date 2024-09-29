package de.cypdashuhn.extendedInventoryPlugin

import com.google.common.cache.CacheBuilder
import database.utility_tables.attributes.PlayerAttributeManager
import de.cypdashuhn.extendedInventoryPlugin.database.PlayerStateManager.insertStateIfMissing
import de.cypdashuhn.extendedInventoryPlugin.database.PositionManager
import de.cypdashuhn.rooster.RoosterCache
import de.cypdashuhn.rooster.core.RoosterPlugin
import de.cypdashuhn.rooster.database.utility_tables.ItemManager
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager.Companion.dbPlayer
import de.cypdashuhn.rooster.localization.LocaleProvider
import de.cypdashuhn.rooster.localization.SqlLocaleProvider
import de.cypdashuhn.rooster.util.createItem
import de.cypdashuhn.rooster_demo.interfaces.graph.GraphDataManager
import de.cypdashuhn.rooster_demo.interfaces.init
import de.cypdashuhn.rooster_demo.interfaces.page.PageDataManager
import de.cypdashuhn.rooster_demo.interfaces.scroll.ScrollDataManager
import org.bukkit.Material
import org.bukkit.event.player.PlayerJoinEvent
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.TimeUnit

class Main : RoosterPlugin() {
    companion object {
        val cache = RoosterCache<String, Any>(CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES))
        val playerManager = PlayerManager()
        val itemManager = ItemManager()
        val playerAttributeManager = PlayerAttributeManager()
    }

    override fun getLocaleProvider(): LocaleProvider {
        return SqlLocaleProvider(listOf("en", "de", "pl"), "en")
    }

    override fun onPlayerJoin(event: PlayerJoinEvent) {
        event.player.insertStateIfMissing()

        transaction {
            PositionManager.Position.new {
                x = 0
                y = 0
                ownerId = event.player.dbPlayer()
                item = itemManager.upsertItem(createItem(Material.STONE_SWORD))
            }
            PositionManager.Position.new {
                x = -2
                y = 1
                ownerId = event.player.dbPlayer()
                item = itemManager.upsertItem(createItem(Material.ACACIA_LOG))
            }
            PositionManager.Position.new {
                x = 8
                y = 4
                ownerId = event.player.dbPlayer()
                item = itemManager.upsertItem(createItem(Material.REDSTONE))
            }
            PositionManager.Position.new {
                x = -1
                y = -3
                ownerId = event.player.dbPlayer()
                item = itemManager.upsertItem(createItem(Material.GLOWSTONE))
            }
        }
    }

    override fun onInitialize() {
        transaction {
            listOf(ScrollDataManager, GraphDataManager, PageDataManager).init()
        }
    }
}