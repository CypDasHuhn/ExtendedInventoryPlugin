package de.cypdashuhn.rooster.material

import de.cypdashuhn.rooster.util.and
import de.cypdashuhn.rooster.util.andNot
import de.cypdashuhn.rooster.util.or
import org.bukkit.Material

// @formatter:off
enum class MaterialGroup {
    ALL({ true }),
    NON_AIR({ it != Material.AIR }),

    COLOR,

    PLANT,

    FLOWER(PLANT),
    SMALL_FLOWER(FLOWER),
    TALL_FLOWER(FLOWER),

    TULIP(endsWith("TULIP"), FLOWER),
    ALLIUM(contains("ALLIUM"), SMALL_FLOWER),
    AZURE_BLUET(contains("AZURE_BLUET"), SMALL_FLOWER),
    BLUE_ORCHID(contains("BLUE_ORCHID"), SMALL_FLOWER),
    CORNFLOWER(contains("CORNFLOWER"), SMALL_FLOWER),
    DANDELION(contains("DANDELION"), SMALL_FLOWER),
    LILY_OF_THE_VALLEY(contains("LILY_OF_THE_VALLEY"), SMALL_FLOWER),
    OXEYE_DAISY(contains("OXEYE_DAISY"), SMALL_FLOWER),
    PINK_PETALS(contains("PINK_PETALS"), SMALL_FLOWER),
    POPPY(contains("POPPY"), SMALL_FLOWER),
    TORCHFLOWER(contains("TORCHFLOWER"), SMALL_FLOWER),
    WITHER_ROSE(contains("WITHER_ROSE"), SMALL_FLOWER),

    LILAC(contains("LILAC"), TALL_FLOWER),
    PEONY(contains("PEONY"), TALL_FLOWER),
    PITCHER_PLANT(contains("PITCHER_PLANT"), TALL_FLOWER),
    ROSE_BUSH(contains("ROSE_BUSH"), TALL_FLOWER),
    SUNFLOWER(contains("SUNFLOWER"), TALL_FLOWER),

    MUSHROOM(contains("MUSHROOM"), PLANT),

    SANDY_BLOCK(contains("SAND")),

    BRICK(contains("BRICK")),

    NETHER(contains("NETHER")),

    NETHER_BRICKS(all(BRICK, NETHER)),

    COLORABLE,
    LIGHT_SOURCE,

    CANDLE(endsWith("_CANDLE"), COLORABLE, LIGHT_SOURCE),
    WOOL(contains("_WOOL"), COLORABLE),
    CARPET(endsWith("_CARPET")),
    WOOL_BLOCK(all(WOOL, CARPET)),
    BED(endsWith("_BED"), COLORABLE),

    INTERNAL_EXCLUDE_FOR_COLOR(anyOf(BRICK) or (anyOf(PLANT) andNot (anyOf(FLOWER)))),

    WHITE((startsWith("WHITE_") or anyOf(LILY_OF_THE_VALLEY)) andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), COLOR),
    LIGHT_GRAY((startsWith("LIGHT_GRAY_") or anyOf(AZURE_BLUET, OXEYE_DAISY)) andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), COLOR),
    GRAY(startsWith("GRAY_") andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), COLOR),
    BLACK((startsWith("BLACK_") or anyOf(WITHER_ROSE)) andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), COLOR),
    BROWN(startsWith("BROWN_") andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), COLOR),
    RED((startsWith("RED_") or anyOf(POPPY, ROSE_BUSH)) andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR, SANDY_BLOCK), COLOR),
    ORANGE((startsWith("ORANGE_") or anyOf(TORCHFLOWER)) andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), COLOR),
    YELLOW((startsWith("YELLOW_") or anyOf(DANDELION, SUNFLOWER)) andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), COLOR),
    LIME(startsWith("LIME_") andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), COLOR),
    GREEN(startsWith("GREEN_") andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), COLOR),
    CYAN((startsWith("CYAN_") or anyOf(PITCHER_PLANT)) andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), COLOR),
    LIGHT_BLUE((startsWith("LIGHT_BLUE_") or anyOf(BLUE_ORCHID)) andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), COLOR),
    BLUE((startsWith("BLUE_") or anyOf(CORNFLOWER)) andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), COLOR),
    PURPLE(startsWith("PURPLE_") andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), COLOR),
    MAGENTA((startsWith("MAGENTA_") or anyOf(ALLIUM, LILAC)) andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), COLOR),
    PINK((startsWith("PINK_") or anyOf(PEONY)) andNot anyOf(INTERNAL_EXCLUDE_FOR_COLOR), COLOR),

    POTTED(startsWith("POTTED")),

    DEEPSLATE(contains("DEEPSLATE")),
    ORE(endsWith("_ORE")),
    MATERIAL_BLOCK(contains("BLOCK")),

    LEGACY(startsWith("LEGACY_")),

    ARMOR,

    MATERIAL,
    MINERAL(MATERIAL),
    METAL(MINERAL),
    GEM(MINERAL),

    LEATHER(startsWith("LEATHER_"), ARMOR, MATERIAL),
    DIAMOND(contains("DIAMOND"), GEM),
    IRON(contains("IRON"), METAL),
    GOLD(contains("GOLD") or contains("GOLDEN"), METAL),
    CHAINMAIL(contains("CHAINMAIL"), METAL),
    WOODEN(startsWith("WOODEN_"), MATERIAL),

    HELMET(endsWith("_HELMET") or endsWith("_CAP"), ARMOR),
    CHESTPLATE(endsWith("_CHESTPLATE"), ARMOR),
    LEGGINGS(endsWith("_LEGGINGS"), ARMOR),
    BOOTS(endsWith("_BOOTS"), ARMOR),

    TOOL,
    AXE(endsWith("_AXE"), TOOL),
    SHOVEL(endsWith("_SHOVEL"), TOOL),
    PICKAXE(endsWith("_PICKAXE"), TOOL),
    SWORD(endsWith("_SWORD"), TOOL),
    HOE(endsWith("_HOE")),

    LEATHER_ARMOR(all(ARMOR, LEATHER)),
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