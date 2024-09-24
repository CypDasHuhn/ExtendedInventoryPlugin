package de.cypdashuhn.rooster.material

import de.cypdashuhn.rooster.util.and
import de.cypdashuhn.rooster.util.or
import org.bukkit.Material

enum class SelectorType(val selector: (Pair<((Material) -> Boolean), MaterialGroup>) -> (Material) -> Boolean) {
    INCLUDE({ (condition, materialGroup) ->
        condition or materialGroup.materialSelector
    }),
    EXCLUDE({ (condition, materialGroup) ->
        condition and { !materialGroup.materialSelector(it) }
    }),
    REQUIRE({ (condition, materialGroup) ->
        condition and materialGroup.materialSelector
    })
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

        fun get() {

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

    fun exclude(group: MaterialGroup): MaterialSelector {
        val currentMaterials = materials
        materials.removeAll(Material.entries.toTypedArray().filter(group.materialSelector))
        return MaterialSelector(currentMaterials)
    }

    fun require(group: MaterialGroup): MaterialSelector {
        val currentMaterials = materials
        materials.retainAll(Material.entries.toTypedArray().filter(group.materialSelector))
        return MaterialSelector(currentMaterials)
    }

    fun get(): List<Material> = materials
}