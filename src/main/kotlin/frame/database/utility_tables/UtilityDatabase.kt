package de.cypdashuhn.rooster.database.utility_tables

import de.cypdashuhn.rooster.Rooster
import org.jetbrains.exposed.sql.Table

abstract class UtilityDatabase {
    init {
        Rooster.dynamicTables += mainDatabase()
    }
    abstract fun mainDatabase(): Table
}