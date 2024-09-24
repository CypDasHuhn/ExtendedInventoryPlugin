package de.cypdashuhn.rooster.material

import de.cypdashuhn.rooster.util.and
import de.cypdashuhn.rooster.util.andNot
import de.cypdashuhn.rooster.util.or
import org.bukkit.Material

enum class MaterialGroup {
    ALL({ true }),
    NON_AIR({ it != Material.AIR }),
    IS_BLOCK({ it.isBlock }),
    IS_ITEM({ it.isItem }),

    IS_COLOR,

    IS_FLOWER,

    IS_TULIP(endsWith("TULIP"), IS_FLOWER),

    IS_MUSHROOM(contains("MUSHROOM")),

    IS_SANDY_BLOCK(contains("SAND")),

    IS_BRICK(contains("BRICK")),

    IS_NETHER(contains("NETHER")),

    IS_NETHER_BRICKS(all(IS_BRICK, IS_NETHER)),

    IS_WHITE(startsWith("WHITE_"), IS_COLOR),
    IS_LIGHT_GRAY(startsWith("LIGHT_GRAY_"), IS_COLOR),
    IS_GRAY(startsWith("GRAY_"), IS_COLOR),
    IS_BLACK(startsWith("BLACK_"), IS_COLOR),
    IS_BROWN(startsWith("BROWN_"), IS_COLOR),
    IS_RED(startsWith("RED_") andNot anyOf(IS_TULIP, IS_MUSHROOM, IS_SANDY_BLOCK, IS_NETHER_BRICKS), IS_COLOR),
    IS_ORANGE(startsWith("ORANGE_"), IS_COLOR),
    IS_YELLOW(startsWith("YELLOW_"), IS_COLOR),
    IS_LIME(startsWith("LIME_"), IS_COLOR),
    IS_GREEN(startsWith("GREEN_"), IS_COLOR),
    IS_CYAN(startsWith("CYAN_"), IS_COLOR),
    IS_LIGHT_BLUE(startsWith("LIGHT_BLUE_"), IS_COLOR),
    IS_BLUE(startsWith("BLUE_"), IS_COLOR),
    IS_PURPLE(startsWith("PURPLE_"), IS_COLOR),
    IS_MAGENTA(startsWith("MAGENTA_"), IS_COLOR),
    IS_PINK(startsWith("PINK_"), IS_COLOR),

    IS_ARMOR,
    IS_LEATHER(startsWith("LEATHER_"), IS_ARMOR),

    IS_LEATHER_ARMOR(all(IS_ARMOR, IS_LEATHER)),
    ;

    var parents: List<MaterialGroup>
    var materialSelector: (Material) -> Boolean

    constructor(materialSelector: (Material) -> Boolean, vararg parents: MaterialGroup) {
        this.materialSelector = materialSelector
        this.parents = if (parents.isEmpty()) listOf() else parents.toList()
    }

    constructor(vararg parents: MaterialGroup) {
        this.parents = if (parents.isEmpty()) listOf() else parents.toList()


        this.materialSelector = { material ->
            var condition: ((Material) -> Boolean)? = null

            entries.filter { it.parents.contains(this) }.forEach {
                condition = if (condition == null) it.materialSelector else condition!! or it.materialSelector
            }
            condition!!(material)
        }
    }
}

fun startsWith(name: String): (Material) -> Boolean = { it.name.startsWith(name, ignoreCase = true) }
fun endsWith(name: String): (Material) -> Boolean = { it.name.endsWith(name, ignoreCase = true) }
fun contains(name: String): (Material) -> Boolean = { it.name.contains(name, ignoreCase = true) }
fun all(vararg groups: MaterialGroup): (Material) -> Boolean {
    var condition: ((Material) -> Boolean)? = null

    groups.toList().forEach {
        condition = if (condition == null) it.materialSelector else condition!! and it.materialSelector
    }

    return condition!!
}
fun anyOf(vararg groups: MaterialGroup): (Material) -> Boolean {
    var condition: ((Material) -> Boolean)? = null

    groups.toList().forEach {
        condition = if (condition == null) it.materialSelector else condition!! or it.materialSelector
    }

    return condition!!
}