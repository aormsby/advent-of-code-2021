package d9_SmokeBasin

import util.Coord
import util.Input
import util.Output

val caveTopo = Input.parseLines(filename = "/input/d9_caves_topo_map.txt").map {
    Input.parseToListOf<Int>(rawData = it)
}

// topo map bounds and helper
val numCol = caveTopo[0].size
val numRow = caveTopo.size
val adjInd = Pair(-1, 1)

// for part 2
val checkedCoords = mutableListOf<Coord>()

fun main() {
    Output.day(9, "Smoke Basin")
    Output.setStartTime()

    // Part 1
    val lowPointsMap = mutableMapOf<Coord, Int>()

    caveTopo.forEachIndexed { r, rowList ->
        rowList.forEachIndexed { c, colVal ->
            val horiz = adjInd.clampToPos(c, numCol)
            val vert = adjInd.clampToPos(r, numRow)

            if (isLowPoint(r, c, horiz, vert))
                lowPointsMap[Coord(x = r, y = c)] = colVal
        }
    }

    Output.part(1, "Sum of Low Point Risk Levels", lowPointsMap.values.sumOf { it + 1 })

    // Part 2
    val basinMap = lowPointsMap.keys.associateWith { 1 } as MutableMap

    // expand outward from all lowest points and count number of non-9 spaces
    basinMap.keys.forEach { point ->
        checkedCoords.add(point)
        basinMap[point] = point.expand()
        checkedCoords.clear()
    }

    Output.part(2, "Sum of Basin Sizes",
        basinMap.values.sorted().takeLast(3).reduce { acc, n -> acc * n })
    Output.executionTime()
}

/**
 * Checks adjacent coordinates to find out if provided point is the lowest
 */
fun isLowPoint(r: Int, c: Int, h: Pair<Int, Int>, v: Pair<Int, Int>): Boolean {
    val heights = mutableListOf(
        caveTopo[r][c]
    )

    if (r + v.first != r)
        heights.add(caveTopo[r + v.first][c])

    if (r + v.second != r)
        heights.add(caveTopo[r + v.second][c])

    if (c + h.first != c)
        heights.add(caveTopo[r][c + h.first])

    if (c + h.second != c)
        heights.add(caveTopo[r][c + h.second])

    val min = heights.minOrNull()
    return heights.count { it == min } == 1 && heights[0] == min
}

/**
 * Clamps adjacent coordinates to bounds of 2d list
 */
fun Pair<Int, Int>.clampToPos(i: Int, len: Int): Pair<Int, Int> =
    when (i) {
        0 -> Pair(0, this.second)
        len - 1 -> Pair(this.first, 0)
        else -> this
    }

/**
 * Returns size of expanded basin
 */
fun Coord.expand(): Int {
    val v = adjInd.clampToPos(this.x, numRow)
    val h = adjInd.clampToPos(this.y, numCol)

    val listAdj = mutableListOf<Coord>()
    var count = 0

    if (this.x + v.first != this.x)
        listAdj.add(Coord(x = this.x + v.first, y = this.y))

    if (this.x + v.second != this.x)
        listAdj.add(Coord(x = this.x + v.second, y = this.y))

    if (this.y + h.first != this.y)
        listAdj.add(Coord(x = this.x, y = this.y + h.first))

    if (this.y + h.second != this.y)
        listAdj.add(Coord(x = this.x, y = this.y + h.second))

    val next = listAdj.filter { caveTopo[it.x][it.y] != 9 && it !in checkedCoords }
    checkedCoords.addAll(next)

    next.forEach {
        count += it.expand()
    }

    return count + 1
}
