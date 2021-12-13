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
    caveGraph["end"] = listOf()

    val completePaths = mutableListOf<List<String>>()
    val curPath = mutableListOf<String>()

    val toDoQ = mutableListOf(Pair("start", 0))
    lateinit var curNode: Pair<String, Int>
    var justPopped = false

    while (toDoQ.size > 0) {
        // set current node to end of queue
        curNode = toDoQ.last()

        // add current node to tracked path if haven't just popped on off
        if (!justPopped)
            curPath.add(curNode.first)
        else justPopped = false

        // --- find neighbors
        val neighbors = caveGraph[curNode.first]!!

        // start at current node's stored index to avoid previously visited neighbors
        // if a neighbor is lowercase and in the tracked path, skip to next
        // also skip to next if there's not room for another small cave
        var nextIndex = curNode.second
        while (nextIndex < neighbors.size && (
                    (neighbors[nextIndex] !in upperAlpha && neighbors[nextIndex] in curPath && !curPath.roomForOneMore()))
        ) {
            nextIndex++
        }

        // if at 'end' or no neighbors left, then save, pop, and continue
        if (curNode.first == "end" || nextIndex >= caveGraph[curNode.first]!!.size) {
            if (curNode.first == "end") {
                completePaths.add(curPath.toList())
            }

            curPath.removeLast()
            toDoQ.removeLast()
            justPopped = true
            continue
        } else {
            toDoQ[toDoQ.size - 1] = Pair(curNode.first, nextIndex + 1)
            toDoQ.add(Pair(neighbors[nextIndex], 0))
        }
    }

    Output.part(1, "Number of Paths", completePaths.singleCavesOnly().size)
    Output.part(2, "Number of Longer Paths", completePaths.size)

//    recursive solution
//    Output.part(1, "Number of Paths", findAllPaths(caveGraph, "start").size)
//    Output.part(2, "Number of Longer Paths", findAllPaths(caveGraph, "start", smallCaveMax = 2).size)
    Output.executionTime(startTime)
}

fun MutableList<String>.roomForOneMore(): Boolean {
    val smalls = this.filter { it in lowerAlpha }
    return smalls == smalls.distinct()
}

fun List<List<String>>.singleCavesOnly(): List<List<String>> =
    this.filter { list ->
        var singles = true
        list.toSet().forEach {
            if (it in lowerAlpha && list.indexOf(it) != list.lastIndexOf(it))
                singles = false
        }
        singles
    }

// recursive solution
//fun findAllPaths(
//    graph: Map<String, List<String>>,
//    node: String,
//    curPath: MutableList<String> = mutableListOf(),
//    smallCaveMax: Int = 1
//): List<List<String>> {
//    // for returning paths in a list of path lists - yay recursion!
//    val successPaths = mutableListOf<List<String>>()
//
//    // add node to path immediately on traversal
//    curPath.add(node)
//
//    // if at 'end', return the current tracked path
//    if (node == "end") {
//        successPaths.add(curPath)
//        return successPaths
//    } else {
//        // only traverse through Large nodes or not-traversed small nodes
//        graph[node]!!.filter {
//            it in upperAlpha || it !in curPath ||
//                    (it in lowerAlpha && smallCaveMax > 1 && curPath.roomForOneMore())
//        }.forEach {
//            successPaths.addAll(findAllPaths(graph, it, curPath.toMutableList(), smallCaveMax))
//        }
//    }
//
//    // nothing left to traverse, don't return path because not at 'end'
//    return successPaths
//}
