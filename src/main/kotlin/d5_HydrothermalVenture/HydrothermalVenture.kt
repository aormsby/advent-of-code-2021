package d5_HydrothermalVenture

import util.Coord
import util.Input
import util.Output
import kotlin.math.sign

fun main() {
    Output.day(5, "Hydrothermal Ventures")
    Output.setStartTime()

    val hv = HydrothermalVenture()

    Output.part(1, "Dangerous Areas (no diagonals)", hv.numDangerPoints())
    Output.part(2, "Dangerous Areas (diagonals)", hv.numDangerPointsWithDiags())

    Output.executionTime()
}

class HydrothermalVenture {
    val coordinatePairs = Input.parseToPairList<Coord, Coord>(
        "/input/d5_vent_map.txt",
        pairDelimiter = " -> ", itemDelimiter = ","
    )

    val ventMap = mutableMapOf<Coord, Int>()
    val diagMap = mutableListOf<Coord>()    // stores diagonals until part 2!

    fun numDangerPoints(): Int {
        coordinatePairs.forEach { cPair ->
            val line = drawLine(cPair.first, cPair.second)
            line.forEach { point -> updateVentMap(point) }
        }

        return ventMap.filter { it.value > 1 }.size
    }

    fun numDangerPointsWithDiags(): Int {
        diagMap.forEach { point ->
            updateVentMap(point)
        }

        return ventMap.filter { it.value > 1 }.size
    }

    fun drawLine(c1: Coord, c2: Coord): List<Coord> {
        val coordList = mutableListOf<Coord>()
        val slope = Coord(x = c2.x - c1.x, y = c2.y - c1.y).simplify()

        coordList.add(c1)
        val cur = Coord(x = c1.x, y = c1.y)

        while (cur != c2) {
            cur.x += slope.x
            cur.y += slope.y
            coordList.add(Coord(x = cur.x, y = cur.y))
        }

        // return empty list if diagonal, but store data for Part 1
        return if (slope.x != 0 && slope.y != 0) {
            diagMap.addAll(coordList)
            return listOf() // avoid adding to ventMap for part 1
        } else coordList
    }

    fun updateVentMap(p: Coord) {
        ventMap.merge(p, 1) { a, b -> a + b }
    }

    fun Coord.simplify(): Coord = Coord(x = this.x.sign, y = this.y.sign)
}