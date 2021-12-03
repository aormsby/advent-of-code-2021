package d2_Dive

import util.InputHelper

const val POSITION = "position"
const val DEPTH = "depth"
const val AIM = "aim"

fun main() {
    val commands = InputHelper.parseLines("/input/d2_navigation_commands.txt")

    val coordinates = mutableMapOf(
        POSITION to 0,
        DEPTH to 0
    )

    commands.forEach {
        val c = it.split(" ")
        when (c[0]) {
            "down" -> coordinates.merge(DEPTH, c[1].toInt()) { a, b -> a + b }
            "up" -> coordinates.merge(DEPTH, c[1].toInt()) { a, b -> (a - b).checkForSurface() }
            else -> coordinates.merge(POSITION, c[1].toInt()) { a, b -> a + b }
        }
    }

    println("coordinate product: ${coordinates[POSITION]!! * coordinates[DEPTH]!!}}")

    val moreAccurateCoordinates = mutableMapOf(
        POSITION to 0,
        DEPTH to 0,
        AIM to 0
    )

    commands.forEach {
        val c = it.split(" ")
        when (c[0]) {
            "down" -> moreAccurateCoordinates.merge(AIM, c[1].toInt()) { a, b -> a + b }
            "up" -> moreAccurateCoordinates.merge(AIM, c[1].toInt()) { a, b -> a - b }
            else -> {
                moreAccurateCoordinates.merge(POSITION, c[1].toInt()) { a, b -> a + b }
                moreAccurateCoordinates.merge(
                    DEPTH,
                    c[1].toInt()
                ) { a, b -> a + (b * moreAccurateCoordinates[AIM]!!).checkForSurface() }
            }
        }
    }

    println("accurate coordinate product: ${moreAccurateCoordinates[POSITION]!! * moreAccurateCoordinates[DEPTH]!!}")
}

fun Int.checkForSurface(): Int = if (this >= 0) this else 0
