package d21_DiracDice

import util.Input
import util.Output
import kotlin.math.max

val possibleDiracRolls = mapPossibleRolls()

fun main() {
    Output.day(21, "Dirac Dice")
    val startTime = Output.startTime()

    // region Part 1
    val startPositions = Input.parseLines(filename = "/input/d21_start_positions.txt")
        .map { it.substringAfter(": ").toInt() }

    val roll = mutableListOf(98, 99, 100)
    val players = listOf(
        PlayerDeterministic(score = 0, position = startPositions[0]),
        PlayerDeterministic(score = 0, position = startPositions[1])
    )

    var turn = 0
    var nextPlayer = 0
    do {
        turn += 3
        roll.next(3)
        players[nextPlayer].play((roll.sum()))
        nextPlayer = (nextPlayer + 1) % 2
    } while (players.all { it.score < 1000 })

    val loserProduct = players.first { it.score < 1000 }.score * turn
    Output.part(1, "Deterministic Loser Product", loserProduct)

    // endregion

    // region Part 2
    val diracPlayers = listOf(
        PlayerDirac(startPosition = startPositions[0]),
        PlayerDirac(startPosition = startPositions[1])
    )

    diracPlayers[0].play()
    diracPlayers[1].play()

    println(diracPlayers[0].lossMap)
    println(diracPlayers[1].winMap)

    val p1Wins = diracPlayers[0].winMap.map { it.value * diracPlayers[1].lossMap[it.key - 1]!! }.sum()
    val p2Wins = diracPlayers[1].winMap.map { it.value * (diracPlayers[0].lossMap[it.key] ?: 0) }.sum()

    Output.part(2, "Most Possible Wins", max(p1Wins, p2Wins))
    // endregion

    Output.executionTime(startTime)
}

class PlayerDeterministic(
    var score: Int,
    var position: Int
) {
    private fun move(amount: Int) {
        position = (position + amount) % 10
        if (position == 0) position = 10
    }

    private fun addScore(amount: Int) {
        score += amount
    }

    fun play(amount: Int) {
        move(amount)
        addScore(position)
    }
}

fun MutableList<Int>.next(increase: Int) {
    for (i in indices)
        this[i] = (this[i] + increase) % 100

    when (val i = indexOf(0)) {
        -1 -> return
        else -> this[i] = 100
    }
}

// key = roll sum, value = num matching rolls
fun mapPossibleRolls(): Map<Int, Int> {
    val rolls = mutableListOf(mutableListOf(1, 1, 1))

    while (rolls.last().sum() != 9) {
        val next = rolls.last().toMutableList()
        next[2] += 1

        if (next[2] > 3) {
            next[2] = 1
            next[1] += 1
        }

        if (next[1] > 3) {
            next[1] = 1
            next[0] += 1
        }

        rolls.add(next)
    }

    return rolls.groupingBy { it.sum() }.eachCount()
}

class PlayerDirac(
    val startPosition: Int,
    val winMap: MutableMap<Int, Long> = mutableMapOf(), //key=turn, value = numWins
    val lossMap: MutableMap<Int, Long> = mutableMapOf()
) {
    private fun scoreRollPermutation(rolls: List<Int>): Int {
        var position = startPosition
        var score = 0

        rolls.forEach { r ->
            position += r
            if (position > 10)
                position -= 10

            score += position
        }

        return score
    }

    fun play() {
        var rollSets = possibleDiracRolls.keys.map { mutableListOf(it) }.toMutableSet()
        var turn = 1

        while (rollSets.isNotEmpty()) {
            // increment turn
            turn++

            // to store winning roll sets for removal
            val turnWinSets = mutableSetOf<MutableList<Int>>()

            // next permutation
            val newRolls = mutableSetOf<MutableList<Int>>()

            rollSets.forEach { prev ->
                possibleDiracRolls.keys.forEach { poss ->
                    val curRoll = (prev + poss) as MutableList
                    newRolls.add(curRoll)

                    if (scoreRollPermutation(curRoll) > 20) {
                        addWinLoss(winMap, turn, curRoll)
                        turnWinSets.add(curRoll)
                    } else {
                        addWinLoss(lossMap, turn, curRoll)
                    }
                }
            }

            rollSets = (newRolls - turnWinSets) as MutableSet
        }
    }

    private fun addWinLoss(which: MutableMap<Int, Long>, turn: Int, rolls: List<Int>) {
        val n = rolls.map { possibleDiracRolls[it]!! }.reduce { acc, cur -> acc * cur }.toLong()
        which.merge(turn, n) { a, b -> a + b }
    }
}