package de.cypdashuhn.rooster

import de.cypdashuhn.rooster.localization.LocaleProvider
import de.cypdashuhn.rooster.ui.context.DatabaseInterfaceContextProvider
import de.cypdashuhn.rooster.ui.context.InterfaceContextProvider
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

interface RoosterShell {
    /**
     * Provides the [LocaleProvider] for this plugin, allowing for localization of in-game text.
     *
     * **Usage Example:**
     * ```kotlin
     * return DatabaseLocaleProvider(listOf("en_US", "de_DE"), "en_US")
     * ```
     * If you don't want to use Expose Tables, you can use other Providers.
     * Read more about it here: TODO: Add Doc Link
     */
    fun getLocaleProvider(): LocaleProvider

    /**
     * Provides the [InterfaceContextProvider] for managing plugin interfaces.
     *
     * By default, it returns a [DatabaseInterfaceContextProvider], but you can override it if you don't want to use an Expose Table
     * Read more about it here: TODO: Add Doc Link
     */
    fun getInterfaceContextProvider(): InterfaceContextProvider = DatabaseInterfaceContextProvider()

    /**
     * Called before Rooster initializes. This is where you modify Rooster's core variables.
     *
     * This hook gives you early access to adjust settings before Rooster runs its setup process.
     * Read more about it here: TODO: Add Doc Link
     */
    fun beforeInitialize() {}

    /**
     * Called after Rooster has fully initialized. Ideal for database access or setup logic.
     *
     * You can safely assume all of Rooster's services are available when this is called.
     * Read more about it here: TODO: Add Doc Link
     */
    fun onInitialize() {}


    /**
     * Called before a player is added to the database. Only applies if you have a PlayerManager
     */
    fun beforePlayerJoin(event: PlayerJoinEvent) {}

    /**
     * Called after a player is added to the database, if you have a PlayerManager.
     * If you don't, you can still use it, or create your own Listener.
     */
    fun onPlayerJoin(event: PlayerJoinEvent) {}

    fun initializeRooster(plugin: JavaPlugin) {
        beforeInitialize()
        Rooster.initialize(
            plugin = plugin,
            localeProvider = getLocaleProvider(),
            interfaceContextProvider = getInterfaceContextProvider(),
            beforePlayerJoin = ::beforePlayerJoin,
            onPlayerJoin = ::onPlayerJoin
        )
        onInitialize()
    }
}