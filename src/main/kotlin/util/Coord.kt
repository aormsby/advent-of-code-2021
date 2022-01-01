package util

import kotlin.math.pow
import kotlin.math.sqrt

data class Coord(
    var x: Int,
    var y: Int
) {
    operator fun plus(c: Coord) = Coord(x = x + c.x, y = y + c.y)
    operator fun minus(c: Coord) = Coord(x = x - c.x, y = y - c.y)

    /**
     * Get adjacent neighbor [Coord] list
     */
    fun neighbors(xLimit: Int = -1, yLimit: Int = -1): List<Coord> {
        var n = listOf(
            Coord(x, y + 1),
            Coord(x, y - 1),
            Coord(x + 1, y),
            Coord(x - 1, y)
        )

        if (xLimit > -1)
            n = n.filter { it.x in 0..xLimit }

        if (yLimit > -1)
            n = n.filter { it.y in 0..yLimit }

        return n
    }

    /**
     * Get adjacent and diagonal neighbor [Coord] list
     */
    fun allNeighbors(xLimit: Int = -1, yLimit: Int = -1): List<Coord> {
        var n = listOf(
            Coord(x - 1, y - 1),
            Coord(x - 1, y + 1),
            Coord(x + 1, y - 1),
            Coord(x + 1, y + 1)
        )

        if (xLimit > -1)
            n = n.filter { it.x in 0..xLimit }

        if (yLimit > -1)
            n = n.filter { it.y in 0..yLimit }

        return neighbors(xLimit, yLimit) + n
    }

    fun distanceTo(c: Coord): Float = sqrt(
        ((x - c.x).toFloat()).pow(2) + ((y - c.y).toFloat()).pow(2)
    )

    override fun toString(): String = "($x, $y)"
}

data class Coord3d(
    var x: Int,
    var y: Int,
    var z: Int
) {
    fun distanceTo(c: Coord3d): Float = sqrt(
        ((x - c.x).toFloat()).pow(2) + ((y - c.y).toFloat()).pow(2) + ((z - c.z).toFloat()).pow(2)
    )

    override fun toString(): String = "($x, $y, $z)"
}