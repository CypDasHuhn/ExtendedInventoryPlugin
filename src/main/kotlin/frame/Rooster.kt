package de.cypdashuhn.rooster

import com.google.common.cache.CacheBuilder
import de.cypdashuhn.rooster.commands.Command
import de.cypdashuhn.rooster.commands.Completer
import de.cypdashuhn.rooster.commands.RoosterCommand
import de.cypdashuhn.rooster.commands.argument_constructors.RootArgument
import de.cypdashuhn.rooster.database.RoosterTable
import de.cypdashuhn.rooster.database.initDatabase
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager
import de.cypdashuhn.rooster.listeners.RoosterListener
import de.cypdashuhn.rooster.localization.LocaleProvider
import de.cypdashuhn.rooster.ui.RoosterInterface
import de.cypdashuhn.rooster.ui.context.DatabaseInterfaceContextProvider
import de.cypdashuhn.rooster.ui.interfaces.Interface
import io.github.classgraph.ClassGraph
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.exposed.sql.Table
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance

object Rooster {
    lateinit var plugin: JavaPlugin
    var databasePath: String? = null

    lateinit var rootArguments: MutableList<RootArgument>
    lateinit var registeredInterfaces: List<Interface<*>>

    var beforePlayerJoin: ((PlayerJoinEvent) -> Unit)? = null
    var playerJoin: ((PlayerJoinEvent) -> Unit)? = null

    val cache = RoosterCache<String, Any>(
        CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES)
    )

    var dynamicTables = mutableListOf<Table>()

    var localeProvider: LocaleProvider? = null
    var interfaceContextProvider = DatabaseInterfaceContextProvider()


    internal var playerManager: PlayerManager? = null

    @Suppress("unused")
    fun initialize(plugin: JavaPlugin) {
        this.plugin = plugin
        if (databasePath == null) databasePath = plugin.dataFolder.resolve("database.db").absolutePath

        if (!plugin.dataFolder.exists()) {
            plugin.dataFolder.mkdirs()
        }

        val tables = dynamicTables + getTables()
        initDatabase(tables, databasePath!!)

        // listeners
        val pluginManager = Bukkit.getPluginManager()
        for (listener in getListeners()) {
            pluginManager.registerEvents(listener, plugin)
        }

        // commands
        rootArguments = getCommands()
        rootArguments.forEach { arg ->
            plugin.getCommand(arg.label)?.let {
                it.setExecutor(Command)
                it.tabCompleter = Completer
            }
        }

        // interface
        registeredInterfaces = getInterfaces()
    }

    private fun getCommands(): MutableList<RootArgument> {
        val commands = mutableListOf<RootArgument>()
        ClassGraph()
            .enableAllInfo()
            .scan().use { scanResult ->
                scanResult
                    .getClassesWithFieldAnnotation(RoosterCommand::class.qualifiedName)
                    .forEach { classInfo ->
                        classInfo.fieldInfo.forEach { fieldInfo ->
                            val field = Class.forName(classInfo.name).getDeclaredField(fieldInfo.name)
                            field.isAccessible = true
                            val fieldValue = field.get(null)
                            if (fieldValue is RootArgument) {
                                commands.add(fieldValue)
                            }
                        }
                    }
            }
        return commands
    }

    private fun getInterfaces() = getAnnotatedInstances(RoosterInterface::class, Interface::class)
    private fun getListeners() = getAnnotatedInstances(RoosterListener::class, Listener::class)
    private fun getTables() = getAnnotatedInstances(RoosterTable::class, Table::class)

    private fun <T: Any> getAnnotatedInstances(kClass: KClass<*>, targetClass: KClass<T>): List<T> {
        val instances = mutableListOf<T>()

        ClassGraph()
            .enableClassInfo()
            .enableAnnotationInfo() // Enable annotation scanning
            .scan().use { scanResult ->
                val info = scanResult.getClassesWithAnnotation(kClass.qualifiedName)

                for (classInfo in info) {
                    try {
                        val clazz = classInfo.loadClass(targetClass.java)

                        val instance = when {
                            clazz.kotlin.objectInstance != null -> {
                                clazz.kotlin.objectInstance as T
                            }

                            clazz.kotlin.companionObjectInstance != null -> {
                                clazz.kotlin.companionObjectInstance as T
                            }

                            else -> {
                                clazz.getDeclaredConstructor().newInstance() as T
                            }
                        }
                        instances.add(instance)
                    } catch (ex: Throwable) {
                        println("Could not load class: ${classInfo.name}, exception: ${ex.message}")
                    }
                }
            }

        return instances
    }
}
