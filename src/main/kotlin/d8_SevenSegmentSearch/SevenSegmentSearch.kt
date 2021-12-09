package d8_SevenSegmentSearch

import util.Input
import util.Output

fun main() {
    Output.day(8, "Seven Segment Search")
    Output.setStartTime()

    /*
    // Regex works, but it's slooooooow.
    val allDisplayText = Input.parseAllText(filename = "/input/d8_seven_segment_displays.txt")
    val uniqueSizeSegmentCount =
        Regex("""(?<=\|.{0,})(?:\b\w{2}\b|\b\w{3}\b|\b\w{4}\b|\b\w{7}\b)""")
            .findAll(allDisplayText)
            .count()
     */

    // I sorted the strings by char to use them as map keys! :)
    val displayPatterns = Input.parseLines("/input/d8_seven_segment_displays.txt")
        .map { str ->
            val x = str.split(" | ")
            Pair(
                x[0].trim(' ').split(" ")
                    .map { item -> item.toSortedSet().joinToString("") },
                x[1].trim(' ').split(" ")
                    .map { item -> item.toSortedSet().joinToString("") }
            )
        }

    var uniqueSegmentsFound = 0
    val displayMapList = mutableListOf<MutableMap<String, Int>>()

    // loop over display patterns
    displayPatterns.forEachIndexed { i, pat ->

        // map known combos for 1, 4, 7, 8 in each patter
        displayMapList.add(mutableMapOf())
        pat.first.forEach {
            when (it.length) {
                2 -> displayMapList[i][it] = 1
                3 -> displayMapList[i][it] = 7
                4 -> displayMapList[i][it] = 4
                7 -> displayMapList[i][it] = 8
            }
        }

        // count number of known segments in each display output (Part 1)
        pat.second.forEach {
            if (it in displayMapList[i].keys) uniqueSegmentsFound++
        }
    }

    println(displayMapList)


    // count number of matches (sum of map values)

    Output.part(1, "Count of 1s, 4s, 7s, and 8s", uniqueSegmentsFound)
    Output.part(2, "n/a", "n/a")
    Output.executionTime()
}