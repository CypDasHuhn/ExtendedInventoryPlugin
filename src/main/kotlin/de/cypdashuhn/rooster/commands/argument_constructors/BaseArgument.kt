package de.cypdashuhn.rooster.commands.argument_constructors

import de.cypdashuhn.rooster.localization.tSend
import org.bukkit.command.CommandSender

typealias PredicateToAction = (ArgumentInfo) -> Pair<Boolean, ((ArgumentInfo) -> Unit)?>
typealias InvokeLambda = (InvokeInfo) -> Unit

typealias ArgumentPredicate = (ArgumentInfo) -> Boolean
typealias CompletionLambda = (ArgumentInfo) -> List<String>
typealias ErrorLambda = (ArgumentInfo) -> Unit
typealias ArgumentHandler = (ArgumentInfo) -> Any

data class ArgumentInfo(
    val sender: CommandSender,
    val args: Array<String>,
    val arg: String,
    val index: Int,
    val values: HashMap<String, Any?>
)

data class InvokeInfo(
    val sender: CommandSender,
    val args: Array<String>,
    val values: HashMap<String, Any?>
)

val defaultTrue: ArgumentPredicate = { _ -> true }
fun returnString(): ArgumentHandler = { (_, _, str, _, _) -> str }

fun errorMessagePair(
    messageKey: String,
    argKey: String? = null
): Pair<Boolean, (ArgumentInfo) -> Unit> {
    return Pair(false, errorMessage(messageKey, argKey))
}

fun errorMessage(
    messageKey: String,
    argKey: String? = null
): (ArgumentInfo) -> Unit {
    return if (argKey != null) {
        { argInfo -> argInfo.sender.tSend(messageKey, argKey to argInfo.arg) }
    } else { argInfo -> argInfo.sender.tSend(messageKey) }
}

class TabCompleter(
    tabCompletions: CompletionLambda?
)

abstract class _BaseArgument(
    open var isArgument: ArgumentPredicate = defaultTrue,

    )

abstract class BaseArgument(
    open var isArgument: ArgumentPredicate = defaultTrue,
    open var tabCompletions: CompletionLambda? = null,
    open var isValid: (PredicateToAction)? = null,
    open var isValidCompleter: ArgumentPredicate? = null,
    open var invoke: InvokeLambda? = null,
    open var errorMissing: ErrorLambda? = null,
    open var errorArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
    open var followingArguments: ArgumentList? = null,
    open var errorMissingChildArg: ((ArgumentInfo) -> Unit)? = null,
    open var argumentHandler: ArgumentHandler = returnString(),
    open var isModifier: Boolean = false,
    open var key: String,
) {
    fun details(): ArgumentDetails {
        return when {
            invoke != null && followingArguments != null && errorMissingChildArg != null -> {
                ArgumentDetails(invoke!!, followingArguments!!, errorMissingChildArg!!)
            }

            invoke != null && followingArguments != null -> {
                ArgumentDetails(invoke!!, followingArguments as CentralizedArgumentList)
            }

            invoke != null -> {
                ArgumentDetails(invoke!!)
            }

            followingArguments != null && errorMissingChildArg != null -> {
                ArgumentDetails(followingArguments!!, errorMissingChildArg!!)
            }

            followingArguments != null -> {
                ArgumentDetails(followingArguments as CentralizedArgumentList)
            }

            else -> {
                error("Impossible Argument Structure")
            }
        }
    }
}

@Suppress("unused")
class UnsafeArgument(
    override var isArgument: ArgumentPredicate = defaultTrue,
    override var tabCompletions: CompletionLambda? = null,
    override var isValid: (PredicateToAction)? = null,
    override var isValidCompleter: ArgumentPredicate? = null,
    override var invoke: InvokeLambda? = null,
    override var errorMissing: ErrorLambda? = null,
    override var errorArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
    override var followingArguments: ArgumentList? = null,
    override var errorMissingChildArg: ((ArgumentInfo) -> Unit)? = null,
    override var argumentHandler: ArgumentHandler = returnString(),
    override var isModifier: Boolean = false,
    override var key: String,
) : BaseArgument(
    isArgument = isArgument,
    tabCompletions = tabCompletions,
    isValidCompleter = isValidCompleter,
    invoke = invoke,
    argumentHandler = argumentHandler,
    isValid = isValid,
    isModifier = isModifier,
    errorMissing = errorMissing,
    followingArguments = followingArguments,
    errorArgumentOverflow = errorArgumentOverflow,
    errorMissingChildArg = errorMissingChildArg,
    key = key
)