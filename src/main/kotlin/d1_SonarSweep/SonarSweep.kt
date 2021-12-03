package d1_SonarSweep

import util.InputHelper

fun main() {
    // read input
    val depthList = InputHelper.parseLinesTo<Int>("/input/d1_sea_floor_depths.txt")

    // part 1
    val deepenings = foldToInt(depthList)
    println("deepenings: $deepenings")

    //part 2
    val threeDeepenings = foldToInt(mapToThreeSums(depthList))
    println("three-deepenings: $threeDeepenings")
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