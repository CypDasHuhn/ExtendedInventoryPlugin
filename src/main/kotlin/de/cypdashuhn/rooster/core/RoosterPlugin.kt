package de.cypdashuhn.rooster.core

import org.bukkit.plugin.java.JavaPlugin

/**
 * The usual way of registering a Rooster Plugin.
 *
 * This class provides key hooks for integrating with Rooster's
 * initialization lifecycle.
 *
 * If you need to create your plugin without extending RoosterPlugin, read
 * here about the alternative:
 *
 * TODO: Add Doc Link
 */
abstract class RoosterPlugin : JavaPlugin(), RoosterShell {
    final override fun onEnable() {
        initializeRooster(this)
    }

    final override fun initializeRooster(plugin: JavaPlugin) = super.initializeRooster(plugin)
}
