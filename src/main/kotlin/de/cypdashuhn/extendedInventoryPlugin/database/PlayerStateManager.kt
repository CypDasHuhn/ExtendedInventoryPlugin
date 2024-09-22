package de.cypdashuhn.extendedInventoryPlugin.database

import de.cypdashuhn.rooster.database.RoosterTable
import de.cypdashuhn.rooster.database.findEntry
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager.Companion.dbPlayer
import org.bukkit.entity.Player
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction

object PlayerStateManager {
    @RoosterTable
    object PlayerStates : IntIdTable() {
        val playerId = reference("player_id", PlayerManager.Players, onDelete = ReferenceOption.CASCADE)
        val ownerId = reference("owner_id", PlayerManager.Players, onDelete = ReferenceOption.CASCADE).nullable()
        val positionX = integer("position_x")
        val positionY = integer("position_y")
    }

    class PlayerState(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<PlayerState>(PlayerStates)

        var player by PlayerManager.DbPlayer referencedOn PlayerStates.playerId
        var owner by PlayerManager.DbPlayer optionalReferencedOn PlayerStates.ownerId
        var positionX by PlayerStates.positionX
        var positionY by PlayerStates.positionY
    }

    fun Player.state(): PlayerState {
        return transaction { PlayerState.find { PlayerStates.playerId eq this@state.dbPlayer().id }.firstOrNull()!! }
    }

    fun Player.updateState(state: PlayerState) {
        PlayerState.findSingleByAndUpdate(PlayerStates.playerId eq this.dbPlayer().id) {
            it.player = state.player
            it.owner = state.owner
            it.positionX = state.positionX
            it.positionY = state.positionY
        }
    }

    fun Player.insertStateIfMissing() {
        val playerId = this.dbPlayer().id
        transaction {
            if (PlayerState.findEntry(PlayerStates.playerId eq playerId) == null) {
                PlayerStates.insert {
                    it[PlayerStates.playerId] = playerId
                    it[ownerId] = playerId
                    it[positionX] = 0
                    it[positionY] = 0
                }
            }
        }
    }
}