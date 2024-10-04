package interfaces

import de.cypdashuhn.extendedInventoryPlugin.Main.Companion.cache
import de.cypdashuhn.extendedInventoryPlugin.Main.Companion.playerManager
import de.cypdashuhn.extendedInventoryPlugin.database.PositionManager
import de.cypdashuhn.rooster.database.findEntry
import de.cypdashuhn.rooster.database.utility_tables.PlayerManager
import de.cypdashuhn.rooster.listeners.ClickState
import de.cypdashuhn.rooster.listeners.hasClicks
import de.cypdashuhn.rooster.localization.t
import de.cypdashuhn.rooster.simulator.Simulator
import de.cypdashuhn.rooster.ui.interfaces.Context
import de.cypdashuhn.rooster.ui.interfaces.Interface
import de.cypdashuhn.rooster.ui.interfaces.InterfaceInfo
import de.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import de.cypdashuhn.rooster.ui.items.InterfaceItem
import de.cypdashuhn.rooster.util.createItem
import interfaces.ItemInterface.ItemInterfaceContext
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.math.max
import kotlin.math.min

@RoosterInterface
object ItemInterface : Interface<ItemInterfaceContext>("item_interface", ItemInterfaceContext::class) {
    private const val BOTTOM_BAR_START = 5 * 9
    private const val BOTTOM_BAR_END = 6 * 9
    private const val START = 0

    private const val REGION_SHIFT_CACHE_POS1 = "region_shift_pos1"
    private const val REGION_SHIFT_CACHE_POS2 = "region_shift_pos2"

    override fun getInventory(player: Player, context: ItemInterfaceContext): Inventory {
        Simulator.interfaceName = "Item Interface #${context.position.x}-${context.position.y}"
        return Bukkit.createInventory(null, 6 * 9, "Item Interface #${context.position.x}-${context.position.y}")
    }

    enum class InterfaceMode {
        NAVIGATE,
        EDIT
    }

    enum class RegionShiftMode {
        START,
        POS1_SELECTED,
        POS2_SELECTED
    }

    class ItemInterfaceContext(
        var position: PositionManager.PositionDTO = PositionManager.PositionDTO(0, 0),
        var ownerId: Int?,
        var mode: InterfaceMode = InterfaceMode.NAVIGATE,
        var isItemMoving: Boolean = false,
        var regionShiftMode: RegionShiftMode? = null
    ) : Context() {
        val owner
            get() = transaction { PlayerManager.DbPlayer.findEntry(PlayerManager.Players.id eq ownerId) }
    }

    private const val MOVED_ITEMS_CACHE_KEY = "item_interface_move_item_cache"

