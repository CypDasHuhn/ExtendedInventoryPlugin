package de.cypdashuhn.rooster.commands.argument_constructors

import org.bukkit.command.CommandSender

class CentralArgument : BaseArgument {
    constructor(
        // invoke [6]
        isArgument: ArgumentPredicate = defaultTrue,
        tabCompletions: CompletionLambda,
        isValid: ((ArgumentInfo) -> Pair<Boolean, ((ArgumentInfo) -> Unit)?>)? = null,
        isValidCompleter: ArgumentPredicate? = null,
        invoke: InvokeLambda,
        errorMissing: ErrorLambda,
        errorArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        argumentHandler: ArgumentHandler = returnString(),
        key: String,
    ) : super(
        isArgument = isArgument,
        tabCompletions = tabCompletions,
        isValidCompleter = isValidCompleter,
        invoke = invoke,
        argumentHandler = argumentHandler,
        isValid = isValid,
        isModifier = false,
        errorMissing = errorMissing,
        followingArguments = null,
        errorArgumentOverflow = errorArgumentOverflow,
        errorMissingChildArg = null,
        key = key
    )

    constructor(
        // tree de-centralized [7]
        isArgument: ArgumentPredicate = defaultTrue,
        tabCompletions: CompletionLambda,
        isValid: ((ArgumentInfo) -> Pair<Boolean, ((ArgumentInfo) -> Unit)?>)? = null,
        isValidCompleter: ArgumentPredicate? = null,
        errorMissing: ErrorLambda,
        errorArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        followingArguments: ArgumentList,
        errorMissingChildArg: ((ArgumentInfo) -> Unit),
        argumentHandler: ArgumentHandler = returnString(),
        key: String,
    ) : super(
        isArgument = isArgument,
        tabCompletions = tabCompletions,
        isValidCompleter = isValidCompleter,
        invoke = null,
        argumentHandler = argumentHandler,
        isValid = isValid,
        isModifier = false,
        errorMissing = errorMissing,
        followingArguments = followingArguments,
        errorArgumentOverflow = errorArgumentOverflow,
        errorMissingChildArg = errorMissingChildArg,
        key = key
    )

    constructor(
        // tree centralized [8]
        isArgument: ArgumentPredicate = defaultTrue,
        tabCompletions: CompletionLambda,
        isValid: ((ArgumentInfo) -> Pair<Boolean, ((ArgumentInfo) -> Unit)?>)? = null,
        isValidCompleter: ArgumentPredicate? = null,
        errorMissing: ErrorLambda,
        errorArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        followingArguments: CentralizedArgumentList,
        argumentHandler: ArgumentHandler = returnString(),
        key: String,
    ) : super(
        isArgument = isArgument,
        tabCompletions = tabCompletions,
        isValidCompleter = isValidCompleter,
        invoke = null,
        argumentHandler = argumentHandler,
        isValid = isValid,
        isModifier = false,
        errorMissing = errorMissing,
        followingArguments = followingArguments,
        errorArgumentOverflow = errorArgumentOverflow,
        errorMissingChildArg = null,
        key = key
    )

    constructor(
        // combined de-centralized [9]
        isArgument: ArgumentPredicate = defaultTrue,
        tabCompletions: CompletionLambda,
        isValid: ((ArgumentInfo) -> Pair<Boolean, ((ArgumentInfo) -> Unit)?>)? = null,
        isValidCompleter: ArgumentPredicate? = null,
        invoke: InvokeLambda,
        errorMissing: ErrorLambda,
        errorArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        followingArguments: ArgumentList,
        errorMissingChildArg: ((ArgumentInfo) -> Unit),
        argumentHandler: ArgumentHandler = returnString(),
        key: String,
    ) : super(
        isArgument = isArgument,
        tabCompletions = tabCompletions,
        isValidCompleter = isValidCompleter,
        invoke = invoke,
        argumentHandler = argumentHandler,
        isValid = isValid,
        isModifier = false,
        errorMissing = errorMissing,
        followingArguments = followingArguments,
        errorArgumentOverflow = errorArgumentOverflow,
        errorMissingChildArg = errorMissingChildArg,
        key = key
    )

    constructor(
        // combined centralized [10]
        isArgument: ArgumentPredicate = defaultTrue,
        tabCompletions: CompletionLambda,
        isValid: PredicateToAction? = null,
        isValidCompleter: ArgumentPredicate? = null,
        invoke: InvokeLambda,
        errorMissing: ErrorLambda,
        errorArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        followingArguments: CentralizedArgumentList,
        argumentHandler: ArgumentHandler = returnString(),
        key: String,
    ) : super(
        isArgument = isArgument,
        tabCompletions = tabCompletions,
        isValidCompleter = isValidCompleter,
        invoke = invoke,
        argumentHandler = argumentHandler,
        isValid = isValid,
        isModifier = false,
        errorMissing = errorMissing,
        followingArguments = followingArguments,
        errorArgumentOverflow = errorArgumentOverflow,
        errorMissingChildArg = null,
        key = key
    )

    constructor(
        // argument details [18]
        isArgument: ArgumentPredicate = defaultTrue,
        tabCompletions: CompletionLambda,
        isValid: ((ArgumentInfo) -> Pair<Boolean, ((ArgumentInfo) -> Unit)?>)? = null,
        isValidCompleter: ArgumentPredicate? = null,
        errorArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        argumentHandler: ArgumentHandler = returnString(),
        errorMissing: ErrorLambda,
        argumentDetails: ArgumentDetails,
        key: String,
    ) : super(
        isArgument = isArgument,
        tabCompletions = tabCompletions,
        isValidCompleter = isValidCompleter,
        invoke = argumentDetails.invoke,
        argumentHandler = argumentHandler,
        isValid = isValid,
        isModifier = false,
        errorMissing = errorMissing,
        followingArguments = argumentDetails.followingArguments,
        errorArgumentOverflow = errorArgumentOverflow,
        errorMissingChildArg = argumentDetails.errorMissingChildArg,
        key = key
    )

    fun toRoot(label: String, startingUnit: ((CommandSender) -> Boolean)?): RootArgument {
        return RootArgument(
            isValid = isValid,
            errorArgumentOverflow = errorArgumentOverflow,
            key = key,
            label = label,
            startingUnit = startingUnit,
            argumentHandler = argumentHandler,
            argumentDetails = this.details()
        )
    }

    fun toNormal(): Argument {
        return Argument(
            isArgument = isArgument,
            tabCompletions = tabCompletions!!,
            isValidCompleter = isValidCompleter,
            argumentHandler = argumentHandler,
            isValid = isValid,
            errorArgumentOverflow = errorArgumentOverflow,
            argumentDetails = this.details(),
            key = key
        )
    }
}