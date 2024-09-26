import de.cypdashuhn.rooster.material.MaterialGroup
import de.cypdashuhn.rooster.material.MaterialSelector

fun main() {
    val element = MaterialSelector(MaterialGroup.MINERAL)
    val all = element.get()
    val withoutOre = element.exclude(MaterialGroup.ORE).get()
    val withoutBlocks = element.exclude(
        MaterialGroup.ORE,
        MaterialGroup.MATERIAL_BLOCK,
        MaterialGroup.LEGACY
    ).get()

    val s = element.require(MaterialGroup.ARMOR).get()

    println("")
}