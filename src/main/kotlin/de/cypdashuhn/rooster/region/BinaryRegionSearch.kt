package de.cypdashuhn.rooster.region

import de.cypdashuhn.rooster.region.BinaryRegionSearch.value
import org.bukkit.Axis
import org.joml.Vector3d
import kotlin.math.absoluteValue

object BinaryRegionSearch {


    fun Vector3d.value(axis: Axis): Double {
        return when (axis) {
            Axis.X -> this.x
            Axis.Y -> this.y
            Axis.Z -> this.z
        }
    }
}

//@formatter:off
fun main() {
    val items = listOf(
        1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10,
        1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10,
        1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10,
        1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10,
        1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 1 to 10, 5 to 28, 5 to 14, 0 to 6, 5 to 30, 6 to 15, 10 to 20, 20 to 30, 2 to 25, 15 to 35, 30 to 55,
        30 to 60, 10 to 70, 18 to 48, 30 to 65, 40 to 50, 50 to 75, 55 to 60, 70 to 100, 45 to 65, 60 to 90, 75 to 95, 85 to 100,
        22 to 45, 40 to 50, 50 to 75, 55 to 60, 70 to 100, 45 to 65, 60 to 90, 75 to 95, 85 to 100, 35 to 60, 50 to 85, 40 to 50, 50 to 75,
        55 to 60, 70 to 100, 45 to 65, 60 to 90, 75 to 95, 85 to 100, 35 to 60, 50 to 85, 75 to 95, 85 to 100, 35 to 60,
        50 to 85, 40 to 50, 50 to 75, 55 to 60, 70 to 100, 45 to 65, 60 to 90, 75 to 95, 85 to 100, 35 to 60, 50 to 85, 55 to 60, 70 to 100, 45 to 65,
        60 to 90, 75 to 95, 85 to 100, 35 to 60, 50 to 85,
    )
    val regionItems = items.mapIndexed { index, pair ->
        RegionItem(
            index,
            pair.first.toDouble(),
            pair.second.toDouble()
        )
    }
    val s = findSeparator(regionItems)
    println(s)
}
//@formatter:on

data class CandidateScore(
    val value: Double,
    val diff: Int,
    val inBetween: Int,
    val compareResult: CompareResult,
    val type: CandidateType
)

data class RegionItem(val id: RegionId, val start: Double, val end: Double)
enum class CandidateType {
    HIGHER,
    LOWER,
    INITIAL
}

fun findCandidateRegions(list: List<RegionReference>) {
    var bestSplitter: Splitter? = null
    var bestCandidate: CandidateScore? = null
    Axis.entries.forEach { axis ->
        val regionItems = list.map { toRegionItem(it, axis) }
        val candidate = findSeparator(regionItems)

        val acceptedDifference = acceptedDifference(list.size)

        // if: bestCandidate is null OR
        // has ("okay" diff OR better/same diff) and better inBetween OR
        // has same inBetween but better diff
        if (bestCandidate == null ||
            ((candidate.diff <= acceptedDifference || candidate.diff <= bestCandidate!!.diff) &&
                    candidate.inBetween < bestCandidate!!.inBetween) ||
            candidate.inBetween <= bestCandidate!!.inBetween && candidate.diff < bestCandidate!!.diff
        ) {
            bestCandidate = candidate
            bestSplitter = Splitter(axis, candidate.value)
        }
    }

    requireNotNull(bestCandidate) { "Best Candidate must be set" }

    val regionItems = bestCandidate!!.compareResult.entries.withIndex().flatMap { (index, entry) ->
        val (item, type) = entry
        val regionReference = list[index]
        require(regionReference.regionId == item.id) { "List order is tangled" }

        if (type == AxisComparison.INTERSECTING) {
            listOf(
                regionReference.customBoxCopy(regionReference.box.cutEdge(bestSplitter!!, lower = true)),
                regionReference.customBoxCopy(regionReference.box.cutEdge(bestSplitter!!, lower = false))
            )
        } else listOf(regionReference)
    }
}

fun toRegionItem(regionReference: RegionReference, axis: Axis): RegionItem {
    val (start, end) = order(regionReference.box, axis)
    return RegionItem(regionReference.regionId, start, end)
}

fun order(box: Box, axis: Axis): Pair<Double, Double> {
    val firstVal = box.first.value(axis)
    val secondVal = box.second.value(axis)
    return if (firstVal < secondVal) Pair(firstVal, secondVal) else Pair(secondVal, firstVal)
}

fun cutValues(candidate: CandidateScore): CandidateScore {
    val newValues = candidate.compareResult.entries.flatMap { (item, type) ->
        val (id, start, end) = item

        if (type != AxisComparison.INTERSECTING) listOf(item to type)
        else listOf(
            RegionItem(id, start, candidate.value) to AxisComparison.BEFORE,
            RegionItem(id, candidate.value, end) to AxisComparison.AFTER
        )
    }

    return candidate.also { it.compareResult.entries = newValues }
}

