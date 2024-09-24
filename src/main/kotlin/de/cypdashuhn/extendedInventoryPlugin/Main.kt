package de.cypdashuhn.extendedInventoryPlugin

import com.google.common.cache.CacheBuilder
import database.utility_tables.attributes.AttributeKey
import database.utility_tables.attributes.PlayerAttributeManager
import de.cypdashuhn.extendedInventoryPlugin.database.PlayerStateManager.insertStateIfMissing
import de.cypdashuhn.extendedInventoryPlugin.database.PositionManager
import de.cypdashuhn.rooster.Rooster
import de.cypdashuhn.rooster.RoosterCache
import de.cypdashuhn.rooster.database.utility_tables.ItemManager
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager.Companion.dbPlayer
import de.cypdashuhn.rooster.localization.DatabaseLocaleProvider
import de.cypdashuhn.rooster.util.createItem
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.concurrent.TimeUnit

class Main : JavaPlugin() {
    companion object {
        val cache = RoosterCache<String, Any>(CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES))
        val playerManager = PlayerManager()
        val itemManager = ItemManager()
        val playerAttributeManager = PlayerAttributeManager()
    }

    val playerTestKey = AttributeKey.boolean("test")
    val playerTestKeyTwo = AttributeKey.customNullable<Int?>("newTest")

    override fun onEnable() {
        Rooster.playerJoin = { event ->
            event.player.insertStateIfMissing()

            playerAttributeManager.set(event.player, playerTestKey, true)
            playerAttributeManager.set(event.player, playerTestKeyTwo, 4)

            val attribute = playerAttributeManager.get(event.player, playerTestKey)
            val attributeTwo = playerAttributeManager.getNullable(event.player, playerTestKeyTwo)

            println("$attribute and $attributeTwo")

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
        Rooster.localeProvider = DatabaseLocaleProvider(listOf("en", "de", "pl"), "en")

        Rooster.initialize(this)
    }
}