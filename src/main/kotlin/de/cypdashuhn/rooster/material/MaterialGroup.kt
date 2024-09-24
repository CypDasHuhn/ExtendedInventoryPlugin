package de.cypdashuhn.rooster.material

import de.cypdashuhn.rooster.util.and
import de.cypdashuhn.rooster.util.or
import org.bukkit.Material

enum class MaterialGroup {
    ALL({ true }),
    NON_AIR({ it != Material.AIR }),
    IS_BLOCK({ it.isBlock }),
    IS_ITEM({ it.isItem }),

    IS_COLOR,

    IS_WHITE(startsWith("WHITE_"), IS_COLOR),
    IS_LIGHT_GRAY(startsWith("LIGHT_GRAY_")),
    IS_GRAY(startsWith("GRAY_")),
    IS_BLACK(startsWith("BLACK_")),
    IS_BROWN(startsWith("BROWN_")),
    IS_RED(startsWith("RED_")),
    IS_ORANGE(startsWith("ORANGE_")),
    IS_YELLOW(startsWith("YELLOW_")),
    IS_LIME(startsWith("LIME_")),
    IS_GREEN(startsWith("GREEN_")),
    IS_CYAN(startsWith("CYAN_")),
    IS_LIGHT_BLUE(startsWith("LIGHT_BLUE_")),
    IS_BLUE(startsWith("BLUE_")),
    IS_PURPLE(startsWith("PURPLE_")),
    IS_MAGENTA(startsWith("MAGENTA_")),
    IS_PINK(startsWith("PINK_")),

    IS_ARMOR,
    IS_LEATHER(startsWith("LEATHER_"), IS_ARMOR),

    IS_LEATHER_ARMOR(subGroup(IS_ARMOR, IS_LEATHER)),
    ;

    var parents: List<MaterialGroup>
    var materialSelector: (Material) -> Boolean

    constructor(materialSelector: (Material) -> Boolean, vararg parents: MaterialGroup) {
        this.materialSelector = materialSelector
        this.parents = parents.toList()
    }

    constructor(parent: List<MaterialGroup> = listOf()) {
        this.parents = parent

        var condition: ((Material) -> Boolean)? = null
        entries.filter { it.parents.contains(this) }.forEach {
            condition = if (condition == null) it.materialSelector else condition!! or it.materialSelector
        }
        this.materialSelector = condition!!
    }
}

fun startsWith(name: String): (Material) -> Boolean = { it.name.startsWith(name, ignoreCase = true) }
fun endsWith(name: String): (Material) -> Boolean = { it.name.endsWith(name, ignoreCase = true) }
fun contains(name: String): (Material) -> Boolean = { it.name.contains(name, ignoreCase = true) }
fun subGroup(vararg groups: MaterialGroup): (Material) -> Boolean {
    var condition: ((Material) -> Boolean)? = null

    groups.toList().forEach {
        condition = if (condition == null) it.materialSelector else condition!! and it.materialSelector
    }

    return condition!!
}

fun test() {
}