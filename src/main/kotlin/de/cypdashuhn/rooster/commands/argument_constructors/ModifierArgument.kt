package de.cypdashuhn.rooster.commands.argument_constructors

fun returnTrue(): ArgumentHandler = { true }

class ModifierArgument(
    // [11]
    isArgument: ArgumentPredicate,
    tabCompletions: CompletionLambda,
    isValid: ((ArgumentInfo) -> Pair<Boolean, ((ArgumentInfo) -> Unit)?>)? = null,
    isValidCompleter: ArgumentPredicate? = null,
    argumentHandler: ArgumentHandler = returnTrue(),
    key: String,
) : BaseArgument(
    isArgument = isArgument,
    tabCompletions = tabCompletions,
    isValidCompleter = isValidCompleter,
    invoke = null,
    argumentHandler = argumentHandler,
    isValid = isValid,
    isModifier = true,
    errorMissing = null,
    followingArguments = null,
    errorArgumentOverflow = null,
    errorMissingChildArg = null,
    key = key
)