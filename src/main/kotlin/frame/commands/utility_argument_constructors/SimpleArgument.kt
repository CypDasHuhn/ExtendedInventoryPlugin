package de.cypdashuhn.rooster.commands.utility_argument_constructors

import de.cypdashuhn.rooster.commands.argument_constructors.*
import de.cypdashuhn.rooster.commands.argument_constructors.*

@Suppress("unused")
object SimpleArgument {
    fun simple(
        name: String,
        isValid: PredicateToAction? = null,
        isValidCompleter: ArgumentPredicate? = null,
        errorArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        argumentHandler: ArgumentHandler = returnString(),
        argumentDetails: ArgumentDetails
    ): Argument {
        return Argument(
            isArgument = { argInfo -> argInfo.arg == name },
            tabCompletions = { listOf(name) },
            isValid = isValid,
            isValidCompleter = isValidCompleter,
            argumentDetails = argumentDetails,
            errorArgumentOverflow = errorArgumentOverflow,
            argumentHandler = argumentHandler,
            key = name
        )
    }

    fun simple(
        names: List<String>,
        isValid: PredicateToAction? = null,
        isValidCompleter: ArgumentPredicate? = null,
        errorArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        argumentHandler: ArgumentHandler = returnString(),
        argumentDetails: ArgumentDetails,
        key: String
    ): Argument {
        return Argument(
            isArgument = { argInfo -> names.contains(argInfo.arg) },
            tabCompletions = { names },
            isValid = isValid,
            isValidCompleter = isValidCompleter,
            argumentDetails = argumentDetails,
            errorArgumentOverflow = errorArgumentOverflow,
            argumentHandler = argumentHandler,
            key = key
        )
    }
}