internal fun Box.cutEdge(splitter: Splitter, lower: Boolean): Box {
    val firstVal = this.first.value(splitter.axis)
    val secondVal = this.second.value(splitter.axis)
    return if (lower) {
        if (firstVal < secondVal) Box(this.first, this.second.set(splitter.value))
        else Box(this.first.set(splitter.value), this.second)
    } else {
        if (firstVal > secondVal) Box(this.first, this.second.set(splitter.value))
        else Box(this.second.set(splitter.value), this.first)
    }
}

fun Vector3d.set(axis: Axis, value: Double): Vector3d {
    return when (axis) {
        Axis.X -> this.set(value, this.y, this.z)
        Axis.Y -> this.set(this.x, value, this.z)
        Axis.Z -> this.set(this.x, this.y, value)
    }
}

fun acceptedDifference(size: Int): Int {
    return (size * 0.025).run { if (this < 1) 1.0 else this }.toInt()
}

fun findSeparator(items: List<RegionItem>): CandidateScore {
    assert(items.size > 1) { "Need at least 2 items" }

    val estimate = items.map { it.id to (it.start + it.end) / 2 }.sortedBy { it.second }[items.size / 2]

    val rawCandidates = items
        .flatMap { listOf(it.id to it.start, it.id to it.end) }
        .distinctBy { it.second }
        .sortedBy { (it.second - estimate.second).absoluteValue }

    val lowerCandidates = rawCandidates.filter { it.second < estimate.second }.map { it to CandidateType.LOWER }
    val higherCandidates = rawCandidates.filter { it.second > estimate.second }.map { it to CandidateType.HIGHER }

    val candidates = listOf((estimate to CandidateType.INITIAL)) + lowerCandidates + higherCandidates
    val previewCandidates =
        listOf((estimate to CandidateType.INITIAL)) + lowerCandidates.take(2) + higherCandidates.take(2)

    val leftCandidates = candidates
        .filter { (candidate, _) -> candidate.first !in previewCandidates.map { it.first.first } }
        .toMutableList()

    val acceptingDifference = acceptedDifference(items.size)

    var bestCandidate: CandidateScore? = null
    var initialEstimateCandidate: CandidateScore? = null

    fun List<Pair<Pair<RegionId, Double>, CandidateType>>.parseItems(onBestCandidateChanged: ((CandidateScore) -> Unit)? = null) {
        this.forEach {
            val value = it.first.second
            val type = it.second
            val compareResult = compareItems(items, value)

            val difference = (compareResult.afterAmount - compareResult.beforeAmount).absoluteValue
            val inBetween = compareResult.intersectingAmount

            if (bestCandidate == null ||
                (difference <= acceptingDifference || difference <= bestCandidate!!.diff) && inBetween <= bestCandidate!!.inBetween
            ) {
                bestCandidate = CandidateScore(value, difference, inBetween, compareResult, type)
                if (type == CandidateType.INITIAL) {
                    initialEstimateCandidate = bestCandidate
                }
                onBestCandidateChanged?.invoke(bestCandidate!!)
            }
        }
    }

    previewCandidates.parseItems()

    requireNotNull(bestCandidate) { "No best candidate" }
    requireNotNull(initialEstimateCandidate) { "No initial estimate candidate" }

    if (bestCandidate!!.inBetween == 0) return bestCandidate!!

    if (initialEstimateCandidate!!.diff == bestCandidate!!.diff &&
        initialEstimateCandidate!!.inBetween == bestCandidate!!.inBetween
    ) return initialEstimateCandidate!!

    if (leftCandidates.isEmpty()) return bestCandidate!!

    if (bestCandidate!!.type == CandidateType.INITIAL) return bestCandidate!!
    else {
        if (bestCandidate!!.type == CandidateType.HIGHER) {
            leftCandidates.removeAll { it.second == CandidateType.LOWER }
        } else {
            leftCandidates.removeAll { it.second == CandidateType.HIGHER }
        }
    }

    while (true) {
        if (leftCandidates.isEmpty()) break

        val newCandidates = leftCandidates.take(3)
        leftCandidates.removeAll { (entry, _) -> entry.first in newCandidates.map { it.first.first } }

        var bestChanged = false
        newCandidates.parseItems(onBestCandidateChanged = {
            bestChanged = true
        })

        if (!bestChanged) break
    }
    return bestCandidate!!
}

enum class AxisComparison {
    BEFORE,
    AFTER,
    INTERSECTING
}

class CompareResult(
    var entries: List<Pair<RegionItem, AxisComparison>>,
    val beforeAmount: Int,
    val afterAmount: Int,
    val intersectingAmount: Int,
)

var calc = 0

fun compareItems(items: List<RegionItem>, compareValue: Double): CompareResult {
    var beforeAmount = 0
    var afterAmount = 0
    var intersectingAmount = 0

    return CompareResult(
        items.map {
            val type: AxisComparison = when {
                it.end <= compareValue -> {
                    beforeAmount += 1
                    AxisComparison.BEFORE
                }

                it.start >= compareValue -> {
                    afterAmount += 1
                    AxisComparison.AFTER
                }

                else -> {
                    intersectingAmount += 1
                    AxisComparison.INTERSECTING
                }
            }
            calc += 1
            println(calc)

            it to type
        },
        beforeAmount,
        afterAmount,
        intersectingAmount,
    )
}
