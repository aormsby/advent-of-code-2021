package d13_TransparentOrigami

import util.Coord
import util.Input
import util.Output
import kotlin.math.abs

fun main() {
    Output.day(13, "Transparent Origami")
    val startTime = Output.startTime()

    val input = Input.parseLines(filename = "/input/d13_fold_instructions.txt")

    var page = input.dropLastWhile { it.isNotBlank() }.dropLast(1).map {
        val c = it.split(",")
        Coord(x = c[0].toInt(), y = c[1].toInt())
    }

    var instructions = input.takeLastWhile { it.isNotBlank() }.map {
        Pair(
            it[it.indexOf("=") - 1],
            it.slice(it.indexOf("=") + 1 until it.length).toInt()
        )
    }

    // fold once
    page = page.foldBy(instructions[0].first, instructions[0].second)
    val firstFoldResult = page.size
    instructions = instructions.drop(1)

    // finish folding
    instructions.forEach {
        page.foldBy(it.first, it.second)
    }

    Output.part(1, "Points after First Fold", firstFoldResult)
    Output.part(2, "Code", "")
    page.print()

    Output.executionTime(startTime)
}

/**
 * Fold page by axis and line/column
 */
fun List<Coord>.foldBy(axis: Char, foldLine: Int): List<Coord> {
    val flippers =
        if (axis == 'x') filter { it.x > foldLine }
        else filter { it.y > foldLine }

    flippers.forEach {
        if (axis == 'x') it.x = abs(it.x - (foldLine * 2))
        else it.y = abs(it.y - (foldLine * 2))
    }

    return this.distinct()
}

fun List<Coord>.print() {
    println()
    val width = maxOf { it.x }
    val height = maxOf { it.y }

    val p = Array(size = height + 1) {
        Array(size = width + 1) { ' ' }
    }

    forEach { p[it.y][it.x] = '%' }

    p.forEach { x ->
        x.forEach {
            print(it)
        }
        println()
    }
    println()
}