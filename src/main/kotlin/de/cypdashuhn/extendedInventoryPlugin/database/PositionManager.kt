package database

import ItemManager
import de.CypDasHuhn.Rooster.database.RoosterTable
import de.CypDasHuhn.Rooster.database.findEntry
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
        val owner = reference("owner_id", OwnerManager.Owners, onDelete = ReferenceOption.CASCADE)
        val x = integer("x")
        val y = integer("y")
        val item = reference("item_id", ItemManager.Items, onDelete = ReferenceOption.CASCADE)
    }

    class Position(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<Position>(Positions)

        var x by Positions.x
        var y by Positions.y
        var owner by OwnerManager.Owner referencedOn Positions.owner
        var item by ItemManager.Item referencedOn Positions.item

        fun toDTO(): PositionDTO {
            return PositionDTO(x, y)
        }
    }

    data class PositionDTO(
        var x: Int,
        var y: Int
    ) {
        fun position(owner: OwnerManager.Owner): Position? {
            return Position.findEntry(Positions.x eq x and (Positions.y eq y) and (Positions.owner eq owner.id))
        }

        val id: Int?
            get() {
                return Position.findEntry(
                    (Positions.x eq this@PositionDTO.x) and (Positions.y eq this@PositionDTO.y)
                )?.id?.value
            }
    }
}