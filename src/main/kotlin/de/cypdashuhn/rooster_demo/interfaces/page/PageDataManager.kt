package de.cypdashuhn.rooster_demo.interfaces.page

import de.cypdashuhn.rooster.database.RoosterTable
import de.cypdashuhn.rooster_demo.interfaces.DemoDatabase
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

data class PageData(val name: String, val color: String, val page: Int, val pos: Pair<Int, Int>)
object PageDataManager : DemoDatabase() {

    @RoosterTable
    object PageTable : IntIdTable("RoosterTestPageData") {
        val name = varchar("name", 255)
        val color = varchar("color", 255)
        val page = integer("page")
        val x = integer("x")
        val y = integer("y")
    }

    fun getById(page: Int, x: Int, y: Int): PageData? {
        return transaction {
            PageTable
                .selectAll()
                .where { (PageTable.page eq page) and (PageTable.x eq x) and (PageTable.y eq y) }
                .firstOrNull()
                ?.let {
                    PageData(
                        it[PageTable.name],
                        it[PageTable.color],
                        it[PageTable.page],
                        Pair(it[PageTable.x], it[PageTable.y])
                    )
                }
        }
    }

    fun addTestData(pageData: PageData) {
        transaction {
            PageTable.insert {
                it[name] = pageData.name
                it[color] = pageData.color
                it[page] = pageData.page
                it[x] = pageData.pos.first
                it[y] = pageData.pos.second
            }
        }
    }

    override fun demoPrep() {
        repeat(50) {
            val randomColor = listOf("red", "green", "blue", "yellow", "lime", "purple").random()
            val randomPage = (1..5).random()
            val randomX = (0..8).random()
            val randomY = (-0..4).random()
            addTestData(PageData("testName$it", randomColor, randomPage, randomX to randomY))
        }
    }

}