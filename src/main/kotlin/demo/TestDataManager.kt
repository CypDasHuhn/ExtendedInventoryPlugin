package demo

import de.cypdashuhn.rooster.database.RoosterTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

data class TestData(val id: Int, val name: String, val color: String)

@RoosterTable
object TestDatas : IntIdTable() {
    val name = varchar("name", 255)
    val color = varchar("color", 255)
}

object TestDataManager {
    fun addTestData(testData: TestData) {
        transaction {
            TestDatas.insert {
                it[name] = testData.name
                it[color] = testData.color
            }
        }
    }

    fun testDataById(id: Int): TestData? {
        return transaction {
            val entry = TestDatas.selectAll().where { TestDatas.id eq id }.firstOrNull() ?: return@transaction null
            TestData(
                entry[TestDatas.id].value,
                entry[TestDatas.name],
                entry[TestDatas.color]
            )
        }
    }
}

