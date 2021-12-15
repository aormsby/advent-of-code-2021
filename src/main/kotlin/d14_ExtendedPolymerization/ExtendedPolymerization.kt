package d14_ExtendedPolymerization

import util.Input
import util.Output

fun main() {
    Output.day(13, "Extended Polymerization")
    val startTime = Output.startTime()

    // sort input
    val input = Input.parseLines(filename = "/input/d14_polymer_pairs_insertion_rules.txt")
    val polymer = input[0].split("").mapNotNull { if (it.isNotBlank()) it else null } as MutableList
    val instructions = input.drop(2).associateBy(
        keySelector = { it.slice(0..1) },
        valueTransform = { it.takeLast(1) }
    )

    // create 2 maps -- one to count single chars, one to keep track of existing pairs in polymer chain
    val polySingles = polymer.toSet().associateBy({ it }) { polymer.count { c -> c == it }.toLong() } as MutableMap
    val polyPairs = polymer.windowed(2).associateBy(
        keySelector = { it.joinToString("") },
        valueTransform = { v -> polymer.windowed(2).count { c -> c == v }.toLong() }) as MutableMap

    // for part 1
    var countStep10 = 0L

    for (step in 1..40) {
        // for each polymer chain pair..
        polyPairs.filterNot { it.value < 1 }.forEach { pair ->
            instructions[pair.key]?.also { insert ->
                // add newly formed pairs from insertion
                polyPairs.merge(pair.key[0] + insert, pair.value) { a, b -> a + b }
                polyPairs.merge(insert + pair.key[1], pair.value) { a, b -> a + b }

                // subtract pair that was split up
                polyPairs.merge(pair.key, pair.value * -1) { a, b -> a + b }

                // add inserted letter count to singles map
                polySingles.merge(insert, pair.value) { a, b -> a + b }
            }
        }

        // store step 10 data for part answer
        if (step == 10) countStep10 = polySingles.values.maxOf { it } - polySingles.values.minOf { it }
    }

    Output.part(1, "10 Steps", countStep10)
    Output.part(2, "40 Steps", polySingles.maxOf { it.value } - polySingles.minOf { it.value })
    Output.executionTime(startTime)
}
