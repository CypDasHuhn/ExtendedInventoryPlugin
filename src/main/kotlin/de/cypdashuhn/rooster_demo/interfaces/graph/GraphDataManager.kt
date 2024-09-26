package de.cypdashuhn.rooster_demo.interfaces.graph

import de.cypdashuhn.rooster_demo.interfaces.DemoManager
import de.cypdashuhn.rooster_demo.interfaces.RoosterDemoManager
import de.cypdashuhn.rooster_demo.interfaces.RoosterDemoTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

data class GraphData(val position: Pair<Int, Int>, val name: String, val color: String)

@RoosterDemoManager
object GraphDataManager : DemoManager() {

    @RoosterDemoTable
    object GraphTable : IntIdTable("RoosterTestGraphData") {
        val name = varchar("name", 255)
        val color = varchar("color", 255)
        val x = integer("x")
        val y = integer("y")
    }

    fun addTestData(graphData: GraphData) {
        transaction {
            GraphTable.insert {
                it[name] = graphData.name
                it[color] = graphData.color
                it[x] = graphData.position.first
                it[y] = graphData.position.second
            }
        }
    }

    fun getByCoord(coord: Pair<Int, Int>): GraphData? {
        return transaction {
            var entry = GraphTable
                .selectAll().where { (GraphTable.x eq coord.first) and (GraphTable.y eq coord.second) }
                .firstOrNull() ?: return@transaction null

            GraphData(
                Pair(entry[GraphTable.x], entry[GraphTable.y]),
                entry[GraphTable.name],
                entry[GraphTable.color]
            )
        }
    }

    override fun demoPrep() {
        if (GraphTable.selectAll().count().toInt() > 0) return

        repeat(75) {
            val randomColor = listOf("red", "green", "blue", "yellow", "lime", "purple").random()
            val randomX = (-25..25).random()
            val randomY = (-25..25).random()
            addTestData(GraphData(Pair(randomX, randomY), "testName$it", randomColor))
        }
    }
}