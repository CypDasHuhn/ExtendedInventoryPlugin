package de.cypdashuhn.rooster.commands.argument_constructors

import org.bukkit.command.CommandSender

class Argument : BaseArgument {
    constructor(
        // invoke [1]
        isArgument: ArgumentPredicate = defaultTrue,
        tabCompletions: CompletionLambda,
        isValid: ((ArgumentInfo) -> Pair<Boolean, ((ArgumentInfo) -> Unit)?>)? = null,
        isValidCompleter: ArgumentPredicate? = null,
        invoke: InvokeLambda,
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
        errorMissing = null,
        followingArguments = null,
        errorArgumentOverflow = errorArgumentOverflow,
        errorMissingChildArg = null,
        key = key
    )

    constructor(
        // tree de-centralized [2]
        isArgument: ArgumentPredicate = defaultTrue,
        tabCompletions: CompletionLambda,
        isValid: ((ArgumentInfo) -> Pair<Boolean, ((ArgumentInfo) -> Unit)?>)? = null,
        isValidCompleter: ArgumentPredicate? = null,
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
        errorMissing = null,
        followingArguments = followingArguments,
        errorArgumentOverflow = errorArgumentOverflow,
        errorMissingChildArg = errorMissingChildArg,
        key = key
    )

    constructor(
        // tree centralized [3]
        isArgument: ArgumentPredicate = defaultTrue,
        tabCompletions: CompletionLambda,
        isValid: ((ArgumentInfo) -> Pair<Boolean, ((ArgumentInfo) -> Unit)?>)? = null,
        isValidCompleter: ArgumentPredicate? = null,
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
        errorMissing = null,
        followingArguments = followingArguments,
        errorArgumentOverflow = errorArgumentOverflow,
        errorMissingChildArg = null,
        key = key
    )

    constructor(
        // combined de-centralized [4]
        isArgument: ArgumentPredicate = defaultTrue,
        tabCompletions: CompletionLambda,
        isValid: ((ArgumentInfo) -> Pair<Boolean, ((ArgumentInfo) -> Unit)?>)? = null,
        isValidCompleter: ArgumentPredicate? = null,
        invoke: InvokeLambda,
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
        errorMissing = null,
        followingArguments = followingArguments,
        errorArgumentOverflow = errorArgumentOverflow,
        errorMissingChildArg = errorMissingChildArg,
        key = key
    )

    constructor(
        // combined centralized [5]
        isArgument: ArgumentPredicate = defaultTrue,
        tabCompletions: CompletionLambda,
        isValid: ((ArgumentInfo) -> Pair<Boolean, ((ArgumentInfo) -> Unit)?>)? = null,
        isValidCompleter: ArgumentPredicate? = null,
        invoke: InvokeLambda,
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
        errorMissing = null,
        followingArguments = followingArguments,
        errorArgumentOverflow = errorArgumentOverflow,
        errorMissingChildArg = null,
        key = key
    )

    constructor(
        // argument details [17]
        isArgument: ArgumentPredicate = defaultTrue,
        tabCompletions: CompletionLambda,
        isValid: ((ArgumentInfo) -> Pair<Boolean, ((ArgumentInfo) -> Unit)?>)? = null,
        isValidCompleter: ArgumentPredicate? = null,
        errorArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        argumentHandler: ArgumentHandler = returnString(),
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
        errorMissing = null,
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


    fun toCentral(errorMissing: ErrorLambda): CentralArgument {
        return CentralArgument(
            isArgument = isArgument,
            tabCompletions = tabCompletions!!,
            isValidCompleter = isValidCompleter,
            argumentHandler = argumentHandler,
            isValid = isValid,
            errorMissing = errorMissing,
            errorArgumentOverflow = errorArgumentOverflow,
            argumentDetails = this.details(),
            key = key
        )
    }
}