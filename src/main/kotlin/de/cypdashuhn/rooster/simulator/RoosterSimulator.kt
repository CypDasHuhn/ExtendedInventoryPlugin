package de.cypdashuhn.rooster.simulator

import de.cypdashuhn.rooster.RoosterShell
import org.bukkit.plugin.java.JavaPlugin

abstract class RoosterSimulator : RoosterShell {
    fun start() {
        Simulator.startSimulator(this)
    }

    final override fun initializeRooster(plugin: JavaPlugin) = super.initializeRooster(plugin)
}