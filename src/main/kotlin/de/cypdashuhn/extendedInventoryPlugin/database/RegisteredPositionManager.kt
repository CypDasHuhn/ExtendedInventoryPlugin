package database

import ItemManager
import OptionalTypeAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import database.OwnerManager.ownerId
import de.CypDasHuhn.Rooster.database.PlayerManager
import de.CypDasHuhn.Rooster.database.PlayerManager.dbPlayer
import de.CypDasHuhn.Rooster.database.RoosterTable
import de.CypDasHuhn.Rooster.database.findEntry
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.*

object RegisteredPositionManager {
    @RoosterTable
    object RegisteredPositions : IntIdTable() {
        val owner = reference("owner_id", OwnerManager.Owners, onDelete = ReferenceOption.CASCADE)
        val name = varchar("name", 50)
        val position = reference("position", PositionManager.Positions, onDelete = ReferenceOption.CASCADE)
        val representingItem = reference("representing_item", ItemManager.Items, onDelete = ReferenceOption.CASCADE)
    }

    class RegisteredPosition(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<RegisteredPosition>(RegisteredPositions)

        var owner by OwnerManager.Owner referencedOn RegisteredPositions.owner
        var position by PositionManager.Position referencedOn RegisteredPositions.position
        var name by RegisteredPositions.name
        var representingItem: ItemStack by ItemManager.Items.itemStack.transform(
            { gson.toJson(it) },
            { gson.fromJson(it, ItemStack::class.java) }
        )
    }

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Optional::class.java, OptionalTypeAdapter<Any>())
        .create()

    fun renameRegisteredPosition(registeredPosition: RegisteredPosition, newName: String) {
        transaction {
            RegisteredPositions.update({ RegisteredPositions.name eq registeredPosition.name }) {
                it[name] = newName
            }
        }
    }

    fun registerPosition(
        player: PlayerManager.Player?,
        newName: String,
        correspondedPosition: PositionManager.Position
    ) {
        transaction {
            RegisteredPositions.insert {
                it[owner] = player.ownerId()
                it[position] = correspondedPosition.id
                it[name] = newName
            }
        }
    }

    fun registeredPositionExists(player: org.bukkit.entity.Player, name: String): Boolean {
        return positionFromName(player, name) != null
    }

    fun positionFromName(player: org.bukkit.entity.Player, name: String): RegisteredPosition? {
        return RegisteredPosition.findEntry(
            RegisteredPositions.name eq name and (RegisteredPositions.owner eq player.dbPlayer().ownerId())
        )
    }
}