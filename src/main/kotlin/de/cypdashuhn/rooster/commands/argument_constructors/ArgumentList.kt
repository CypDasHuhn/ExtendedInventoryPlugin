package de.cypdashuhn.rooster.commands.argument_constructors

open class ArgumentList {
    var arguments: (ArgumentInfo) -> List<BaseArgument>

    constructor(vararg arguments: BaseArgument) {
        this.arguments = { listOf(*arguments) }
    }

    constructor(arguments: (ArgumentInfo) -> List<BaseArgument>) {
        this.arguments = arguments
    }
}

class CentralizedArgumentList : ArgumentList {
    constructor(
        centralArgument: CentralArgument,
        vararg arguments: BaseArgument
    ) : super(*(arguments.toMutableList().apply { add(centralArgument) }).toTypedArray())

    constructor(
        centralArgument: CentralArgument,
        arguments: ((ArgumentInfo) -> List<BaseArgument>)? = null
    ) : super({ argInfo ->
        if (arguments != null) arguments(argInfo).toMutableList().also { it.add(centralArgument) }
        else listOf(centralArgument)
    })

    constructor(
        centralArgument: (ArgumentInfo) -> CentralArgument,
        arguments: ((ArgumentInfo) -> List<BaseArgument>)? = null
    ) : super({ argInfo ->
        if (arguments != null) arguments(argInfo).toMutableList().also { it.add(centralArgument(argInfo)) }
        else listOf(centralArgument(argInfo))
    })
}