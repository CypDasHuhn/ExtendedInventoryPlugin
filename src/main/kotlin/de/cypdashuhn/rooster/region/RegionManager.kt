package de.cypdashuhn.rooster.region

import de.cypdashuhn.rooster.util.value
import org.bukkit.Axis
import org.bukkit.World
import org.bukkit.event.Event
import kotlin.math.absoluteValue
import kotlin.reflect.KClass

/** Generally unfinished in every aspect. just ignore! */
@Suppress("unused", "unused_parameter", "unused_variable")
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

    fun RegionsToBinaryGroup(regions: List<Region>): BinaryGroup? {
        // : Map<Axis, Map<Region.AxisComparison, List<Region>>>
        val map = Axis.values().map { axis ->
            val mappedValues = regions.map { listOf(it.edge1.value(axis), it.edge2.value(axis)) }.flatten()

            var offset = 1.0

            var axisComparisonToRegion: Map<Region.AxisComparison, List<Region>>

            while (true) {
                val difference = mappedValues.max() - mappedValues.min()
                val value = (difference / 2) * offset + mappedValues.min()

                axisComparisonToRegion =
                    regions.map { it to it.compareToAxis(axis, value) }.groupBy({ it.second }, { it.first })

                val distribution = axisComparisonToRegion.mapValues { it.value.size }

                val before = distribution[Region.AxisComparison.BEFORE] ?: 0
                val behind = distribution[Region.AxisComparison.BEHIND] ?: 0

                if (before == behind || (before - behind).absoluteValue == 1) {
                    return@map axis to axisComparisonToRegion
                } else {
                    val ratio = before.toDouble() / behind.toDouble()
                    if (offset == ratio) {
                        offset *=
                            if (offset <= 1) 0.75
                            else 1.3
                    } else {
                        offset = ratio
                    }
                }
            }
            @Suppress("unreachable_code")
            return@map axis to mapOf<Region.AxisComparison, List<Region>>()
        }.groupBy({ it.first }, { it.second })

        return null
    }
}