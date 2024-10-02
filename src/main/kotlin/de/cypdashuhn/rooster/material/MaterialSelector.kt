package de.cypdashuhn.rooster.material

import de.cypdashuhn.rooster.util.PredicateCombinator
import de.cypdashuhn.rooster.util.and
import de.cypdashuhn.rooster.util.andNot
import de.cypdashuhn.rooster.util.or
import org.bukkit.Material

enum class SelectorType(val combinator: PredicateCombinator<Material>) {
    INCLUDE(::or),
    EXCLUDE(::andNot),
    REQUIRE(::and)
}

class MaterialSelector {
    companion object {
        fun getMaterialOrNull(string: Material): Material? {
            return try {
                Material.valueOf(string.name)
            } catch (_: IllegalArgumentException) {
                null
            }
        }

        fun get(vararg filter: Pair<SelectorType, MaterialGroup>) {
            MaterialSelector().get(*filter)
        }
    }

    private var materialFilter: List<Pair<SelectorType, MaterialGroup>>
    private var excludeLegacy: Boolean

    constructor(excludeLegacy: Boolean = true) {
        this.materialFilter = listOf()
        this.excludeLegacy = excludeLegacy
    }

    private constructor(materialFilter: List<Pair<SelectorType, MaterialGroup>>, excludeLegacy: Boolean) {
        this.materialFilter = materialFilter
        this.excludeLegacy = excludeLegacy
    }


    constructor(group: MaterialGroup, excludeLegacy: Boolean = true) {
        this.materialFilter = listOf(SelectorType.INCLUDE to group)
        this.excludeLegacy = excludeLegacy
    }

    fun include(vararg groups: MaterialGroup): MaterialSelector = addFilter(SelectorType.INCLUDE, *groups)
    fun exclude(vararg groups: MaterialGroup): MaterialSelector = addFilter(SelectorType.EXCLUDE, *groups)
    fun require(vararg groups: MaterialGroup): MaterialSelector = addFilter(SelectorType.REQUIRE, *groups)

    fun addFilter(selectorType: SelectorType, group: MaterialGroup): MaterialSelector {
        val materialFilterCopy = this.materialFilter.map { it.copy() }.toMutableList()
        materialFilterCopy.add(selectorType to group)
        return MaterialSelector(materialFilterCopy, this.excludeLegacy)
    }

    fun addFilter(vararg filters: Pair<SelectorType, MaterialGroup>): MaterialSelector {
        val materialFilterCopy = this.materialFilter.map { it.copy() }.toMutableList()
        materialFilterCopy.addAll(filters)
        return MaterialSelector(materialFilterCopy, this.excludeLegacy)
    }

    fun addFilter(selectorType: SelectorType, vararg groups: MaterialGroup): MaterialSelector {
        val filters = groups.toList().map { selectorType to it }
        return addFilter(*filters.toTypedArray())
    }

    fun get(vararg filter: Pair<SelectorType, MaterialGroup>): List<Material> {
        if (filter.isEmpty()) {
            return Material.entries.toList()
        }

        val filterList = filter.toList()

        var condition: ((Material) -> Boolean)? = null

        filterList.forEach { (selector, materialGroup) ->
            if (condition == null) {
                condition = { false }
            }
            condition = selector.combinator(condition!!, materialGroup.materialSelector)
        }
        if (excludeLegacy) {
            condition = SelectorType.EXCLUDE.combinator(condition!!, MaterialGroup.LEGACY.materialSelector)
        }
        return Material.entries.filter(condition!!)
    }
}