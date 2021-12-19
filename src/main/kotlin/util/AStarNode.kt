package util

data class AStarNode(
    val position: Coord,
    val cost: Int,
    var gValue: Int? = null,
    var hValue: Int? = null,
    var fScore: Int? = null,
    var parent: Coord? = null
)
