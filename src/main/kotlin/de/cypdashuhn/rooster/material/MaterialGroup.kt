package de.cypdashuhn.rooster.material

import de.cypdashuhn.rooster.util.and
import de.cypdashuhn.rooster.util.andNot
import de.cypdashuhn.rooster.util.or
import org.bukkit.Material

// @formatter:off
enum class MaterialGroup {
    ALL({ true }),
    NON_AIR({ it != Material.AIR }),
    IS_BLOCK({ it.isBlock }),
    IS_ITEM({ it.isItem }),

    IS_COLOR,

    IS_PLANT,

    IS_FLOWER(IS_PLANT),
    IS_SMALL_FLOWER(IS_FLOWER),
    IS_TALL_FLOWER(IS_FLOWER),

    IS_TULIP(endsWith("TULIP"), IS_FLOWER),
    IS_ALLIUM(contains("ALLIUM"), IS_SMALL_FLOWER),
    IS_AZURE_BLUET(contains("AZURE_BLUET"), IS_SMALL_FLOWER),
    IS_BLUE_ORCHID(contains("BLUE_ORCHID"), IS_SMALL_FLOWER),
    IS_CORNFLOWER(contains("CORNFLOWER"), IS_SMALL_FLOWER),
    IS_DANDELION(contains("DANDELION"), IS_SMALL_FLOWER),
    IS_LILY_OF_THE_VALLEY(contains("LILY_OF_THE_VALLEY"), IS_SMALL_FLOWER),
    IS_OXEYE_DAISY(contains("OXEYE_DAISY"), IS_SMALL_FLOWER),
    IS_PINK_PETALS(contains("PINK_PETALS"), IS_SMALL_FLOWER),
    IS_POPPY(contains("POPPY"), IS_SMALL_FLOWER),
    IS_TORCHFLOWER(contains("TORCHFLOWER"), IS_SMALL_FLOWER),
    IS_WITHER_ROSE(contains("WITHER_ROSE"), IS_SMALL_FLOWER),

    IS_LILAC(contains("LILAC"), IS_TALL_FLOWER),
    IS_PEONY(contains("PEONY"), IS_TALL_FLOWER),
    IS_PITCHER_PLANT(contains("PITCHER_PLANT"), IS_TALL_FLOWER),
    IS_ROSE_BUSH(contains("ROSE_BUSH"), IS_TALL_FLOWER),
    IS_SUNFLOWER(contains("SUNFLOWER"), IS_TALL_FLOWER),

    IS_MUSHROOM(contains("MUSHROOM"), IS_PLANT),

    IS_SANDY_BLOCK(contains("SAND")),

    IS_BRICK(contains("BRICK")),

    IS_NETHER(contains("NETHER")),

    IS_NETHER_BRICKS(all(IS_BRICK, IS_NETHER)),

    IS_COLORABLE,
    IS_LIGHT_SOURCE,

    IS_CANDLE(endsWith("_CANDLE"), IS_COLORABLE, IS_LIGHT_SOURCE),
    IS_WOOL(contains("_WOOL"), IS_COLORABLE),
    IS_CARPET(endsWith("_CARPET")),
    IS_WOOL_BLOCK(all(IS_WOOL, IS_CARPET)),
    IS_BED(endsWith("_BED"), IS_COLORABLE),

    INTERNAL_EXCLUDE_FOR_COLOR(anyOf(IS_BRICK) or (anyOf(IS_PLANT) andNot (anyOf(IS_FLOWER)))),

    IS_WHITE((startsWith("WHITE_") or anyOf(IS_LILY_OF_THE_VALLEY)) andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), IS_COLOR),
    IS_LIGHT_GRAY((startsWith("LIGHT_GRAY_") or anyOf(IS_AZURE_BLUET, IS_OXEYE_DAISY)) andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), IS_COLOR),
    IS_GRAY(startsWith("GRAY_") andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), IS_COLOR),
    IS_BLACK((startsWith("BLACK_") or anyOf(IS_WITHER_ROSE)) andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), IS_COLOR),
    IS_BROWN(startsWith("BROWN_") andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), IS_COLOR),
    IS_RED((startsWith("RED_") or anyOf(IS_POPPY, IS_ROSE_BUSH)) andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR, IS_SANDY_BLOCK), IS_COLOR),
    IS_ORANGE((startsWith("ORANGE_") or anyOf(IS_TORCHFLOWER)) andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), IS_COLOR),
    IS_YELLOW((startsWith("YELLOW_") or anyOf(IS_DANDELION, IS_SUNFLOWER)) andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), IS_COLOR),
    IS_LIME(startsWith("LIME_") andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), IS_COLOR),
    IS_GREEN(startsWith("GREEN_") andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), IS_COLOR),
    IS_CYAN((startsWith("CYAN_") or anyOf(IS_PITCHER_PLANT)) andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), IS_COLOR),
    IS_LIGHT_BLUE((startsWith("LIGHT_BLUE_") or anyOf(IS_BLUE_ORCHID)) andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), IS_COLOR),
    IS_BLUE((startsWith("BLUE_") or anyOf(IS_CORNFLOWER)) andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), IS_COLOR),
    IS_PURPLE(startsWith("PURPLE_") andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), IS_COLOR),
    IS_MAGENTA((startsWith("MAGENTA_") or anyOf(IS_ALLIUM, IS_LILAC)) andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), IS_COLOR),
    IS_PINK((startsWith("PINK_") or anyOf(IS_PEONY)) andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), IS_COLOR),

    IS_POTTED(startsWith("POTTED")),

    IS_ARMOR,
    IS_LEATHER(startsWith("LEATHER_"), IS_ARMOR),

    IS_DIAMOND(contains("DIAMOND")),
    IS_IRON(contains("IRON")),
    IS_GOLD(contains("GOLD") or contains("GOLDEN")),
    IS_CHAINMAIL(contains("CHAINMAIL")),
    IS_WOODEN(startsWith("WOODEN_")),

    IS_HELMET(endsWith("_HELMET") or endsWith("_CAP"), IS_ARMOR),
    IS_CHESTPLATE(endsWith("_CHESTPLATE"), IS_ARMOR),
    IS_LEGGINGS(endsWith("_LEGGINGS"), IS_ARMOR),
    IS_BOOTS(endsWith("_BOOTS"), IS_ARMOR),

    IS_TOOL,
    IS_AXE(endsWith("_AXE"), IS_TOOL),
    IS_SHOVEL(endsWith("_SHOVEL"), IS_TOOL),
    IS_PICKAXE(endsWith("_PICKAXE"), IS_TOOL),
    IS_SWORD(endsWith("_SWORD"), IS_TOOL),
    IS_HOE(endsWith("_HOE")),

    IS_LEATHER_ARMOR(all(IS_ARMOR, IS_LEATHER)),
    ;// @formatter:on

    private var parents: List<MaterialGroup>
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