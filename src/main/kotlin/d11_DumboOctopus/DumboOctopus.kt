package d11_DumboOctopus

import util.Coord
import util.Input
import util.Output

val octopusGrid = Input.parseLines(filename = "/input/d11_dumbo_octopi_formation.txt")
    .mapIndexed { l, line ->
        line.split("").filter { n -> n.isNotBlank() }.mapIndexed { c, col ->
            DumboOctopus(coord = Coord(x = l, y = c), energy = col.toInt())
        }
    }

val numRows = octopusGrid.size
val numCol = octopusGrid[0].size
val numOctopi = numRows * numCol
var allFlashStep = 0

val stepHasFlashedList = mutableListOf<DumboOctopus>()
var currentFlashers = mutableListOf<DumboOctopus>()
val nextFlashers = mutableListOf<DumboOctopus>()

fun main() {
    Output.day(11, "Dumbo Octopus")
    val startTime = Output.startTime()

    var step100Flashes = 0

    var step = 1
    while (true) {
        octopusGrid.forEach { row ->
            row.forEach { oct ->
                oct.energy++

                if (oct.energy > 9) {
                    currentFlashers.add(oct)
                    oct.flashed = true
                }
            }
        }

        while (currentFlashers.size > 0) {
            stepHasFlashedList.addAll(currentFlashers)
            currentFlashers.forEach { oct ->
                octopusGrid[oct.coord.x][oct.coord.y].flashAndEnergize()
            }

            currentFlashers.clear()
            currentFlashers.addAll(nextFlashers)
            nextFlashers.clear()
        }

        val flashed = stepHasFlashedList.distinct()

        if (step <= 100) {
            step100Flashes += flashed.size
        }

        if (stepHasFlashedList.size == numOctopi) {
            allFlashStep = step
            break
        }

        stepHasFlashedList.clear()
        flashed.forEach { it.reset() }
        step++
    }

    Output.part(1, "Flashes: 100 Steps", step100Flashes)
    Output.part(2, "All Flash Step", step)
    Output.executionTime(startTime)
}

data class DumboOctopus(
    val coord: Coord,
    var energy: Int,
    var flashed: Boolean = false
)

fun DumboOctopus.flashAndEnergize() {
    // energize adjacent octopi
    val adjRows = mutableListOf(0)
    if (this.coord.x != 0)
        adjRows.add(-1)
    if (this.coord.x != numRows - 1)
        adjRows.add(1)

    val adjCols = mutableListOf(0)
    if (this.coord.y != 0)
        adjCols.add(-1)
    if (this.coord.y != numCol - 1)
        adjCols.add(1)

    val adjPoints = generateAdjPoints(adjRows, adjCols)
    val energizedOctopi = mutableListOf<DumboOctopus>()

    // find adjacent octopi and increase their energy
    adjPoints.forEach {
        val adjOct = octopusGrid[this.coord.x + it.first][this.coord.y + it.second]
        energizedOctopi.add(adjOct)
        adjOct.energy++
    }

    energizedOctopi.filter { !it.flashed && it.energy > 9 && it !in stepHasFlashedList }.forEach {
        it.flashed = true
        nextFlashers.add(it)
    }
}

fun DumboOctopus.reset() {
    this.flashed = false
    this.energy = 0
}

fun generateAdjPoints(l1: List<Int>, l2: List<Int>): List<Pair<Int, Int>> {
    val pointList = mutableListOf<Pair<Int, Int>>()

    for (x in l1) {
        for (y in l2) {
            pointList.add(Pair(x, y))
        }
    }

    return pointList.filter { it != Pair(0, 0) }
}