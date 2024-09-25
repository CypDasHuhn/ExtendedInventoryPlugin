package de.cypdashuhn.rooster.material

import de.cypdashuhn.rooster.util.PredicateCombinator
import de.cypdashuhn.rooster.util.andNotR
import de.cypdashuhn.rooster.util.andR
import de.cypdashuhn.rooster.util.orR
import org.bukkit.Material

enum class SelectorType(val combinator: PredicateCombinator<Material>) {
    INCLUDE(::orR),
    EXCLUDE(::andNotR),
    REQUIRE(::andR)
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
            return Material.entries.filter(condition!!)
        }
    }

    private var materials = mutableListOf<Material>()

    constructor()

    private constructor(materials: List<Material>) {
        this.materials = materials.toMutableList()
    }


    constructor(group: MaterialGroup) {
        materials.addAll(Material.entries.toTypedArray().filter(group.materialSelector))
    }

    fun include(group: MaterialGroup): MaterialSelector {
        val currentMaterials = materials
        materials.addAll(Material.entries.toTypedArray().filter(group.materialSelector))
        return MaterialSelector(currentMaterials)
    }

    fun exclude(vararg groups: MaterialGroup): MaterialSelector {
        val currentMaterials = materials.map { it }.toMutableList()

        groups.forEach {
            currentMaterials.removeAll(Material.entries.toTypedArray().filter(it.materialSelector))
        }

        return MaterialSelector(currentMaterials)
    }

    fun require(group: MaterialGroup): MaterialSelector {
        val currentMaterials = materials
        materials.retainAll(Material.entries.toTypedArray().filter(group.materialSelector))
        return MaterialSelector(currentMaterials)
    }

    fun get(): List<Material> = materials
}