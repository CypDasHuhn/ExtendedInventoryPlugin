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
import de.cypdashuhn.rooster.localization.DatabaseLocaleProvider
import de.cypdashuhn.rooster.localization.LocaleProvider
import de.cypdashuhn.rooster.ui.context.DatabaseInterfaceContextProvider
import de.cypdashuhn.rooster.ui.context.InterfaceContextProvider
import de.cypdashuhn.rooster.ui.interfaces.Interface
import de.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import de.cypdashuhn.rooster_demo.interfaces.DemoManager
import de.cypdashuhn.rooster_demo.interfaces.RoosterDemoManager
import de.cypdashuhn.rooster_demo.interfaces.RoosterDemoTable
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

    var registeredRootArguments: MutableList<RootArgument> = mutableListOf()
    var registeredInterfaces: MutableList<Interface<*>> = mutableListOf()
    var registeredTables: MutableList<Table> = mutableListOf()
    var registeredDemoTables: MutableList<Table> = mutableListOf()
    var registeredDemoManager: MutableList<DemoManager> = mutableListOf()
    var registeredListeners: MutableList<Listener> = mutableListOf()

    var beforePlayerJoin: ((PlayerJoinEvent) -> Unit)? = null
    var onPlayerJoin: ((PlayerJoinEvent) -> Unit)? = null

    val cache = RoosterCache<String, Any>(
        CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES)
    )

    var dynamicTables = mutableListOf<Table>()

    internal lateinit var localeProvider: LocaleProvider
    internal lateinit var interfaceContextProvider: InterfaceContextProvider
    internal var playerManager: PlayerManager? = null

    @Suppress("unused")
    fun initialize(
        plugin: JavaPlugin,
        localeProvider: LocaleProvider = DatabaseLocaleProvider(listOf("en"), "en"),
        interfaceContextProvider: InterfaceContextProvider = DatabaseInterfaceContextProvider(),
        beforePlayerJoin: ((PlayerJoinEvent) -> Unit) = {},
        onPlayerJoin: ((PlayerJoinEvent) -> Unit) = {},
    ) {
        this.beforePlayerJoin = beforePlayerJoin
        this.onPlayerJoin = onPlayerJoin
        this.localeProvider = localeProvider
        this.interfaceContextProvider = interfaceContextProvider

        this.plugin = plugin
        if (databasePath == null) databasePath = plugin.dataFolder.resolve("database.db").absolutePath

        if (!plugin.dataFolder.exists()) {
            plugin.dataFolder.mkdirs()
        }
        initializeInstances()

        val tables = dynamicTables + registeredTables
        initDatabase(tables, databasePath!!)

        // listeners
        val pluginManager = Bukkit.getPluginManager()
        for (listener in registeredListeners) {
            pluginManager.registerEvents(listener, plugin)
        }

        // commands
        registeredRootArguments.forEach { arg ->
            plugin.getCommand(arg.label)?.let {
                it.setExecutor(Command)
                it.tabCompleter = Completer
            }
        }
    }

    private fun initializeInstances() {
        ClassGraph()
            .enableClassInfo()
            .enableAllInfo() // Enable annotation scanning
            .scan().use { scanResult ->
                val lists: List<Triple<KClass<*>, KClass<*>, MutableList<Any>>> = listOf(
                    Triple(RoosterInterface::class, Interface::class, registeredInterfaces as MutableList<Any>),
                    Triple(RoosterTable::class, Table::class, registeredTables as MutableList<Any>),
                    Triple(RoosterDemoTable::class, Table::class, registeredDemoTables as MutableList<Any>),
                    Triple(RoosterDemoManager::class, DemoManager::class, registeredDemoManager as MutableList<Any>),
                    Triple(RoosterListener::class, Listener::class, registeredListeners as MutableList<Any>),
                )

                lists.forEach { (annotationClass, targetClass, instances) ->
                    val info = scanResult.getClassesWithAnnotation(annotationClass.qualifiedName)

                    for (classInfo in info) {
                        try {
                            val clazz = classInfo.loadClass(targetClass.java)

                            val instance = when {
                                clazz.kotlin.objectInstance != null -> {
                                    clazz.kotlin.objectInstance as Any
                                }

                                clazz.kotlin.companionObjectInstance != null -> {
                                    clazz.kotlin.companionObjectInstance as Any
                                }

                                else -> {
                                    clazz.getDeclaredConstructor().newInstance() as Any
                                }
                            }

                            instances.add(instance)
                        } catch (ex: Throwable) {
                            println("Could not load class: ${classInfo.name}, exception: ")
                            ex.printStackTrace()
                        }
                    }
                }

                scanResult
                    .getClassesWithFieldAnnotation(RoosterCommand::class.qualifiedName)
                    .forEach { classInfo ->
                        classInfo.fieldInfo.forEach { fieldInfo ->
                            val field = Class.forName(classInfo.name).getDeclaredField(fieldInfo.name)
                            field.isAccessible = true
                            val fieldValue = field.get(null)
                            if (fieldValue is RootArgument) {
                                registeredRootArguments.add(fieldValue)
                            }
                        }
                    }
            }
    }
}
