package de.cypdashuhn.rooster.region

import de.cypdashuhn.rooster.util.value
import org.bukkit.Axis
import org.bukkit.World
import org.bukkit.event.Event
import kotlin.math.absoluteValue
import kotlin.reflect.KClass

/** Generally unfinished in every aspect. just ignore! */
object RegionManager {
    enum class EventTarget {
        ENTER_REGION,
        MOVE_IN_REGION,
        LEAVE_REGION
    }

    class EventTargetDTO(
        val abstracted: List<EventTarget> = listOf(),
        val event: List<KClass<Event>> = listOf()
    )

    class BinaryGroup(
        val group1: BinaryGroupWrapper,
        val group2: BinaryGroupWrapper,
        val axis: Axis,
        val value: Int
    )

    class BinaryGroupWrapper(
        val group: BinaryGroup?,
        val regionIndex: Int?
    )

    var registeredRegions = listOf<Region>()
    var mappedRegions =
        mutableMapOf<
                EventTargetDTO,
                MutableMap<
                        World,
                        BinaryGroup
                        >,
                >() // Regions Index

    fun registerRegion(region: Region, vararg eventTarget: KClass<Event>) {
        registerRegion(region, EventTargetDTO(event = listOf(*eventTarget)))
    }

    fun registerRegion(region: Region, vararg eventTarget: EventTarget) {
        registerRegion(region, EventTargetDTO(abstracted = listOf(*eventTarget)))
    }

    fun registerRegion(region: Region, eventTarget: EventTargetDTO) {
        registeredRegions += region
    }

    fun reloadMappings(eventTarget: EventTargetDTO) {
        val regionsMappedToWorlds = mappedRegions[eventTarget]

        if (regionsMappedToWorlds == null) {
            mappedRegions[eventTarget] = mutableMapOf()
        }
        requireNotNull(regionsMappedToWorlds)

        regionsMappedToWorlds.clear()


    }

    fun regionsToBinaryGroup(regions: List<Region>): BinaryGroup? {
        // : Map<Axis, Map<Region.AxisComparison, List<Region>>>
        val map: Map<Pair<Axis, Int>, Map<Region.AxisComparison, List<Region>>> = Axis.entries.associate { axis ->
            val mappedValues = regions.map { listOf(it.edge1.value(axis), it.edge2.value(axis)) }.flatten()

            var offset = 1.0
            var axisComparisonToRegion: Map<Region.AxisComparison, List<Region>>
            var splitValue: Int

            var lastValue: Int? = null
            var repeatCount = 0
            val repeatsWithoutSuccess = 5

            while (true) {
                val minValue = mappedValues.min()
                val maxValue = mappedValues.max()
                val difference = maxValue - minValue
                val value = ((difference / 2) * offset + minValue).toInt()

                axisComparisonToRegion =
                    regions.map { it to it.compareToAxis(axis, value.toDouble()) }.groupBy({ it.second }, { it.first })

                val distribution = axisComparisonToRegion.mapValues { it.value.size }

                val before = distribution[Region.AxisComparison.BEFORE] ?: 0
                val behind = distribution[Region.AxisComparison.BEHIND] ?: 0

                if (before == behind || (before - behind).absoluteValue == 1) {
                    splitValue = value
                    return@associate (axis to splitValue) to axisComparisonToRegion
                } else {
                    val ratio = before.toDouble() / behind.toDouble()
                    if (offset == ratio) {
                        offset *= if (offset <= 1) 0.75 else 1.3
                    } else {
                        offset = ratio
                    }

                    // Check if this is an unsuccessful repetition
                    if (lastValue == value || (lastValue != null && (lastValue - value).absoluteValue == 1)) {
                        repeatCount++
                    } else {
                        repeatCount = 0 // Reset count if the value changes significantly
                    }

                    if (repeatCount >= repeatsWithoutSuccess) {
                        splitValue = value
                        return@associate (axis to splitValue) to axisComparisonToRegion
                    }

                    lastValue = value // Update lastValue for comparison in the next loop
                }
            }
            @Suppress("unreachable_code")
            return@associate (axis to 0) to emptyMap<Region.AxisComparison, List<Region>>() // Fallback
        }

        val mostEfficientSplit = map.entries.minByOrNull { (_, axisComparisonMap) ->
            axisComparisonMap[Region.AxisComparison.INTERSECTING]?.size ?: 0
        }!!

        val (axis, splitValue) = mostEfficientSplit.key
        val axisComparisonMap = mostEfficientSplit.value


        return null
    }

    fun splitIntersectingRegions(
        axis: Axis,
        splitValue: Int,
        axisComparisonMap: Map<Region.AxisComparison, List<Region>>
    ): Map<Region.AxisComparison, List<Region>> {

        // Get the intersecting regions
        val intersectingRegions = axisComparisonMap[Region.AxisComparison.INTERSECTING] ?: emptyList()

        // Prepare a mutable map to hold the updated regions
        val updatedMap = axisComparisonMap
            .filterKeys { it != Region.AxisComparison.INTERSECTING }
            .toMutableMap()

        val behindRegions = updatedMap.getOrDefault(Region.AxisComparison.BEHIND, mutableListOf()).toMutableList()
        val beforeRegions = updatedMap.getOrDefault(Region.AxisComparison.BEFORE, mutableListOf()).toMutableList()

        intersectingRegions.forEach { region ->
            // Split the region along the axis at splitValue
            val (newRegionBehind, newRegionBefore) = region.splitAlongAxis(axis, splitValue)

            // Add the newly split regions to the appropriate groups
            behindRegions.add(newRegionBehind)
            beforeRegions.add(newRegionBefore)
        }

        // Update the map with the new region groups
        updatedMap[Region.AxisComparison.BEHIND] = behindRegions
        updatedMap[Region.AxisComparison.BEFORE] = beforeRegions

        return updatedMap
    }

    // Add a helper function to split the region along the axis
    fun Region.splitAlongAxis(axis: Axis, splitValue: Int): Pair<Region, Region> {
        val newEdge1Behind = if (edge1.value(axis) > splitValue) edge1 else edge1.apply { setValue(axis, splitValue) }
        val newEdge2Before = if (edge2.value(axis) < splitValue) edge2 else edge2.apply { setValue(axis, splitValue) }

        val regionBehind = Region(newEdge1Behind, edge2)
        val regionBefore = Region(edge1, newEdge2Before)

        return regionBehind to regionBefore
    }

    // Helper function to set the axis value for Location
    fun Location.setValue(axis: Axis, value: Int) {
        when (axis) {
            Axis.X -> this.x = value.toDouble()
            Axis.Y -> this.y = value.toDouble()
            Axis.Z -> this.z = value.toDouble()
        }
    }

}