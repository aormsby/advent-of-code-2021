package d8_SevenSegmentSearch

import util.Input
import util.Output

fun main() {
    Output.day(8, "Seven Segment Search")
    val startTime = Output.startTime()

    /*
    // Regex works for P1, but it's slooooooow.
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

    // For Part and Part 2 outputs
    var uniqueSegmentsFound = 0
    var outputSum = 0

    // loop over display patterns
    displayPatterns.forEach { pat ->
        val displayMap = mutableMapOf<Int, String>()

        // map known combos for 1, 4, 7, 8 in each patter
        pat.first.forEach {
            when (it.length) {
                2 -> displayMap[1] = it
                3 -> displayMap[7] = it
                4 -> displayMap[4] = it
                7 -> displayMap[8] = it
            }
        }

        // count number of known segments in each display output (Part 1)
        pat.second.forEach {
            if (it in displayMap.values) uniqueSegmentsFound++
        }

        // deduce remaining segments and map
        val fiveSegs = pat.first.filter { it.length == 5 } as MutableList
        val sixSegs = pat.first.filter { it.length == 6 } as MutableList

        // 9 is the only 6-segment number to contain the segments of 7 and 4
        displayMap[9] = sixSegs.first { displayMap[7]!!.isInside(it) && displayMap[4]!!.isInside(it) }
        sixSegs.remove(displayMap[9]!!)

        // 0 is the only remaining 6-segment number to contain the segments of 7
        displayMap[0] = sixSegs.first { displayMap[7]!!.isInside(it) }
        sixSegs.remove(displayMap[0]!!)

        // 6 is the only 6-segment number remaining
        displayMap[6] = sixSegs.first()

        // 3 is the only 5-segment number to contain the segments of 7
        displayMap[3] = fiveSegs.first() { displayMap[7]!!.isInside(it) }
        fiveSegs.remove(displayMap[3]!!)

        // To obtain five, I got the diffs of 3, 0, and 6
        // The remaining segments are inside 5's segments (but not 2's)
        val fiveCheck = displayMap[3]!!.diff(displayMap[0]!!).diff(displayMap[6]!!)
        displayMap[5] = fiveSegs.first { fiveCheck.isInside(it) }
        fiveSegs.remove(displayMap[5]!!)

        // 2 is the only 5-segment number remaining
        displayMap[2] = fiveSegs.first()

        // determine output value and add to total sum
        // flipped the map for easy pattern/number correlation
        val flippedMap = displayMap.entries.associateBy({ it.value }) { it.key }
        outputSum += pat.second.fold("") { acc, s -> acc + flippedMap[s] }.toInt()
    }

    Output.part(1, "Count of 1s, 4s, 7s, and 8s", uniqueSegmentsFound)
    Output.part(2, "Sum of All Outputs", outputSum)
    Output.executionTime(startTime)
}

/**
 * Returns a String of all segments that are not shared
 * between the two given strings.
 */
fun String.diff(s2: String): String {
    val group = (this + s2).groupingBy { it }.eachCount()
    val minValue = group.minOf { it.value }
    return group.filter { it.value == minValue }.keys.joinToString("")
}

/**
 * Checks if each segment is shared by a given pattern
 */
fun String.isInside(s: String): Boolean {
    this.forEach {
        if (!s.contains(it))
            return false
    }
    return true
}