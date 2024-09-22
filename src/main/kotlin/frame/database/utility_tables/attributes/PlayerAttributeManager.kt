package database.utility_tables.attributes

import de.cypdashuhn.rooster.Rooster
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager.Companion.dbPlayer
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement

class PlayerAttributeManager : AttributeManager<Player>() {
    init {
        requireNotNull(Rooster.playerManager) {  "Player Manager must be registered" }
    }

    object PlayerAttributes : Attributes("rooster_player_attributes") {
        val player = reference("player", PlayerManager.Players, onDelete = ReferenceOption.NO_ACTION)
    }
    override fun getAttributesTable(): Attributes = PlayerAttributes

    override fun initializeNewField(row: InsertStatement<*>, value: Player) {
        row[PlayerAttributes.player] = value.dbPlayer().id
    }

    override fun valueToQuery(value: Player): Op<Boolean> {
        return PlayerAttributes.player eq value.dbPlayer().id
    }
}