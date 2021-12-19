package d15_Chiton

import util.AStarNode
import util.Coord
import util.Input
import util.Output

lateinit var chitonNodeMap: MutableList<MutableList<AStarNode>>
lateinit var mapEdges: Coord

val moveOptions = listOf(
    Coord(x = 0, y = 1),
    Coord(x = 0, y = -1),
    Coord(x = 1, y = 0),
    Coord(x = -1, y = 0)
)

fun main() {
    Output.day(13, "Chiton")
    val startTime = Output.startTime()

    chitonNodeMap = Input.parseTo2dList(filename = "/input/d15_chiton_risk_level.txt")

    val startNode = chitonNodeMap.first().first().apply { gValue = 0 }
    val goalNode = chitonNodeMap.last().last()
    mapEdges = goalNode.position

    chitonNodeMap.forEach { line ->
        line.forEach { node ->
            node.hValue = (mapEdges - node.position).combine()
        }
    }

    val openNodes = mutableListOf(startNode)
    val closedNodes = mutableListOf<AStarNode>()
    var nextNode = openNodes.first()

    // F = G + H
    // G = movement cost from the start node to the current square (so cost + parent G)
    // H = estimated movement cost from the current node to the goal node (already calculated)

    while (nextNode != goalNode) {
        val curNode = nextNode
        openNodes.remove(curNode)
        closedNodes.add(curNode)

        val validNeighbors = curNode.getNeighbors().filterNot { it in closedNodes }

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

    var lowestPathRisk = 0
    var s = ""
    // reverse from end node back to start node
    while (nextNode != startNode) {
        lowestPathRisk += nextNode.cost
        s += nextNode.cost.toString()
        nextNode = chitonNodeMap[nextNode.parent!!]
    }

//    println("1121365111511323211")
//    println(s)
//    println(lowestPathRisk)

    Output.part(1, "Lowest Path Total Risk", lowestPathRisk)
    Output.part(2, "n/a", "n/a")
    Output.executionTime(startTime)
}

fun AStarNode.getNeighbors(): List<AStarNode> =
    moveOptions.map { it + position }
        .filter { it.x > -1 && it.x <= mapEdges.x && it.y > -1 && it.y <= mapEdges.y }
        .map { chitonNodeMap[it] }

operator fun MutableList<MutableList<AStarNode>>.get(c: Coord) = this[c.x][c.y]
fun Coord.combine() = x + y