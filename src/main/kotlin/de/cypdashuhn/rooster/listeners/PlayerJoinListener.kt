package de.cypdashuhn.rooster.listeners

import de.cypdashuhn.rooster.Rooster
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

@Suppress("unused")
@RoosterListener
object PlayerJoinListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        Rooster.beforePlayerJoin?.let { it(event) }
        Rooster.playerManager?.playerLogin(event.player)
        Rooster.playerJoin?.let { it(event) }
    }
}