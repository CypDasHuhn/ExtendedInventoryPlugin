package database

import de.CypDasHuhn.Rooster.database.InterfaceContextManager
import de.CypDasHuhn.Rooster.database.PlayerManager
import de.CypDasHuhn.Rooster.database.PlayerManager.getPlayerByUUID
import de.CypDasHuhn.Rooster.database.RoosterTable
import de.CypDasHuhn.Rooster.database.findEntry
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object OwnerManager {
    @RoosterTable
    object Owners : IntIdTable() {
        val isPlayer = bool("is_player")
        val player = reference("player_id", PlayerManager.Players, onDelete = ReferenceOption.CASCADE).nullable()
    }

    class Owner(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<Owner>(Owners)

        var isPlayer by Owners.isPlayer
        val player by PlayerManager.Player referencedOn InterfaceContextManager.InterfaceContexts.player
    }

    fun createGeneralOwnerIfMissing() {
        transaction {
            val generalOwner = Owner.findEntry(Owners.isPlayer eq false)
            if (generalOwner == null) {
                Owners.insert {
                    it[isPlayer] = false
                    it[player] = null
                }
            }
        }
    }

    fun org.bukkit.entity.Player.insertOwnerIfMissing() {
        transaction {
            val targetPlayer = getPlayerByUUID(this@insertOwnerIfMissing.uniqueId.toString())!! // after player insert
            val owner = Owner.findEntry(Owners.player eq targetPlayer.id)
            if (owner == null) {
                Owners.insert {
                    it[isPlayer] = true
                    it[player] = targetPlayer.id
                }
            }
        }
    }

    fun PlayerManager.Player?.owner(): Owner {
        if (this == null) return generalOwner
        val owner = Owner.findEntry(Owners.player eq this.id)
        requireNotNull(owner) { "Player Owner instance not initialized!" }
        return owner
    }

    fun PlayerManager.Player?.ownerId(): EntityID<Int> = this.owner().id

    val generalOwner: Owner
        get() = Owner.findEntry(Owners.isPlayer eq false)!!
}
