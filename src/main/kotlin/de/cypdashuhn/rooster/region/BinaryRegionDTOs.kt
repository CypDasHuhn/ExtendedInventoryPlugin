package de.cypdashuhn.rooster.region

import de.cypdashuhn.rooster.util.value
import org.bukkit.Axis
import org.bukkit.Location
import org.bukkit.event.Event
import org.joml.Vector3d
import kotlin.reflect.KClass

enum class MoveEvent {
    ENTER_REGION,
    MOVE_IN_REGION,
    LEAVE_REGION
}

class EventTargetDTO(
    val moveEvents: List<MoveEvent> = listOf(),
    val events: List<KClass<out Event>> = listOf()
) {
    fun correctTarget(moveEvent: MoveEvent): Boolean {
        return moveEvents.contains(moveEvent)
    }

    fun correctTarget(event: Event): Boolean {
        return events.contains(event::class)
    }
}


typealias RegionId = Int
typealias Box = Pair<Vector3d, Vector3d>

class RegionReference(region: Region, customBox: Box? = null) {
    val box: Box?
    val regionId: RegionId

    init {
        val regionId = RegisteredRegionManager.regionIdByReference(region)
        requireNotNull(regionId) { "Region must be registered" }
        this.box = customBox ?: region.box
        this.regionId = regionId
    }
}

internal class BinaryGroup(
    val beforeGroup: BinaryGroupWrapper,
    val afterGroup: BinaryGroupWrapper,
    val splitter: Splitter
) {
    fun get(location: Location): List<RegionReference> {
        val value = location.value(splitter.axis)
        val regions = mutableListOf<RegionReference>()
        if (value < splitter.value) regions.addAll(beforeGroup.get(location))
        else regions.addAll(afterGroup.get(location))
        return regions
    }
}

internal class IntersectingRegion(
    val beforeRegion: RegionReference,
    val afterRegion: RegionReference,
    val splitterAxis: Axis,
    val beforeValue: Double,
    val afterValue: Double
) {
    fun get(location: Location): List<RegionReference> {
        val value = location.value(splitterAxis)

        val regions = mutableListOf<RegionReference>()
        if (value < afterValue) regions += beforeRegion
        if (value > beforeValue) regions += afterRegion
        return regions
    }
}


internal data class Splitter(val axis: Axis, val value: Double)

internal class RegionReferenceWrapper {
    var regionReference: RegionReference? = null
    var intersectingRegion: IntersectingRegion? = null

    constructor(region: RegionReference) {
        this.regionReference = region
    }

    constructor(intersectingRegion: IntersectingRegion) {
        this.intersectingRegion = intersectingRegion
    }

    fun get(location: Location): List<RegionReference> {
        if (regionReference != null) {
            return listOf(regionReference!!)
        } else if (intersectingRegion != null) {
            return intersectingRegion!!.get(location)
        }
        throw IllegalStateException("One of regionReference or intersectingRegion must be set")
    }
}

internal class BinaryGroupWrapper {
    var group: BinaryGroup? = null
    var regionAndChildren: Pair<RegionReferenceWrapper, BinaryGroupWrapper?>? = null

    constructor(group: BinaryGroup) {
        this.group = group
    }

    constructor(region: RegionReferenceWrapper, child: BinaryGroupWrapper? = null) {
        this.regionAndChildren = region to child
    }

    fun get(location: Location): List<RegionReference> {
        if (group != null) {
            return group!!.get(location)
        } else if (regionAndChildren != null) {
            val regions = mutableListOf<RegionReference>()
            val regionWrapper = regionAndChildren!!.first
            val child = regionAndChildren!!.second
            regions.addAll(regionWrapper.get(location))
            if (child != null) regions.addAll(child.get(location))

            return regions
        }
        throw IllegalStateException("One of group or regionAndChildren must be set")
    }
}

class RegisteredRegion internal constructor(
    val region: Region,
    val regionId: RegionId,
    val regionKey: String?,
    internal val regionLambda: RegionLambdaWrapper<*>
)