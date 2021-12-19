package d15_Chiton

import util.Coord
import util.Input
import util.Output
import java.util.*

val moveOptions = listOf(
    Coord(x = 0, y = 1),
    Coord(x = 0, y = -1),
    Coord(x = 1, y = 0),
    Coord(x = -1, y = 0)
)

fun main() {
    Output.day(15, "Chiton")
    val startTime = Output.startTime()

    val chitonMap = Input.parseTo2dList<Int>(filename = "/input/d15_chiton_risk_level.txt")

    val safestPath = findSafestPath(
        chitonMap,
        Coord(0, 0),
        Coord(chitonMap.lastIndex, chitonMap.first().lastIndex)
    )
    Output.part(1, "Lowest Path Total Risk", safestPath)

    val bigSafestPath = findSafestPath(
        chitonMap,
        Coord(0, 0),
        Coord((chitonMap.size * 5) - 1, (chitonMap.first().size * 5) - 1)
    )
    Output.part(2, "Lowest Path Total Risk (Large Map)", bigSafestPath)

    Output.executionTime(startTime)
}

class Node(val coord: Coord, val totalRisk: Int) : Comparable<Node> {
    override fun compareTo(other: Node): Int =
        this.totalRisk - other.totalRisk
}

fun findSafestPath(
    curMap: MutableList<MutableList<Int>>,
    start: Coord,
    goal: Coord
): Int {
    val openNodes = PriorityQueue(listOf(Node(start, 0)))
    val closedNodes = mutableSetOf<Coord>()

    while (openNodes.isNotEmpty()) {
        val curNode = openNodes.poll()

        if (curNode.coord == goal) {
            return curNode.totalRisk
        }

        if (curNode.coord !in closedNodes) {
            closedNodes.add(curNode.coord)
            curNode.coord
                .neighbors(xLimit = goal.x, yLimit = goal.y)
                .forEach { openNodes.offer(Node(it, curNode.totalRisk + curMap[it])) }
        }
    }

    // open nodes list is empty
    error("No path found!")
}

operator fun MutableList<MutableList<Int>>.get(coord: Coord): Int {
    // the risk increase by 1 for each new map tile in either direction
    val xRiskIncrease = coord.x / this.first().size
    val yRiskIncrease = coord.y / this.size

    val newRisk = this[coord.x % this.size][coord.y % this.first().size] +
            xRiskIncrease + yRiskIncrease
    return newRisk.takeIf { it < 10 } ?: (newRisk - 9)
}