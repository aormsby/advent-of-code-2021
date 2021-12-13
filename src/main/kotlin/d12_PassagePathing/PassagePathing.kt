package d12_PassagePathing

import util.Input
import util.Output

val upperAlpha = "A".."Z"
val lowerAlpha = "a".."z"

fun main() {
    Output.day(11, "Passage Pathing")
    val startTime = Output.startTime()

    val input = Input.parseTo2dList<String>(filename = "/input/d12_path_map.txt", delimiter = "-")

    val caveGraph = mutableMapOf<String, List<String>>()
    input.forEach {
        if (it[1] != "start")
            caveGraph.merge(it[0], listOf(it[1])) { a, b -> a + b }

        if (it[0] != "start")
            caveGraph.merge(it[1], listOf(it[0])) { a, b -> a + b }
    }

    Output.part(1, "Number of Paths", findAllPaths(caveGraph, "start").size)
    Output.part(2, "Number of Longer Paths", findAllPaths(caveGraph, "start", smallCaveMax = 2).size)
    Output.executionTime(startTime)
}

fun findAllPaths(
    graph: Map<String, List<String>>,
    node: String,
    curPath: MutableList<String> = mutableListOf(),
    smallCaveMax: Int = 1
): List<List<String>> {
    // for returning paths in a list of path lists - yay recursion!
    val successPaths = mutableListOf<List<String>>()

    // add node to path immediately on traversal
    curPath.add(node)

    // if at 'end', return the current tracked path
    if (node == "end") {
        successPaths.add(curPath)
        return successPaths
    } else {
        // only traverse through Large nodes or not-traversed small nodes
        graph[node]!!.filter {
            it in upperAlpha || it !in curPath ||
                    (it in lowerAlpha && smallCaveMax > 1 && curPath.roomForOneMore())
        }.forEach {
            successPaths.addAll(findAllPaths(graph, it, curPath.toMutableList(), smallCaveMax))
        }
    }

    // nothing left to traverse, don't return path because not at 'end'
    return successPaths
}

fun MutableList<String>.roomForOneMore(): Boolean {
    val smalls = this.filter { it in lowerAlpha }
    return smalls == smalls.distinct()
}