package d7_TheTreacheryOfWhales

import util.Input
import util.Output
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

fun main() {
    Output.day(7, "The Treachery of Whales")
    Output.setStartTime()

    val crabmarines = Input.parseToListOf<Int>(
        rawData = Input.parseLine("/input/d7_crabmarine_positions.txt"), delimiter = ","
    )

    val median = crabmarines.sorted().median()
    val fuelToMedian = crabmarines.sumOf { abs(it - median) }

    Output.part(1, "Fuel to Align to Median with 'Single Step' Consumption", fuelToMedian)

    val averageFloor = crabmarines.average().toInt()        // check lower
    val averageCiel = crabmarines.average().roundToInt()    // possibly check upper

    val fuelToAverageFloor = crabmarines.sumToAverage(averageFloor)
    val fuelToAverage = if (averageFloor != averageCiel)
        min(fuelToAverageFloor, crabmarines.sumToAverage(averageCiel))
    else fuelToAverageFloor

    Output.part(2, "Fuel to Align to Average with 'Sum of Integers' Consumption", fuelToAverage)
    Output.executionTime()
}

fun List<Int>.median(): Int =
    if (this.size % 2 == 0)
        (this[this.size / 2] + this[(this.size - 1) / 2]) / 2
    else this[this.size / 2]

// with higher fuel cost
fun List<Int>.sumToAverage(ave: Int) =
    this.sumOf {
        val diff = abs(it - ave)
        diff * (1 + diff) / 2
    }