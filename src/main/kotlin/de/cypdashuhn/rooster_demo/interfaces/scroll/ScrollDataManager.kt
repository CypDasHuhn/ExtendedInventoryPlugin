package de.cypdashuhn.rooster_demo.interfaces.scroll

import de.cypdashuhn.rooster_demo.interfaces.DemoManager
import de.cypdashuhn.rooster_demo.interfaces.RoosterDemoManager
import de.cypdashuhn.rooster_demo.interfaces.RoosterDemoTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

data class ScrollData(val id: Int, val name: String, val color: String)

@RoosterDemoManager
object ScrollDataManager : DemoManager() {
    @RoosterDemoTable
    object ScrollTable : IntIdTable("RoosterTestScrollData") {
        val name = varchar("name", 255)
        val color = varchar("color", 255)
    }

    fun addTestData(scrollData: ScrollData) {
        transaction {
            ScrollTable.insert {
                it[name] = scrollData.name
                it[color] = scrollData.color
            }
        }
    }

    fun testDataById(id: Int): ScrollData? {
        return transaction {
            val entry = ScrollTable.selectAll().where { ScrollTable.id eq id }.firstOrNull() ?: return@transaction null
            ScrollData(
                entry[ScrollTable.id].value,
                entry[ScrollTable.name],
                entry[ScrollTable.color]
            )
        }
    }

    override fun demoPrep() {
        if (ScrollTable.selectAll().count().toInt() > 0) return

        val possibleColors = listOf("red", "green", "blue", "yellow", "lime", "purple")
        repeat(50) {
            val randomColor = possibleColors.random()

            addTestData(ScrollData(0, "testName$it", randomColor))
        }
    }
}

