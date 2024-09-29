package de.cypdashuhn.rooster.database.utility_tables

import de.cypdashuhn.rooster.core.Rooster
import org.jetbrains.exposed.sql.Table

abstract class UtilityDatabase {
    init {
        Rooster.dynamicTables += mainDatabase()
    }

    protected abstract fun mainDatabase(): Table
}