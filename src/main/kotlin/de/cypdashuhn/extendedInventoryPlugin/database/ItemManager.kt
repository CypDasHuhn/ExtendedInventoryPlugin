import com.google.gson.Gson
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction

object ItemManager {
    object Items : IntIdTable() {
        val itemStack = text("item")
    }

    class Item(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<Item>(Items)

        var itemStack: ItemStack by Items.itemStack.transform(
            { itemStack -> Gson().toJson(itemStack.serialize()) },
            { json -> ItemStack.deserialize(Gson().fromJson(json, Map::class.java) as Map<String, Any>) }
        )
    }

    fun insertOrGetItem(itemStack: ItemStack): Item {
        return transaction {
            val itemStackJson = Gson().toJson(itemStack.serialize())
            val item = Item.find { Items.itemStack eq itemStackJson }.firstOrNull()

            if (item != null) return@transaction item

            return@transaction Item.new {
                this.itemStack = itemStack
            }
        }
    }
}
