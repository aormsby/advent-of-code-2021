package d15_Chiton

import util.AStarNode
import util.Coord
import util.Input
import util.Output

val moveOptions = listOf(
    Coord(x = 0, y = 1),
    Coord(x = 0, y = -1),
    Coord(x = 1, y = 0),
    Coord(x = -1, y = 0)
)

fun main() {
    Output.day(13, "Chiton")
    val startTime = Output.startTime()

    val chitonMapSmall = Input.parseTo2dList<AStarNode>(filename = "/input/d15_chiton_risk_level.txt")

    val lowestPathRisk = findLowestPathRisk(
        chitonMapSmall,
        chitonMapSmall.first().first().apply { gValue = 0 },
        chitonMapSmall.last().last()
    )

    Output.part(1, "Lowest Path Total Risk", lowestPathRisk)
    Output.part(2, "n/a", "n/a")
    Output.executionTime(startTime)
}

// A* notes...
// F = G + H
// G = movement cost from the start node to the current square (so cost + parent G)
// H = estimated movement cost from the current node to the goal node (already calculated)

fun findLowestPathRisk(
    searchMap: MutableList<MutableList<AStarNode>>,
    start: AStarNode,
    goal: AStarNode
): Int {
    val mapEdges = goal.position

    searchMap.forEach { line ->
        line.forEach { node ->
            node.hValue = (searchMap.last().last().position - node.position).combine()
        }
    }

    val openNodes = mutableListOf(start)
    val closedNodes = mutableListOf<AStarNode>()
    var nextNode = openNodes.first()

    while (nextNode != goal) {
        val curNode = nextNode
        openNodes.remove(curNode)
        closedNodes.add(curNode)

        val validNeighbors = curNode.getNeighbors(searchMap, mapEdges).filterNot { it in closedNodes }

        validNeighbors.apply {
            forEach { node ->
                val newGValue = node.cost + curNode.gValue!!
                val newFScore = newGValue + node.hValue!!

                openNodes.find { n -> n == node }?.let {
                    if (newFScore < it.fScore!!) {
                        it.parent = curNode.position
                        it.gValue = newGValue
                        it.fScore = newFScore
                    }
                } ?: run {
                    node.parent = curNode.position
                    node.gValue = newGValue
                    node.fScore = newFScore
                    openNodes.add(node)
                }
            }

            openNodes.sortByDescending { it.fScore }
            nextNode = openNodes.last()
        }
    }

    return nextNode.gValue!!
}

fun AStarNode.getNeighbors(map: MutableList<MutableList<AStarNode>>, edges: Coord): List<AStarNode> =
    moveOptions.map { it + position }
        .filter { it.x > -1 && it.x <= edges.x && it.y > -1 && it.y <= edges.y }
        .map { map[it] }

operator fun MutableList<MutableList<AStarNode>>.get(c: Coord) = this[c.x][c.y]
fun Coord.combine() = x + y