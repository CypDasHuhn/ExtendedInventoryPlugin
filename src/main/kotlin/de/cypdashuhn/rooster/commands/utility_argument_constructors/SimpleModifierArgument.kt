package de.cypdashuhn.rooster.commands.utility_argument_constructors

import de.cypdashuhn.rooster.commands.argument_constructors.*

@Suppress("unused")
object SimpleModifierArgument {
    fun simple(
        name: String,
        isValid: ((ArgumentInfo) -> Pair<Boolean, ((ArgumentInfo) -> Unit)?>)? = null,
        isValidCompleter: ArgumentPredicate? = null,
        argumentHandler: ArgumentHandler = returnTrue()
    ): ModifierArgument {
        return ModifierArgument(
            key = name,
            isArgument = { it.arg == name },
            tabCompletions = { listOf(name) },
            isValid = isValid,
            isValidCompleter = isValidCompleter,
            argumentHandler = argumentHandler
        )
    }
}