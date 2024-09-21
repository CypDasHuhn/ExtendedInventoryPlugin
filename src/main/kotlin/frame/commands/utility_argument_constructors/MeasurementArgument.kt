package de.cypdashuhn.rooster.commands.utility_argument_constructors

import de.cypdashuhn.rooster.commands.argument_constructors.ArgumentDetails
import de.cypdashuhn.rooster.commands.argument_constructors.CentralArgument
import kotlin.reflect.KClass

/** Generally unfinished in every aspect. just ignore! */
@Suppress("unused", "unused_parameter")
object MeasurementArgument {
    enum class MeasurementType(val enum: KClass<*>) {
        TIME(Time::class),
        WEIGHT(Weight::class),
    }

    enum class Time(val short: String) {
        MILLISECOND("ms"),
        SECOND("s"),
        MINUTE("m"),
        HOUR("h"),
        DAY("d"),
        WEEK("wk"),
        MONTH("mnt"),
        YEAR("y");
    }

    enum class Weight(val short: String) {
        MICROGRAM("mg"),
        GRAM("g"),
        KILOGRAM("kg"),
        TON("t"),
    }

    fun measurement(
        key: String,
        measurementType: MeasurementType,

        argumentDetails: ArgumentDetails,
    ): CentralArgument {
        return CentralArgument(
            key = key,
            tabCompletions = { argInfo ->
                val listOfShorts = listOf("")
                listOfShorts.map { "${argInfo.arg}$it" } // return arg with appended suffix
            },
            isValid = { (_, _, arg, _, _) ->
                val listOfShorts = listOf("")
                val hasValidAmount = listOfShorts
                    .filter { arg.endsWith(it) }
                    .map { arg.substring(arg.length - it.length..arg.length) } // arg without substring. basically amount
                    .map { it.toDoubleOrNull() }
                    .any { it != null }

                if (hasValidAmount) {
                    Pair(true, {})
                } else {
                    Pair(false) { "Invalid measurement" }
                }
            },
            argumentDetails = argumentDetails,
            errorMissing = {}
        )
    }
}