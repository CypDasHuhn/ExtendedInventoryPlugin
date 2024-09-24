package de.cypdashuhn.rooster.unfinished

import de.cypdashuhn.rooster.region.Region
import org.bukkit.Axis
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Entity
import org.joml.Vector3d

/** Generally unfinished in every aspect. just ignore! */
@Suppress("unused", "unused_parameter")
class Particle(
    val particleType: Particle = Particle.CLOUD,
    val amount: Int = 10,
    val offset: Vector3d = Vector3d(1.0, 1.0, 1.0),
    val longRange: Boolean = false,

    ) {
    fun spawnAt(vararg locations: Location) {
        TODO("Implement Location Spawns")
    }

    fun spawnFor(vararg entities: Entity) {
        TODO("Implement Location Spawns")
    }
}

enum class Face(
    val axis: Axis,
    val positive: Boolean
) {
    TOP(Axis.Y, true),
    BOTTOM(Axis.Y, false),
    WEST(Axis.X, false),
    EAST(Axis.X, true),
    NORTH(Axis.Z, false),
    SOUTH(Axis.Z, true),
}

/** Generally unfinished in every aspect. just ignore! */
@Suppress("unused", "unused_parameter")
class ParticleBox(
) {
    var steps = 1.0

    constructor(
        region: Region,
        particle: Particle,
        density: Double = 0.5,
        faces: List<Face> // = default by axis

    ) : this()

    constructor(
        particle: Particle,
        steps: Double = 0.5,
        faces: List<Face> // = default by axis
    ) : this()

    fun spawn() {
        val region: Region = null!!
        val locations: MutableList<Location> = mutableListOf()

        iterateOverDouble(region.minX.toDouble(), region.maxX.toDouble(), steps) { x ->
            locations.add(Location(region.world, x, region.maxY.toDouble(), region.maxZ.toDouble()))
            locations.add(Location(region.world, x, region.minY.toDouble(), region.maxZ.toDouble()))
            locations.add(Location(region.world, x, region.maxY.toDouble(), region.minZ.toDouble()))
            locations.add(Location(region.world, x, region.minY.toDouble(), region.minZ.toDouble()))
        }

        iterateOverDouble(region.minY.toDouble(), region.maxY.toDouble(), steps) { y ->
            locations.add(Location(region.world, region.maxX.toDouble(), y, region.maxZ.toDouble()))
            locations.add(Location(region.world, region.minX.toDouble(), y, region.maxZ.toDouble()))
            locations.add(Location(region.world, region.maxX.toDouble(), y, region.minZ.toDouble()))
            locations.add(Location(region.world, region.minX.toDouble(), y, region.minZ.toDouble()))
        }

        iterateOverDouble(region.minZ.toDouble(), region.maxZ.toDouble(), steps) { z ->
            locations.add(Location(region.world, region.maxX.toDouble(), region.maxY.toDouble(), z))
            locations.add(Location(region.world, region.minX.toDouble(), region.minY.toDouble(), z))
            locations.add(Location(region.world, region.maxX.toDouble(), region.maxY.toDouble(), z))
            locations.add(Location(region.world, region.minX.toDouble(), region.minY.toDouble(), z))
        }
    }

    fun spawnWithOffset() {
        TODO("Implement Location Spawns")
    }
}

fun iterateOverDouble(
    start: Double,
    end: Double,
    step: Double,
    action: (Double) -> Unit
) {
    var current = start
    while (current <= end) {
        action(current)
        current += step
    }
}