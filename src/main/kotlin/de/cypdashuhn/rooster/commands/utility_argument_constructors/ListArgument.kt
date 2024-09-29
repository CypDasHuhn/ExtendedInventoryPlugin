package de.cypdashuhn.rooster.commands.utility_argument_constructors

import de.cypdashuhn.rooster.commands.argument_constructors.*
import de.cypdashuhn.rooster.core.Rooster.cache
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.reflect.KClass

@Suppress("unused")
object ListArgument {
    fun list(
        list: List<String>,
        ignoreCase: Boolean = false,
        key: String,
        errorInvalid: ((ArgumentInfo) -> Unit),
        errorMissing: ((ArgumentInfo) -> Unit),
        argumentDetails: ArgumentDetails,
        isArgument: ArgumentPredicate = defaultTrue,
        isValidCompleter: ArgumentPredicate? = null,
        errorArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        argumentHandler: ArgumentHandler = returnString(),
    ): CentralArgument {
        return CentralArgument(
            isArgument = isArgument,
            isValidCompleter = isValidCompleter,
            argumentHandler = argumentHandler,
            errorArgumentOverflow = errorArgumentOverflow,
            isValid = { argInfo ->
                if (
                    (ignoreCase && list.map { it.lowercase() }.contains(argInfo.arg.lowercase()))
                    || list.contains(argInfo.arg)
                ) {
                    Pair(true) {}
                } else {
                    Pair(false, errorInvalid)
                }
            },
            tabCompletions = { list },
            key = key,
            errorMissing = errorMissing,
            argumentDetails = argumentDetails
        )
    }

    fun list(
        list: List<String>,
        ignoreCase: Boolean = false,
        key: String,
        errorInvalidMessageKey: String,
        argKey: String = "arg",
        errorMissingMessageKey: String,
        argumentDetails: ArgumentDetails,
        isArgument: ArgumentPredicate = defaultTrue,
        isValidCompleter: ArgumentPredicate? = null,
        errorArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        argumentHandler: ArgumentHandler = returnString(),
    ): CentralArgument {
        return CentralArgument(
            isArgument = isArgument,
            isValidCompleter = isValidCompleter,
            argumentHandler = argumentHandler,
            errorArgumentOverflow = errorArgumentOverflow,
            isValid = { argInfo ->
                if (
                    (ignoreCase && list.map { it.lowercase() }.contains(argInfo.arg.lowercase()))
                    || list.contains(argInfo.arg)
                ) {
                    Pair(true) {}
                } else {
                    errorMessagePair(errorInvalidMessageKey, argKey)
                }
            },
            tabCompletions = { list },
            key = key,
            errorMissing = errorMessage(errorMissingMessageKey),
            argumentDetails = argumentDetails
        )
    }

    data class DBCacheInfo(
        val query: Query,
        val arg: String
    )

    private const val LIST_FILTERED_CACHE_KEY = "rooster_list_cache_filtered"
    private const val LIST_CACHE_KEY = "rooster_list_cache_filtered"

