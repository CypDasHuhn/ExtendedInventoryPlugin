package de.cypdashuhn.rooster.commands.argument_constructors

import org.bukkit.command.CommandSender

class RootArgument : BaseArgument {
    var label: String
    var startingUnit: ((CommandSender) -> Boolean)? = null

    constructor( // invoke [12]
        isValid: ((ArgumentInfo) -> Pair<Boolean, ((ArgumentInfo) -> Unit)?>)? = null,
        invoke: InvokeLambda,
        errorArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        argumentHandler: ArgumentHandler = returnString(),
        key: String = "label",
        label: String,
        startingUnit: ((CommandSender) -> Boolean)? = null
    ) : super(
        isArgument = defaultTrue,
        tabCompletions = null,
        isValidCompleter = null,
        invoke = invoke,
        argumentHandler = argumentHandler,
        isValid = isValid,
        isModifier = false,
        errorMissing = null,
        followingArguments = null,
        errorArgumentOverflow = errorArgumentOverflow,
        errorMissingChildArg = null,
        key = key
    ) {
        this.label = label
        this.startingUnit = startingUnit
    }

    constructor( // following decentralized [13]
        isValid: ((ArgumentInfo) -> Pair<Boolean, ((ArgumentInfo) -> Unit)?>)? = null,
        errorArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        followingArguments: ArgumentList,
        errorMissingChildArg: ((ArgumentInfo) -> Unit),
        argumentHandler: ArgumentHandler = returnString(),
        key: String = "label",
        label: String,
        startingUnit: ((CommandSender) -> Boolean)? = null
    ) : super(
        isArgument = defaultTrue,
        tabCompletions = null,
        isValidCompleter = null,
        invoke = null,
        argumentHandler = argumentHandler,
        isValid = isValid,
        isModifier = false,
        errorMissing = null,
        followingArguments = followingArguments,
        errorArgumentOverflow = errorArgumentOverflow,
        errorMissingChildArg = errorMissingChildArg,
        key = key
    ) {
        this.label = label
        this.startingUnit = startingUnit
    }

    constructor( // following centralized [14]
        isValid: ((ArgumentInfo) -> Pair<Boolean, ((ArgumentInfo) -> Unit)?>)? = null,
        errorArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        followingArguments: CentralizedArgumentList,
        argumentHandler: ArgumentHandler = returnString(),
        key: String = "label",
        label: String,
        startingUnit: ((CommandSender) -> Boolean)? = null
    ) : super(
        isArgument = defaultTrue,
        tabCompletions = null,
        isValidCompleter = null,
        invoke = null,
        argumentHandler = argumentHandler,
        isValid = isValid,
        isModifier = false,
        errorMissing = null,
        followingArguments = followingArguments,
        errorArgumentOverflow = errorArgumentOverflow,
        errorMissingChildArg = null,
        key = key
    ) {
        this.label = label
        this.startingUnit = startingUnit
    }

    constructor( // combined decentralized [15]
        isValid: ((ArgumentInfo) -> Pair<Boolean, ((ArgumentInfo) -> Unit)?>)? = null,
        invoke: InvokeLambda,
        errorArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        followingArguments: ArgumentList,
        errorMissingChildArg: ((ArgumentInfo) -> Unit),
        argumentHandler: ArgumentHandler = returnString(),
        key: String = "label",
        label: String,
        startingUnit: ((CommandSender) -> Boolean)? = null
    ) : super(
        isArgument = defaultTrue,
        tabCompletions = null,
        isValidCompleter = null,
        invoke = invoke,
        argumentHandler = argumentHandler,
        isValid = isValid,
        isModifier = false,
        errorMissing = null,
        followingArguments = followingArguments,
        errorArgumentOverflow = errorArgumentOverflow,
        errorMissingChildArg = errorMissingChildArg,
        key = key
    ) {
        this.label = label
        this.startingUnit = startingUnit
    }

    constructor( // combined centralized [16]
        isValid: ((ArgumentInfo) -> Pair<Boolean, ((ArgumentInfo) -> Unit)?>)? = null,
        invoke: InvokeLambda? = null,
        errorArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        followingArguments: CentralizedArgumentList,
        argumentHandler: ArgumentHandler = returnString(),
        key: String = "label",
        label: String,
        startingUnit: ((CommandSender) -> Boolean)? = null
    ) : super(
        isArgument = defaultTrue,
        tabCompletions = null,
        isValidCompleter = null,
        invoke = invoke,
        argumentHandler = argumentHandler,
        isValid = isValid,
        isModifier = false,
        errorMissing = null,
        followingArguments = followingArguments,
        errorArgumentOverflow = errorArgumentOverflow,
        errorMissingChildArg = null,
        key = key
    ) {
        this.label = label
        this.startingUnit = startingUnit
    }

    constructor(
        // argument details [19]
        isValid: ((ArgumentInfo) -> Pair<Boolean, ((ArgumentInfo) -> Unit)?>)? = null,
        errorArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        argumentHandler: ArgumentHandler = returnString(),
        argumentDetails: ArgumentDetails,
        key: String = "label",
        label: String,
        startingUnit: ((CommandSender) -> Boolean)? = null
    ) : super(
        isArgument = defaultTrue,
        tabCompletions = null,
        isValidCompleter = null,
        invoke = argumentDetails.invoke,
        argumentHandler = argumentHandler,
        isValid = isValid,
        isModifier = false,
        errorMissing = null,
        followingArguments = argumentDetails.followingArguments,
        errorArgumentOverflow = errorArgumentOverflow,
        errorMissingChildArg = argumentDetails.errorMissingChildArg,
        key = key
    ) {
        this.label = label
        this.startingUnit = startingUnit
    }

    fun toDecentral(
        isArgument: ArgumentPredicate = defaultTrue,
        tabCompletions: CompletionLambda,
        isValidCompleter: ArgumentPredicate? = null,
        key: String = "label",
    ): Argument {
        return Argument(
            isArgument = isArgument,
            tabCompletions = tabCompletions,
            isValidCompleter = isValidCompleter,
            argumentHandler = argumentHandler,
            isValid = isValid,
            errorArgumentOverflow = errorArgumentOverflow,
            argumentDetails = this.details(),
            key = key
        )
    }

    fun toNormal(
        isArgument: ArgumentPredicate = defaultTrue,
        tabCompletions: CompletionLambda,
        isValidCompleter: ArgumentPredicate? = null,
        errorMissing: ErrorLambda,
        key: String = "label",
    ): CentralArgument {
        return CentralArgument(
            isArgument = isArgument,
            tabCompletions = tabCompletions,
            isValidCompleter = isValidCompleter,
            argumentHandler = argumentHandler,
            isValid = isValid,
            errorArgumentOverflow = errorArgumentOverflow,
            argumentDetails = this.details(),
            errorMissing = errorMissing,
            key = key
        )
    }
}