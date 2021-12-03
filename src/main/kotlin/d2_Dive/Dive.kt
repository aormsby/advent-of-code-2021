package d2_Dive

import util.Input
import util.Output

const val POSITION = "position"
const val DEPTH = "depth"
const val AIM = "aim"

const val UP = "up"
const val DOWN = "down"
//const val FORWARD = "forward"

fun main() {
    Output.day(2, "Dive!")

    val commands = Input.parseToPairList<String, Int>("/input/d2_navigation_commands.txt")

    val coordinates = mutableMapOf(
        POSITION to 0,
        DEPTH to 0
    )

    commands.forEach {
        when (it.first) {
            DOWN -> coordinates.merge(DEPTH, it.second) { a, b -> a + b }
            UP -> coordinates.merge(DEPTH, it.second) { a, b -> (a - b).checkForSurface() }
            else -> coordinates.merge(POSITION, it.second) { a, b -> a + b }
        }
    }

    Output.part(1, "Bad Coordinates", coordinates[POSITION]!! * coordinates[DEPTH]!!)

    val accurateCoordinates = mutableMapOf(
        POSITION to 0,
        DEPTH to 0,
        AIM to 0
    )

    commands.forEach {
        when (it.first) {
            DOWN -> accurateCoordinates.merge(AIM, it.second) { a, b -> a + b }
            UP -> accurateCoordinates.merge(AIM, it.second) { a, b -> a - b }
            else -> {
                accurateCoordinates.merge(POSITION, it.second) { a, b -> a + b }
                accurateCoordinates.merge(
                    DEPTH,
                    it.second
                ) { a, b -> a + (b * accurateCoordinates[AIM]!!).checkForSurface() }
            }
        }
    }

    Output.part(1, "Good Coordinates", accurateCoordinates[POSITION]!! * accurateCoordinates[DEPTH]!!)
}

// Turns out this was unnecessary... but a great idea!
fun Int.checkForSurface(): Int = if (this >= 0) this else 0
