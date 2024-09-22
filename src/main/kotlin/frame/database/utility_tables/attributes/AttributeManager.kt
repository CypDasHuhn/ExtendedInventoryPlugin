package database.utility_tables.attributes

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import de.cypdashuhn.rooster.database.utility_tables.UtilityDatabase
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.transaction

abstract class AttributeManager<T : Any?> : UtilityDatabase() {
    internal abstract fun valueToQuery(value: T): Op<Boolean>
    internal abstract fun initializeNewField(row: InsertStatement<*>, value: T)

    private val gson = Gson()
    private val attributeKeyManager = AttributeKeyManager()

    override fun mainDatabase(): Table = getAttributesTable()
    val table by lazy { getAttributesTable() }

    internal abstract fun getAttributesTable(): Attributes
    internal abstract fun getEntityClass(): IntEntityClass<Attribute>

    // Concrete implementation of the Attributes table
    open class Attributes(tableName: String) : IntIdTable(tableName) {
        val attributeKey = reference("attribute_key", AttributeKeyManager.AttributeKeys, onDelete = ReferenceOption.CASCADE)
        val attributeValue = text("attribute_value")
    }

    open class Attribute(id: EntityID<Int>, attributeManager: AttributeManager<*>) : IntEntity(id) {
        var attributeKey by AttributeKeyManager.DbAttributeKey referencedOn attributeManager.getAttributesTable().attributeKey
        var attributeValue by attributeManager.getAttributesTable().attributeValue
    }

    fun <K: Any?> setAttribute(tableKey: T, key: AttributeKey<K>, value: K?) {
        val attributeKeyId = attributeKeyManager.getAttributeKey(key.key).id
        val jsonValue = gson.toJson(value)
        transaction {
            table.deleteWhere { valueToQuery(tableKey) and (table.attributeKey eq attributeKeyId) }

            table.insert {
                it[attributeKey] = attributeKeyId
                it[attributeValue] = jsonValue
                initializeNewField(it, tableKey)
            }
        }
    }

    fun <K: Any> getAttribute(tableKey: T, key: AttributeKey<K>): K {
        val attributeKeyId = attributeKeyManager.getAttributeKey(key.key).id

        return transaction {
            val dbEntry = table.selectAll().where { valueToQuery(tableKey) and (table.attributeKey eq attributeKeyId) }.firstOrNull()

            if (dbEntry != null) {
                try {
                    gson.fromJson(dbEntry[table.attributeValue], key.type) ?: key.default!!
                } catch (e: JsonSyntaxException) {
                    key.default!!
                }
            } else {
                key.default!!
            }
        }
    }
    fun <K: Any?> getAttributeNullable(tableKey: T, key: AttributeKey<K>): K? {
        val attributeKeyId = attributeKeyManager.getAttributeKey(key.key).id

        return transaction {
            val dbEntry = table.selectAll().where { valueToQuery(tableKey) and (table.attributeKey eq attributeKeyId) }.firstOrNull()

            if (dbEntry != null) {
                try {
                    gson.fromJson(dbEntry[table.attributeValue], key.type) ?: key.default
                } catch (e: JsonSyntaxException) {
                    key.default
                }
            } else {
                key.default
            }
        }
    }
}