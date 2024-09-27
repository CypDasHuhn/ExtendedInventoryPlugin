package de.cypdashuhn.rooster.commands

import de.cypdashuhn.rooster.Rooster.cache
import de.cypdashuhn.rooster.Rooster.registeredRootArguments
import de.cypdashuhn.rooster.commands.argument_constructors.ArgumentInfo
import de.cypdashuhn.rooster.commands.argument_constructors.BaseArgument
import de.cypdashuhn.rooster.commands.argument_constructors.InvokeInfo
import org.bukkit.command.CommandSender

object ArgumentParser {
    val defaultErrorArgumentsOverflow: (ArgumentInfo) -> Unit =
        { it.sender.sendMessage("Too many Arguments!") }

    class ReturnResult {
        var success: Boolean
        var tabCompleterList: List<String>
        var invocationLambda: () -> Unit
        var errorInvocation: () -> Unit

        constructor(success: Boolean, lambda: () -> Unit) {
            this.success = success
            this.tabCompleterList = ArrayList()
            this.invocationLambda = if (success) lambda else {
                {}
            }
            this.errorInvocation = if (!success) lambda else {
                {}
            }
        }

        constructor(value: List<String>) {
            this.success = true
            this.tabCompleterList = value
            this.invocationLambda = {}
            this.errorInvocation = {}
        }

        constructor() {
            this.success = false
            this.tabCompleterList = ArrayList()
            this.invocationLambda = {}
            this.errorInvocation = {}
        }
    }

    enum class CommandParseType {
        TabCompleter,
        Invocation
    }

    const val CACHE_KEY = "rooster_arguments_cache"

    private fun Array<String>.withoutLast(): Array<String> {
        return this.copyOfRange(0, this.size - 1)
    }

    data class CacheInfo(
        val stringArguments: Array<String>,
        val arguments: MutableList<BaseArgument>,
        val headArgument: BaseArgument,
        val errorArgumentOverflow: ((ArgumentInfo) -> Unit)?
    )

