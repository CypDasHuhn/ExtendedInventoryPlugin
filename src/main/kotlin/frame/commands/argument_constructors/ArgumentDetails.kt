package de.cypdashuhn.rooster.commands.argument_constructors

class ArgumentDetails {
    private var pInvoke: InvokeLambda? = null
    val invoke
        get() = pInvoke

    private var pFollowingArguments: ArgumentList? = null
    val followingArguments
        get() = pFollowingArguments

    private var pErrorMissingChildArg: ((ArgumentInfo) -> Unit)? = null
    val errorMissingChildArg
        get() = pErrorMissingChildArg

    constructor(
        invoke: InvokeLambda
    ) {
        this.pInvoke = invoke
    }

    constructor(
        followingArguments: ArgumentList,
        errorMissingChildArg: (ArgumentInfo) -> Unit
    ) {
        this.pFollowingArguments = followingArguments
        this.pErrorMissingChildArg = errorMissingChildArg
    }

    constructor(
        followingArguments: CentralizedArgumentList
    ) {
        this.pFollowingArguments = followingArguments
    }

    constructor(
        invoke: InvokeLambda,
        followingArguments: ArgumentList,
        errorMissingChildArg: (ArgumentInfo) -> Unit
    ) {
        this.pInvoke = invoke
        this.pFollowingArguments = followingArguments
        this.pErrorMissingChildArg = errorMissingChildArg
    }

    constructor(
        invoke: InvokeLambda,
        followingArguments: CentralizedArgumentList
    ) {
        this.pInvoke = invoke
        this.pFollowingArguments = followingArguments
    }
}