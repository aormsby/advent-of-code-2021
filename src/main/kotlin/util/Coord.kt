package util

data class Coord(
    var x: Int,
    var y: Int
) {
    operator fun plus(c: Coord) = Coord(x = x + c.x, y = y + c.y)
    operator fun minus(c: Coord) = Coord(x = x - c.x, y = y - c.y)
}
