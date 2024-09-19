import net.kyori.adventure.text.TextComponent
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

fun createItem(
    material: Material,
    name: TextComponent? = null,
    description: List<TextComponent>? = null,
    amount: Int = 1,
    /** Not implemented */
    nbt: Any? = null,
): ItemStack {
    val item = ItemStack(material, amount)
    val itemMeta = item.itemMeta
    if (name != null) itemMeta.displayName(name)
    if (description != null) itemMeta.lore(description)
    item.itemMeta = itemMeta

    return item
}