    override fun getInterfaceItems(): List<InterfaceItem<ItemInterfaceContext>> {
        return listOf(
            InterfaceItem( // registered items
                condition = { (slot, rawContext, _) ->
                    if (slot !in (START until BOTTOM_BAR_START)) return@InterfaceItem false
                    val position = slotToPosition(slot, rawContext)
                    position != null
                },
                itemStackCreator = { (slot, rawContext, _) ->
                    val position = slotToPosition(slot, rawContext)

                    transaction { position!!.item.itemStack }
                },
                action = { (click, context, event) ->
                    event.isCancelled = false
                    if (context.isItemMoving) {
                        return@InterfaceItem
                    }
                    if (context.regionShiftMode != null) {
                        event.isCancelled = true
                        when (context.regionShiftMode) {
                            RegionShiftMode.START -> {
                                cache.put(REGION_SHIFT_CACHE_POS1, click.player, slotToPosition(click.slot, context)!!)
                                openInventory(click.player, context.also {
                                    it.regionShiftMode = RegionShiftMode.POS1_SELECTED
                                })
                            }

                            RegionShiftMode.POS1_SELECTED -> {
                                cache.put(REGION_SHIFT_CACHE_POS2, click.player, slotToPosition(click.slot, context)!!)
                                openInventory(click.player, context.also {
                                    it.regionShiftMode = RegionShiftMode.POS2_SELECTED
                                })
                            }

                            RegionShiftMode.POS2_SELECTED -> {
                                val targetPosition = slotToPosition(click.slot, context)!!
                                val pos1 =
                                    cache.getIfPresent(
                                        REGION_SHIFT_CACHE_POS1,
                                        click.player
                                    ) as PositionManager.PositionDTO
                                val pos2 =
                                    cache.getIfPresent(
                                        REGION_SHIFT_CACHE_POS2,
                                        click.player
                                    ) as PositionManager.PositionDTO
                                val maxPos = PositionManager.PositionDTO(max(pos1.x, pos2.x), max(pos1.y, pos2.y))
                                val minPos = PositionManager.PositionDTO(min(pos1.x, pos2.x), min(pos1.y, pos2.y))

                                val rangeX = maxPos.x - minPos.x
                                val rangeY = maxPos.y - minPos.y

                                for (x in targetPosition.x until targetPosition.x + rangeX) {
                                    for (y in targetPosition.y until targetPosition.y + rangeY) {
                                        val currentPosition = PositionManager.PositionDTO(x, y)
                                        if (currentPosition.position(context.owner) != null) {
                                            TODO("LOCALIZATION - invalid target region")
                                        }
                                    }
                                } // range is valid (clear)

                                cache.invalidate(REGION_SHIFT_CACHE_POS1, click.player)

                                TODO("ACTION - shift items in region")
                            }

                            null -> {} // not reachable
                        }

                        return@InterfaceItem
                    }
                }
            ),
            InterfaceItem(
                // bottom row
                condition = { it.slot in (BOTTOM_BAR_START..BOTTOM_BAR_END) },
                itemStackCreator = { createItem(Material.GRAY_STAINED_GLASS_PANE, Component.text("")) },
            ),
            InterfaceItem( // navigation item
                condition = { it.slot == BOTTOM_BAR_START },
                itemStackCreator = {
                    createItem(
                        Material.COMPASS,
                        t("item_interface_navigation", it.player)
                    )
                },
                action = { (click, context, event) ->
                    if (context.isItemMoving) {
                        val map = cache.get(
                            MOVED_ITEMS_CACHE_KEY,
                            click.player,
                            { mutableMapOf<PositionManager.PositionDTO, ItemStack?>() })
                        for (i in 0 until BOTTOM_BAR_START) {
                            val pos = slotToPositionDTO(i, context)
                            map[pos] = event.inventory.getItem(i)
                        }

                        cache.put(MOVED_ITEMS_CACHE_KEY, click.player, map)
                    }

                    if (event.hasClicks(ClickState.LEFT_NORMAL_CLICK)) context.position.x--
                    else if (event.hasClicks(ClickState.RIGHT_NORMAL_CLICK)) context.position.x++
                    else if (event.hasClicks(ClickState.LEFT_SHIFT_CLICK)) context.position.y--
                    else if (event.hasClicks(ClickState.RIGHT_SHIFT_CLICK)) context.position.y++


                    openInventory(click.player, context)
                }
            ),
            InterfaceItem( // Mode navigation [ navigate -> edit]
                condition = slotAndModeCondition(9, InterfaceMode.NAVIGATE),
                itemStackCreator = {
                    createItem(
                        Material.SPYGLASS,
                        t("item_interface_switch_edit", it.player)
                    )
                },
                action = { (click, context, _) ->
                    openInventory(click.player, context.also { it.mode = InterfaceMode.EDIT })
                }
            ),
            InterfaceItem( // Mode navigation [ edit -> navigate]
                condition = slotAndModeCondition(9, InterfaceMode.EDIT),
                itemStackCreator = {
                    createItem(
                        Material.IRON_PICKAXE,
                        t("item_interface_switch_navigate", it.player)
                    )
                },
                action = { (click, context, _) ->
                    openInventory(click.player, context.also { it.mode = InterfaceMode.NAVIGATE })
                }
            ),
            // ##### NAVIGATE ITEMS #####
            InterfaceItem( // jump to coordinates
                condition = slotAndModeCondition(3, InterfaceMode.NAVIGATE),
                itemStackCreator = {
                    createItem(
                        Material.IRON_BOOTS,
                        t("item_interface_jumpto_coord", it.player)
                    )
                },
                action = { TODO("INTERFACE - Anvil GUI & Coordinates") }
            ),
            InterfaceItem( // jump to name
                condition = slotAndModeCondition(4, InterfaceMode.NAVIGATE),
                itemStackCreator = {
                    createItem(
                        Material.GOLDEN_BOOTS,
                        t("item_interface_jumpto_name", it.player)
                    )
                },
                action = { TODO("INTERFACE - Anvil GUI & Registered Position Names") }
            ),
            InterfaceItem( // filter attributes
                condition = slotAndModeCondition(5, InterfaceMode.NAVIGATE),
                itemStackCreator = {
                    createItem(
                        Material.REPEATER,
                        t("item_interface_filter", it.player)
                    )
                },
                action = { TODO("INTERFACE - Filter attributes") }
            ),
            // ##### EDIT ITEMS #####
            InterfaceItem( // create new registered position
                condition = slotAndModeCondition(3, InterfaceMode.EDIT),
                itemStackCreator = {
                    createItem(
                        Material.OAK_SIGN,
                        t("item_interface_create_position", it.player)
                    )
                },
                action = { TODO("ACTION - create new registered position") }
            ),
            InterfaceItem( // Manage Existing registered positions
                condition = slotAndModeCondition(4, InterfaceMode.EDIT),
                itemStackCreator = {
                    createItem(
                        Material.BOOK,
                        t("item_interface_manage_positions", it.player)
                    )
                },
                action = { TODO("INTERFACE - manage registered positions") }
            ),
            InterfaceItem( // Region Shift
                condition = slotAndModeCondition(5, InterfaceMode.EDIT),
                itemStackCreator = {
                    createItem(
                        Material.BOOK,
                        t("item_interface_region_shift", it.player)
                    )
                },
                action = { (click, context, _) ->
                    openInventory(click.player, context.also { it.regionShiftMode = RegionShiftMode.START })
                }
            ),
            InterfaceItem( // Move Mode
                condition = slotAndModeCondition(6),
                itemStackCreator = {
                    createItem(
                        Material.BOOK,
                        t("item_interface_move", it.player)
                    )
                },
                action = { (click, context, _) ->
                    openInventory(click.player, context.also { it.isItemMoving = true })
                }
            ),
            InterfaceItem( // Cancel Move Mode
                condition = slotAndModeCondition(6, isItemMovingMode = true),
                itemStackCreator = {
                    createItem(
                        Material.BOOK,
                        t("item_interface_move_finish", it.player)
                    )
                },
                action = { (click, context, _) ->

                    openInventory(click.player, context.also { it.isItemMoving = false })
                }
            )
        )
    }