    fun parse(
        sender: CommandSender,
        label: String,
        rawStringArguments: Array<String>,
        commandParseType: CommandParseType
    ): ReturnResult {
        val errorWithoutInfo = ReturnResult()

        val topArgument = requireNotNull(
            registeredRootArguments.firstOrNull { it.label.lowercase() == label.lowercase() }
        ) { "Root must be found, else command invocation wouldn't be possible" }

        topArgument.startingUnit?.let {
            val continueArgument = it(sender)
            if (!continueArgument) return errorWithoutInfo
        }
        val stringArguments = rawStringArguments.toMutableList().also { it.add(0, label) }.toTypedArray()

        @Suppress("UNCHECKED_CAST")
        var arguments = mutableListOf(topArgument) as MutableList<BaseArgument>
        var headArgument = topArgument as BaseArgument
        var errorArgumentOverflow = topArgument.errorArgumentOverflow

        var cachePosition: Int? = null
        val cacheInfo = cache.getIfPresent(CACHE_KEY, sender) as CacheInfo?

        if (cacheInfo != null && commandParseType == CommandParseType.TabCompleter) {
            if (cacheInfo.stringArguments.withoutLast()
                contentEquals
                stringArguments.withoutLast() ||
                cacheInfo.stringArguments
                contentEquals
                stringArguments.withoutLast()
            ) {
                arguments = cacheInfo.arguments
                headArgument = cacheInfo.headArgument
                errorArgumentOverflow = cacheInfo.errorArgumentOverflow

                cachePosition = when {
                    stringArguments.last().isBlank() -> cacheInfo.stringArguments.size - 1
                    else -> cacheInfo.stringArguments.size - 1
                }
            }
        }

        val values: HashMap<String, Any?> = HashMap()

        for ((index, stringArgument) in stringArguments.withIndex()) {
            if (cachePosition != null && cachePosition > index) {
                continue
            }

            val argumentInfo = ArgumentInfo(sender, stringArguments, stringArgument, index, values)
            val cacheInfo = CacheInfo(stringArguments, arguments, headArgument, errorArgumentOverflow)

            val currentTabCompletions: () -> List<String> = {
                arguments
                    .filter { (it.isValidCompleter == null || it.isValidCompleter!!(argumentInfo)) && it.tabCompletions != null }
                    .flatMap { it.tabCompletions!!(argumentInfo) }
            }

            val currentArgument = arguments.firstOrNull { arg -> arg.isArgument(argumentInfo) }
                ?: when (commandParseType) { // Null Handling
                    CommandParseType.TabCompleter -> {
                        cacheCommand(sender, cacheInfo)

                        return ReturnResult(currentTabCompletions())
                    }

                    CommandParseType.Invocation -> {
                        arguments.forEach { arg ->
                            arg.errorMissing?.let {
                                return ReturnResult(success = false) { it(argumentInfo) }
                            }
                        }
                        requireNotNull(headArgument.errorMissingChildArg) { "Head Argument error passed. If head doesn't have message, Argument structure is wrong." }

                        return ReturnResult(success = false) { headArgument.errorMissingChildArg!!.invoke(argumentInfo) }
                    }
                }

            currentArgument.errorArgumentOverflow?.let { errorArgumentOverflow = it }

            if (commandParseType == CommandParseType.Invocation) {
                if (currentArgument.isValid != null) {
                    val (isValid, invalidAction) = currentArgument.isValid!!(argumentInfo)
                    if (!isValid) {
                        return ReturnResult(success = false) { invalidAction?.invoke(argumentInfo) }
                    }
                }
                values[currentArgument.key] = currentArgument.argumentHandler(argumentInfo)
            }


            arguments // Each modifier not mentioned is set to false
                .filter { it.isModifier }
                .forEach { values.putIfAbsent(it.key, false) }

            val isLastElement = index == stringArguments.size - 1

            if (!isLastElement) {
                if (commandParseType == CommandParseType.TabCompleter) {
                    if (currentArgument.isValid != null) {
                        val (isValid, invalidAction) = currentArgument.isValid!!(argumentInfo)
                        if (!isValid) {
                            return ReturnResult(success = false) { invalidAction?.invoke(argumentInfo) }
                        }
                    }
                    values[currentArgument.key] = currentArgument.argumentHandler(argumentInfo)
                }

                if (currentArgument.isModifier) {
                    // step further (stay but without this one)
                    arguments.remove(currentArgument)
                } else if (currentArgument.followingArguments != null) {
                    // step further
                    arguments = currentArgument.followingArguments!!.arguments(argumentInfo).toMutableList()
                    headArgument = currentArgument
                } else {
                    // to many arguments
                    return ReturnResult(success = false) {
                        errorArgumentOverflow?.let { it(argumentInfo) }
                            ?: defaultErrorArgumentsOverflow(argumentInfo)
                    }
                }
                continue
            }

            // Only invoked if it's the last element
            cacheCommand(sender, cacheInfo)

            when (commandParseType) {
                CommandParseType.TabCompleter -> {
                    return ReturnResult(currentTabCompletions())
                }

                CommandParseType.Invocation -> {
                    val comparativeArgument = when (currentArgument.isModifier) {
                        true -> headArgument
                        false -> currentArgument
                    }
                    if (comparativeArgument.invoke != null) {
                        return ReturnResult(success = true) {
                            comparativeArgument.invoke!!(InvokeInfo(sender, stringArguments, values))
                        }
                    }

                    // error handling

                    requireNotNull(comparativeArgument.followingArguments) {
                        "Current argument needs to have following arguments if invoke doesn't exist"
                    }

                    headArgument.errorMissingChildArg?.let {
                        return ReturnResult(success = false) {
                            it(argumentInfo)
                        }
                    }

                    val inferiorArguments = when (comparativeArgument.isModifier) {
                        true -> arguments.also { it.remove(comparativeArgument) }
                        false -> comparativeArgument.followingArguments!!.arguments(argumentInfo)
                    }

                    val firstArgumentWithError = inferiorArguments.firstOrNull { it.errorMissingChildArg != null }
                    requireNotNull(firstArgumentWithError) {
                        "At least one Argument should have an error Message, else the Parent would need to have one registered"
                    }

                    return ReturnResult(success = false) {
                        firstArgumentWithError.errorMissingChildArg!!(argumentInfo)
                    }

                }
            }
        }
        return errorWithoutInfo
    }

    private fun cacheCommand(sender: CommandSender, cacheInfo: CacheInfo) {
        cache.put(
            CACHE_KEY,
            sender,
            cacheInfo,
        )
    }
}