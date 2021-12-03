package d1_SonarSweep

import util.Input
import util.Output

fun main() {
    Output.day(1, "Sonar Sweep")

    val depthList = Input.parseLinesTo<Int>("/input/d1_sea_floor_depths.txt")

    val deepenings = foldToInt(depthList)
    Output.part(1, "Deepenings", deepenings)

    val threeDeepenings = foldToInt(mapToThreeSums(depthList))
    Output.part(2, "3-Deepenings", threeDeepenings)
}

fun foldToInt(list: List<Int>): Int =
    list.foldIndexed(0) { i, acc, cur ->
        list.getOrNull(i - 1)?.let {
            if (cur > it) acc + 1 else acc
        } ?: acc
    }

fun mapToThreeSums(list: List<Int>): List<Int> =
    list.mapIndexedNotNull { i, _ ->
        if (list.size - i < 3) null
        else listOf(
            list[i],
            list[i + 1],
            list[i + 2]
        ).sum()
    }