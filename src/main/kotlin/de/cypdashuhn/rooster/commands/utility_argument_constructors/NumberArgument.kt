package de.cypdashuhn.rooster.commands.utility_argument_constructors

import de.cypdashuhn.rooster.commands.argument_constructors.*
import de.cypdashuhn.rooster.localization.tSend

@Suppress("unused")
object NumberArgument {
    fun number(
        key: String = "number",
        notANumberError: (ArgumentInfo) -> Unit,
        acceptDecimals: Boolean = false,
        decimalNotAcceptedError: (ArgumentInfo) -> Unit,
        acceptNegatives: Boolean = false,
        negativesNotAcceptedError: (ArgumentInfo) -> Unit,
        furtherCondition: PredicateToAction? = null,
        tabCompleterPlaceholder: String = "number_key", /* set translations to stuff like "[number]" */
        errorMissing: (ArgumentInfo) -> Unit,
        argumentDetails: ArgumentDetails,
    ): CentralArgument {
        return CentralArgument(
            key = key,
            isValid = { argInfo ->
                val arg = argInfo.arg
                if (arg.toDoubleOrNull() == null) {
                    Pair(false, notANumberError)
                }
                if (!acceptDecimals || arg.toDoubleOrNull() != null) {
                    Pair(false, decimalNotAcceptedError)
                }
                if (acceptDecimals && arg.toDoubleOrNull() != null) {
                    if (!acceptNegatives && arg.toDouble() < 0) {
                        Pair(false, negativesNotAcceptedError)
                    }
                }
                if (!acceptNegatives || arg.toUIntOrNull() == null) {
                    Pair(false, negativesNotAcceptedError)
                }

                furtherCondition?.let {
                    furtherCondition(argInfo)
                }

                Pair(true) {}
            },
            argumentHandler = {
                if (acceptDecimals) it.arg.toDouble()
                else it.arg.toInt()
            },
            argumentDetails = argumentDetails,
            errorMissing = errorMissing,
            tabCompletions = { listOf(tabCompleterPlaceholder) }
        )
    }

    fun number(
        key: String = "number",
        notANumberErrorMessageKey: String,
        decimalNotAcceptedErrorMessageKey: String? = null,
        negativesNotAcceptedErrorMessageKey: String? = null,
        numArg: String = "num",
        furtherCondition: PredicateToAction? = null,
        /** set translations to stuff like "\[number]" */
        tabCompleterPlaceholder: String = "placeholder_number_key",
        errorMissingMessageKey: String,
        argumentDetails: ArgumentDetails,
    ): CentralArgument {
        return number(
            key = key,
            notANumberError = { it.sender.tSend(notANumberErrorMessageKey, numArg to it.arg) },
            acceptDecimals = decimalNotAcceptedErrorMessageKey != null,
            decimalNotAcceptedError = { it.sender.tSend(decimalNotAcceptedErrorMessageKey ?: "", numArg to it.arg) },
            acceptNegatives = negativesNotAcceptedErrorMessageKey != null,
            negativesNotAcceptedError = {
                it.sender.tSend(
                    negativesNotAcceptedErrorMessageKey ?: "",
                    numArg to it.arg
                )
            },
            furtherCondition = furtherCondition,
            tabCompleterPlaceholder = tabCompleterPlaceholder,
            errorMissing = { it.sender.tSend(errorMissingMessageKey) },
            argumentDetails = argumentDetails
        )
    }

