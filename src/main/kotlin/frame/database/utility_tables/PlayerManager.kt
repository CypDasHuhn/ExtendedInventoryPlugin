package de.cypdashuhn.rooster.database.utility_tables

import de.cypdashuhn.rooster.Rooster
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager.Players.uuid
import org.bukkit.Bukkit
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import de.cypdashuhn.rooster.util.uuid
import org.bukkit.entity.Player

/**
 * Not Completely Necessary. Use BukkitAPI instead. This manager is if your
 * call frequency exceeds API Limitations, or whatever else you'd like to
 * do.
 */
open class PlayerManager : UtilityDatabase() {
    override fun mainDatabase() = Players

    init {
        this.also { Rooster.playerManager = it }
    }

    object Players : IntIdTable() {
        val uuid = varchar("uuid", 36)
        val name = varchar("name", 16)
        val lastLogin = long("last_login")
    }

    class DbPlayer(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<DbPlayer>(Players)

        var uuid by Players.uuid
        var name by Players.name
        var lastLogin by Players.lastLogin

        val online
            get() = bukkitPlayer != null

        val bukkitPlayer: Player?
            get() = Bukkit.getWorlds().flatMap { it.players }.first { it.uuid() == uuid }
    }


    fun playerLogin(player: Player) {
        return transaction {
            DbPlayer.find { uuid eq player.uuid() }.firstOrNull()?.delete()

            Players.insert {
                it[uuid] = player.uuid()
                it[name] = player.name
                it[lastLogin] = player.lastLogin
            }
        }
    }

    fun playerByUUID(uuid: String): DbPlayer? {
        return transaction { DbPlayer.find { Players.uuid eq uuid }.firstOrNull() }
    }

    fun playerByName(name: String): DbPlayer? {
        return transaction { DbPlayer.find { Players.name eq name }.firstOrNull() }
    }

    fun players(): List<DbPlayer> {
        return transaction { DbPlayer.all().toList() }
    }

    companion object {
        fun Player.dbPlayer(): DbPlayer {
            requireNotNull(Rooster.playerManager) { "Player Manager must be registered" }
            return Rooster.playerManager!!.playerByUUID(this.uuid())!!
        }
    }
}