    override fun defaultContext(player: Player): ItemInterfaceContext {
        return ItemInterfaceContext(ownerId = playerManager.playerByName(player.name)!!.id.value)
    }

    private fun transformToRelativeCoordinates(value: Int): Pair<Int, Int> {
        require(value in 0..44) { "Value must be in the range 0 to 44." }

        val centerX = 22
        val centerY = 9

        val x = (value % centerY) - (centerX % centerY)
        val y = (value / centerY) - (centerX / centerY)

        return Pair(x, y)
    }

    fun slotToPosition(slot: Int, context: ItemInterfaceContext): PositionManager.Position? {
        val (x, y) = transformToRelativeCoordinates(slot)
        val position = context.position

        val newPosition = PositionManager.PositionDTO(position.x + x, position.y + y)

        val newPos = newPosition.position(context.owner)
        return newPos
    }

    fun slotToPositionDTO(slot: Int, context: ItemInterfaceContext): PositionManager.PositionDTO {
        val (x, y) = transformToRelativeCoordinates(slot)
        val position = context.position

        return PositionManager.PositionDTO(position.x + x, position.y + y)
    }

    private fun slotAndModeCondition(
        targetSlot: Int,
        targetInterfaceMode: InterfaceMode? = null,
        isItemMovingMode: Boolean = false,
        furtherCondition: (InterfaceInfo<ItemInterfaceContext>) -> Boolean = { true },
    ): (InterfaceInfo<ItemInterfaceContext>) -> Boolean {
        return { (slot, context, player) ->
            slot == BOTTOM_BAR_START + targetSlot - 1 &&
                    (targetInterfaceMode == null || context.mode == targetInterfaceMode) &&
                    isItemMovingMode == context.isItemMoving &&
                    furtherCondition(InterfaceInfo(slot, context, player))
        }
    }
}
