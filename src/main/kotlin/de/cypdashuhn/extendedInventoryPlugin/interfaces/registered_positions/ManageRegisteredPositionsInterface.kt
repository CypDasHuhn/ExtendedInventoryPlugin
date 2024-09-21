package interfaces.registered_positions

import de.cypdashuhn.rooster.util.createItem
import de.cypdashuhn.extendedInventoryPlugin.database.RegisteredPositionManager
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager.Companion.dbPlayer
import de.cypdashuhn.rooster.localization.tComponent
import de.cypdashuhn.rooster.ui.Context
import de.cypdashuhn.rooster.ui.RoosterInterface
import de.cypdashuhn.rooster.ui.interfaces.Interface
import de.cypdashuhn.rooster.ui.items.InterfaceItem
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.jetbrains.exposed.sql.transactions.transaction

@RoosterInterface
object ManageRegisteredPositionsInterface :
    Interface<ManageRegisteredPositionsInterface.ManageRegisteredPositionsContext>(
        "managed_positions_interface", ManageRegisteredPositionsContext::class
    ) {
    private const val BOTTOM_BAR = 5 * 9
    override fun getInventory(player: Player, context: ManageRegisteredPositionsContext): Inventory {
        return Bukkit.createInventory(null, 6 * 9, tComponent("managed_positions_interface_name", player))
    }

    class ManageRegisteredPositionsContext(
        var row: Int,
        private var ownerId: Int
    ) : Context() {
        val owner
            get() = transaction { PlayerManager.DbPlayer[ownerId] }
    }

    override fun getInterfaceItems(): List<InterfaceItem<ManageRegisteredPositionsContext>> {
        return listOf(
            InterfaceItem(
                condition = { it.slot < BOTTOM_BAR },
                itemStackCreator = { createItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, Component.empty()) }
            ),
            InterfaceItem(
                condition = {
                    if (it.slot >= BOTTOM_BAR) return@InterfaceItem false

                    return@InterfaceItem positionFromContext(it.slot, it.context) != null
                },
                itemStackCreator = {
                    positionFromContext(it.slot, it.context)!!.representingItem
                }
            ),
            InterfaceItem(
                condition = { it.slot >= BOTTOM_BAR },
                itemStackCreator = { createItem(Material.GRAY_STAINED_GLASS_PANE, Component.empty()) }
            ),
        )
    }

    override fun defaultContext(player: Player): ManageRegisteredPositionsContext {
        return ManageRegisteredPositionsContext(row = 1, ownerId = player.dbPlayer().id.value)
    }

    private fun positionFromContext(
        slot: Int,
        context: ManageRegisteredPositionsContext,
    ): RegisteredPositionManager.RegisteredPosition? {
        require(slot < BOTTOM_BAR) { "Needs to be inside the valid range" }

        val position = slot + context.row * 9

        return transaction {
            RegisteredPositionManager.RegisteredPosition.find {
                RegisteredPositionManager.RegisteredPositions.ownerId eq context.owner.id
            }
                .limit(1, offset = (position - 1).toLong()) // Limit to 1, skip (position - 1) records
                .singleOrNull() // Get the single result or null if none exists
        }
    }
}

