package de.cypdashuhn.rooster.region

import de.cypdashuhn.rooster.util.value
import org.bukkit.Axis
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.event.Event
import kotlin.math.absoluteValue
import kotlin.reflect.KClass


/** Generally unfinished in every aspect. just ignore! */
object RegionManager {


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
        registerRegion(region, EventTargetDTO(events = listOf(*eventTarget)))
    }

    fun registerRegion(region: Region, vararg eventTarget: MoveEvent) {
        registerRegion(region, EventTargetDTO(moveEvents = listOf(*eventTarget)))
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

    internal data class ReadResult(
        val beforeRegions: List<RegionReferenceWrapper>,
        val afterRegions: List<RegionReferenceWrapper>,
        val splitter: Splitter
    )

    internal fun regionsToReadResult(regions: List<Region>): ReadResult {
        // : Map<Axis, Map<Region.AxisComparison, List<Region>>>
        val map: Map<Pair<Axis, Double>, Map<Region.AxisComparison, MutableList<RegionReferenceWrapper>>> =
            Axis.entries.associate { axis ->
                val mappedValues = regions.map { listOf(it.edge1.value(axis), it.edge2.value(axis)) }.flatten()

                var offset = 1.0
                var axisComparisonToRegion: Map<Region.AxisComparison, MutableList<RegionReferenceWrapper>>
                var splitValue: Double

                var lastValue: Double? = null
                var repeatCount = 0
                val repeatsWithoutSuccess = 5

                while (true) {
                    val minValue = mappedValues.min()
                    val maxValue = mappedValues.max()
                    val difference = maxValue - minValue
                    val value = ((difference / 2) * offset + minValue)

                    axisComparisonToRegion = regions
                        .map { it to it.compareToAxis(axis, value.toDouble()) }
                        .groupBy({ it.second }, { it.first })
                        .mapValues { it.value.toMutableList() }

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
                        if (lastValue == value || (lastValue != null && (lastValue - value).absoluteValue <= 1)) {
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
                return@associate (axis to 0.0) to emptyMap<Region.AxisComparison, MutableList<RegionReferenceWrapper>>() // Fallback
            }

        val mostEfficientSplit = map.entries.minByOrNull { (_, axisComparisonMap) ->
            axisComparisonMap[Region.AxisComparison.INTERSECTING]?.size ?: 0
        }!!

        val (axis, splitValue) = mostEfficientSplit.key
        val axisComparisonMap = mostEfficientSplit.value

        axisComparisonMap[Region.AxisComparison.BEHIND]!!.addAll(axisComparisonMap[Region.AxisComparison.INTERSECTING]!!)
        axisComparisonMap[Region.AxisComparison.BEHIND]!!.addAll(axisComparisonMap[Region.AxisComparison.INTERSECTING]!!)

        return ReadResult(
            null,
            null,
            Splitter(axis, splitValue)
        )
    }

    internal fun regionsToBinaryGroup(regions: List<RegionReference>): BinaryGroup {

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