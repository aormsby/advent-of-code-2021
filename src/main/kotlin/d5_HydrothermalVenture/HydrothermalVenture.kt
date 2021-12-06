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
    hv.clearMap()   // reset for Part 2
    Output.part(2, "Dangerous Areas (diagonals)", hv.numDangerPoints(diags = true))

    Output.executionTime()
}

class HydrothermalVenture {
    val coordinatePairs = Input.parseToPairList<Coord, Coord>(
        "/input/d5_vent_map.txt",
        pairDelimiter = " -> ", itemDelimiter = ","
    )

    val ventMap = mutableMapOf<Coord, Int>()

    // reset map between parts
    fun clearMap() = ventMap.clear()

    fun numDangerPoints(diags: Boolean = false): Int {
        coordinatePairs.forEach { cPair ->
            val line = drawLine(cPair.first, cPair.second, diags)
            line.forEach { point ->
                // increments value at this point
                ventMap.merge(point, 1) { a, b -> a + b }
            }
        }

        return ventMap.filter { it.value > 1 }.size
    }

    fun drawLine(c1: Coord, c2: Coord, diags: Boolean): List<Coord> {
        val coordList = mutableListOf<Coord>()
        val slope = Coord(x = c2.x - c1.x, y = c2.y - c1.y).simplify()

        // return empty list if skipping diagonals
        if (!diags && slope.x != 0 && slope.y != 0)
            return listOf()

        coordList.add(c1)
        val cur = Coord(x = c1.x, y = c1.y)

        while (cur != c2) {
            cur.x += slope.x
            cur.y += slope.y
            coordList.add(Coord(x = cur.x, y = cur.y))
        }

        return coordList
    }

    fun Coord.simplify(): Coord = Coord(x = 1 * this.x.sign, y = 1 * this.y.sign)
}