    fun <E : IntEntity> dbList(
        entity: IntEntityClass<E>,
        displayField: Column<String>,
        filter: ((ArgumentInfo, E) -> Boolean)? = null,
        ignoreCase: Boolean = false,
        key: String,
        errorInvalidMessageKey: String,
        argKey: String = "arg",
        errorMissingMessageKey: String,
        argumentDetails: ArgumentDetails,
        isArgument: ArgumentPredicate = defaultTrue,
        isValidCompleter: ArgumentPredicate? = null,
        errorArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
        argumentHandler: ArgumentHandler = returnString(),
    ): CentralArgument {
        return CentralArgument(
            isArgument = isArgument,
            isValidCompleter = isValidCompleter,
            argumentHandler = argumentHandler,
            errorArgumentOverflow = errorArgumentOverflow,
            isValid = { (sender, args, arg, index, values) ->
                val matchingEntries = transaction {
                    val condition = if (ignoreCase) displayField.lowerCase() eq arg.lowercase()
                    else displayField eq arg

                    val cacheInfo = cache.getIfPresent(LIST_FILTERED_CACHE_KEY, sender) as DBCacheInfo?
                    val query = if (cacheInfo != null) {
                        if (arg.startsWith(cacheInfo.arg)) {
                            cacheInfo.query
                        } else entity.table.selectAll()
                    } else entity.table.selectAll()

                    val entries = query.where { condition }

                    cache.put(LIST_FILTERED_CACHE_KEY, sender, DBCacheInfo(entries, arg), 5 * 1000)

                    entity.wrapRows(entries)
                }

                val entries = if (filter != null) matchingEntries.filter {
                    filter(
                        ArgumentInfo(sender, args, arg, index, values),
                        it
                    )
                }
                else matchingEntries
                when {
                    entries.firstOrNull() == null -> errorMessagePair(errorInvalidMessageKey, argKey)
                    else -> Pair(true) {}
                }
            },
            tabCompletions = { argInfo ->
                val entries = cache.get(
                    LIST_CACHE_KEY,
                    argInfo.sender,
                    { transaction { entity.wrapRows(entity.table.selectAll()) } },
                    5 * 1000
                )
                val matchingEntries = if (filter != null) entries.filter { filter(argInfo, it) } else entries
                matchingEntries.map { it.readValues[displayField] }
            },
            key = key,
            errorMissing = errorMessage(errorMissingMessageKey),
            argumentDetails = argumentDetails
        )
    }

    fun <E : Enum<E>> enum(
        enumClass: KClass<E>,
        ignoreCase: Boolean = false,
        key: String,
        errorInvalid: (ArgumentInfo) -> Unit,
        errorMissing: (ArgumentInfo) -> Unit,
        argumentDetails: ArgumentDetails,
        isArgument: ArgumentPredicate = defaultTrue,
        isValidCompleter: ArgumentPredicate? = null,
        errorArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
    ): CentralArgument {
        val enumValues = enumClass.java.enumConstants.map { it.name }

        return CentralArgument(
            isArgument = isArgument,
            isValidCompleter = isValidCompleter,
            argumentHandler = { argInfo ->
                enumClass.java.enumConstants.first {
                    if (ignoreCase) it.name.equals(argInfo.arg, ignoreCase = true)
                    else it.name == argInfo.arg
                }
            },
            errorArgumentOverflow = errorArgumentOverflow,
            isValid = { argInfo ->
                if (
                    (ignoreCase && enumValues.map { it.lowercase() }.contains(argInfo.arg.lowercase()))
                    || enumValues.contains(argInfo.arg)
                ) {
                    Pair(true) {}
                } else {
                    Pair(false, errorInvalid)
                }
            },
            tabCompletions = { enumValues },
            key = key,
            errorMissing = errorMissing,
            argumentDetails = argumentDetails
        )
    }

    fun <E : Enum<E>> enum(
        enumClass: KClass<E>,
        ignoreCase: Boolean = false,
        key: String,
        errorInvalidMessageKey: String,
        argKey: String = "arg",
        errorMissingMessageKey: String,
        argumentDetails: ArgumentDetails,
        isArgument: ArgumentPredicate = defaultTrue,
        isValidCompleter: ArgumentPredicate? = null,
        errorArgumentOverflow: ((ArgumentInfo) -> Unit)? = null,
    ): CentralArgument {
        val enumValues = enumClass.java.enumConstants.map { it.name }

        return CentralArgument(
            isArgument = isArgument,
            isValidCompleter = isValidCompleter,
            argumentHandler = { argInfo ->
                enumClass.java.enumConstants.first {
                    if (ignoreCase) it.name.equals(argInfo.arg, ignoreCase = true)
                    else it.name == argInfo.arg
                }
            },
            errorArgumentOverflow = errorArgumentOverflow,
            isValid = { argInfo ->
                if (
                    (ignoreCase && enumValues.map { it.lowercase() }.contains(argInfo.arg.lowercase()))
                    || enumValues.contains(argInfo.arg)
                ) {
                    Pair(true) {}
                } else {
                    errorMessagePair(errorInvalidMessageKey, argKey)
                }
            },
            tabCompletions = { enumValues },
            key = key,
            errorMissing = errorMessage(errorMissingMessageKey),
            argumentDetails = argumentDetails
        )
    }
}