    fun xyCoordinates(
        xKey: String = "X",
        yKey: String = "Y",
        numberArg: String = "number",
        notANumberErrorMessageKey: String = "not_a_number",
        /**
         * setting this field to a null value will enable decimals. by default,
         * decimals will not be passed.
         */
        decimalErrorMessageKey: String? = "decimal_error",
        /**
         * setting this field to a non-null value will disable negatives. by
         * default, negatives will be passed.
         */
        negativesNotAcceptedErrorMessageKey: String? = null,
        errorMissingXMessageKey: String = "error_missing_num",
        errorMissingYMessageKey: String = "error_missing_num",
        xCondition: PredicateToAction? = null,
        yCondition: PredicateToAction? = null,
        argumentDetails: ArgumentDetails
    ): CentralArgument {
        return number(
            key = xKey,
            notANumberErrorMessageKey = notANumberErrorMessageKey,
            decimalNotAcceptedErrorMessageKey = decimalErrorMessageKey,
            negativesNotAcceptedErrorMessageKey = negativesNotAcceptedErrorMessageKey,
            numArg = numberArg,
            errorMissingMessageKey = errorMissingXMessageKey,
            furtherCondition = xCondition,
            argumentDetails = ArgumentDetails(
                followingArguments = CentralizedArgumentList(
                    number(
                        key = yKey,
                        notANumberErrorMessageKey = notANumberErrorMessageKey,
                        decimalNotAcceptedErrorMessageKey = decimalErrorMessageKey,
                        negativesNotAcceptedErrorMessageKey = negativesNotAcceptedErrorMessageKey,
                        numArg = numberArg,
                        errorMissingMessageKey = errorMissingYMessageKey,
                        furtherCondition = yCondition,
                        argumentDetails = argumentDetails,
                    )
                )
            )
        )
    }

    fun xyzCoordinates(
        xKey: String = "X",
        yKey: String = "Y",
        zKey: String = "Z",
        numberArg: String = "number",
        notANumberErrorMessageKey: String = "not_a_number",
        /**
         * setting this field to a null value will enable decimals. by default,
         * decimals will not be passed.
         */
        decimalErrorMessageKey: String? = "decimal_error",
        /**
         * setting this field to a non-null value will disable negatives. by
         * default, negatives will be passed.
         */
        negativesNotAcceptedErrorMessageKey: String? = null,
        errorMissingXMessageKey: String = "error_missing_num",
        errorMissingYMessageKey: String = "error_missing_num",
        xCondition: PredicateToAction? = null,
        disableYCondition: Boolean = false,
        yCondition: PredicateToAction? = { (sender, _, arg, _, _) ->
            val num = arg.toDouble()
            when {
                num < -64.0 -> {
                    Pair(false) { sender.tSend("number_under_build_height_error", numberArg to arg) }
                }

                num > 320.0 -> {
                    Pair(false) {
                        sender.tSend("number_over_build_height_error", numberArg to arg)
                    }
                }

                else -> {
                    Pair(true) {}
                }
            }
        },
        zCondition: PredicateToAction? = null,
        argumentDetails: ArgumentDetails
    ): CentralArgument {
        return number(
            key = xKey,
            notANumberErrorMessageKey = notANumberErrorMessageKey,
            decimalNotAcceptedErrorMessageKey = decimalErrorMessageKey,
            negativesNotAcceptedErrorMessageKey = negativesNotAcceptedErrorMessageKey,
            numArg = numberArg,
            errorMissingMessageKey = errorMissingXMessageKey,
            furtherCondition = xCondition,
            argumentDetails = ArgumentDetails(
                followingArguments = CentralizedArgumentList(
                    number(
                        key = yKey,
                        notANumberErrorMessageKey = notANumberErrorMessageKey,
                        decimalNotAcceptedErrorMessageKey = decimalErrorMessageKey,
                        negativesNotAcceptedErrorMessageKey = negativesNotAcceptedErrorMessageKey,
                        numArg = numberArg,
                        errorMissingMessageKey = errorMissingYMessageKey,
                        furtherCondition = if (disableYCondition) null else yCondition,
                        argumentDetails = ArgumentDetails(
                            followingArguments = CentralizedArgumentList(
                                number(
                                    key = zKey,
                                    notANumberErrorMessageKey = notANumberErrorMessageKey,
                                    decimalNotAcceptedErrorMessageKey = decimalErrorMessageKey,
                                    negativesNotAcceptedErrorMessageKey = negativesNotAcceptedErrorMessageKey,
                                    numArg = numberArg,
                                    errorMissingMessageKey = errorMissingYMessageKey,
                                    furtherCondition = zCondition,
                                    argumentDetails = argumentDetails,
                                )
                            )
                        )
                    )
                )
            )
        )
    }
}