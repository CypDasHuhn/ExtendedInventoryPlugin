package database

import database.OwnerManager.ownerId
import de.CypDasHuhn.Rooster.database.PlayerManager
import de.CypDasHuhn.Rooster.database.PlayerManager.dbPlayer
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

object PlayerStateManager {
    @RoosterTable
    object PlayerStates : IntIdTable() {
        val player = reference("player_id", PlayerManager.Players, onDelete = ReferenceOption.CASCADE)
        val owner = reference("current_owner_id", OwnerManager.Owners)
        val positionX = integer("position_x")
        val positionY = integer("position_y")
    }

    class PlayerState(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<PlayerState>(PlayerStates)

        var player by PlayerManager.Player referencedOn PlayerStates.player
        var owner by OwnerManager.Owner referencedOn PlayerStates.owner
        var positionX by PlayerStates.positionX
        var positionY by PlayerStates.positionY
    }

    fun PlayerManager.Player.state(): PlayerState {
        return transaction { PlayerState.find { PlayerStates.player eq this@state.id }.firstOrNull()!! }
    }

    fun PlayerManager.Player.updateState(state: PlayerState) {
        PlayerState.findSingleByAndUpdate(PlayerStates.player eq this.id) {
            it.player = state.player
            it.owner = state.owner
            it.positionX = state.positionX
            it.positionY = state.positionY
        }
    }

    fun org.bukkit.entity.Player.insertStateIfMissing() {
        val player = this.dbPlayer()
        transaction {
            if (PlayerState.findEntry(PlayerStates.player eq player.id) == null) {
                PlayerStates.insert {
                    it[PlayerStates.player] = player.id
                    it[owner] = player.ownerId()
                    it[positionX] = 0
                    it[positionY] = 0
                }
            }
        }
    }
}