package d10_SyntaxScoring

import util.Input
import util.Output

fun main() {
    Output.day(10, "Syntax Scoring")
    val startTime = Output.startTime()

    val navSubsystem = Input.parseLines(filename = "/input/d10_syntax_chunks.txt")
        .map { Input.parseToListOf<Char>(rawData = it) } as MutableList

    val errorPoints = mapOf(
        ')' to 3,
        ']' to 57,
        '}' to 1197,
        '>' to 25137
    )

    val autocompletePoints = mapOf(
        ')' to 1,
        ']' to 2,
        '}' to 3,
        '>' to 4
    )

    val openers = listOf('(', '[', '{', '<')
    val closers = listOf(')', ']', '}', '>')

    val errorMap = mutableMapOf<Char, Int>()
    val autocompleteScores = mutableListOf<Long>()

    navSubsystem.forEachIndexed next@{ i, line ->
        val deq = ArrayDeque<Char>()

        // go char-by-char, adding to deque and finding corrupt lines
        line.forEach { c ->
            if (c in openers)
                deq.addLast(c)
            else if (closers.indexOf(c) == openers.indexOf(deq.last()))
                deq.removeLast()
            else {  // track syntax error
                errorMap.merge(c, 1) { a, b -> a + b }
                return@next
            }
        }

        // complete lines and track autocomplete char points
        val scoresInOrder = mutableListOf<Int>()
        deq.reversed().forEach { c ->
            scoresInOrder.add(autocompletePoints[closers[openers.indexOf(c)]]!!)
        }
        autocompleteScores.add(scoresInOrder.fold(0L) { acc, cur -> (acc * 5) + cur })
    }

    Output.part(1, "Syntax Error Score", errorMap.map { errorPoints[it.key]!! * it.value }.sum())
    Output.part(2, "Autocomplete Score", autocompleteScores.sorted()[autocompleteScores.size / 2])
    Output.executionTime(startTime)
}