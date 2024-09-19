import com.google.common.cache.CacheBuilder
import database.OwnerManager
import database.OwnerManager.insertOwnerIfMissing
import database.PlayerStateManager.insertStateIfMissing
import de.CypDasHuhn.Rooster.RoosterCache
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.TimeUnit

class Main : JavaPlugin() {
    companion object {
        val cache = RoosterCache<String, Any>(CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES))
    }

    override fun onEnable() {
        Rooster.afterPlayerJoin = { event ->
            event.player.insertOwnerIfMissing()
            event.player.insertStateIfMissing()
        }

        Rooster.initialize(this)
        OwnerManager.createGeneralOwnerIfMissing()

        val item = ItemManager.insertOrGetItem(ItemStack(Material.STONE_SWORD))
    }
}