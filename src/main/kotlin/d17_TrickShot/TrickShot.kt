package d17_TrickShot

import util.Input
import util.Output

fun main() {
    Output.day(17, "Trick Shot")
    val startTime = Output.startTime()

    val targetArea = Input.parseAllText(filename = "/input/d17_target_area.txt")
        .split(',')
        .map { it.substringAfter("=").split("..").map { n -> n.toInt() } }

    val yMaxVelo = (targetArea[1][0] + 1) * -1
    Output.part(1, "Highest Y Position", (yMaxVelo * (yMaxVelo + 1)) / 2)

    val possibleXVelos = mutableListOf<Pair<Int, Int>>()
    val possibleYVelos = mutableListOf<Pair<Int, Int>>()
    val maxSteps = (yMaxVelo * 2) + 2

    for (step in 1..maxSteps) {
        possibleXVelos.addAll(
            (1..targetArea[0][1]).filter { n ->
                (1..step).sumOf {
                    val x = n - (it - 1)
                    if (x > 0) x else 0
                } in targetArea[0][0]..targetArea[0][1]
            }.map { Pair(step, it) }
        )

        possibleYVelos.addAll(
            (targetArea[1][0]..yMaxVelo).filter { n ->
                (1..step).sumOf { n - (it - 1) } in targetArea[1][0]..targetArea[1][1]
            }.map { Pair(step, it) }
        )
    }

    val distinctInitialPairs = possibleYVelos.mapNotNull { y ->
        if (y.second == 9)
            print("")
        val x = possibleXVelos.filter { it.first == y.first }
        if (x.isNotEmpty())
            x.map { Pair(it.second, y.second) }
        else null
    }.flatten().toSet()

    Output.part(2, "Distinct Initial Velocity Values", distinctInitialPairs.size)
    Output.executionTime(startTime)
}
