package de.cypdashuhn.extendedInventoryPlugin.database

import de.cypdashuhn.rooster.database.RoosterTable
import de.cypdashuhn.rooster.database.findEntry
import de.cypdashuhn.rooster.database.utility_tables.ItemManager
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and

object PositionManager {
    @RoosterTable
    object Positions : IntIdTable() {
        val ownerId = reference("owner_id", PlayerManager.Players, onDelete = ReferenceOption.CASCADE).nullable()
        val x = integer("x")
        val y = integer("y")
        val item = reference("item_id", ItemManager.Items, onDelete = ReferenceOption.CASCADE)
    }

    class Position(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<Position>(Positions)

        var x by Positions.x
        var y by Positions.y
        var ownerId by PlayerManager.DbPlayer optionalReferencedOn Positions.ownerId
        var item by ItemManager.Item referencedOn Positions.item

        fun toDTO(): PositionDTO {
            return PositionDTO(x, y)
        }
    }

    data class PositionDTO(
        var x: Int,
        var y: Int
    ) {
        fun position(owner: PlayerManager.DbPlayer?): Position? {
            return Position.findEntry(Positions.x eq x and (Positions.y eq y) and (Positions.ownerId eq owner?.id))
        }

        val id: Int?
            get() {
                return Position.findEntry(
                    (Positions.x eq this@PositionDTO.x) and (Positions.y eq this@PositionDTO.y)
                )?.id?.value
            }
    }
}