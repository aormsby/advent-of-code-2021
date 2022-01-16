package d21_DiracDice

import util.Input
import util.Output

val possibleRollMap = mapPossibleRolls()

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

    diracPlayers[0].play(true)
    diracPlayers[1].play(false)

    val p1Wins = diracPlayers[0].winLossMap.map { it.value * diracPlayers[1].winLossMap[it.key - 1]!! }.sum()

    Output.part(2, "P1 Possible Wins", p1Wins)
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

    return rolls.groupingBy { it.sum() }.eachCount()//.entries.associateBy({ it.key }, { it.value * 3 })
}

fun MutableSet<MutableList<Int>>.addPossibleRolls(possibilities: Set<Int>): MutableSet<MutableList<Int>> {
    val newRolls = mutableSetOf<MutableList<Int>>()

    possibilities.forEach { d ->
        forEach {
            newRolls.add((it + d) as MutableList)
        }
    }

    return newRolls
}

// todo: collect both wins and losses for each player
class PlayerDirac(
    val startPosition: Int,
    val winLossMap: MutableMap<Int, Long> = mutableMapOf() //key=turn, value = numWins
) {
    fun scoreRollPermutation(rolls: List<Int>): Int {
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

    fun play(checksWins: Boolean) {
        var rollSets = possibleRollMap.keys.map { mutableListOf(it) }.toMutableSet()
        var turn = 1

        while (rollSets.isNotEmpty()) {
            // increment turn
            turn++

            // to store winning/losing roll sets for removals
            val winLossSets = mutableSetOf<MutableList<Int>>()

            // todo: add rolls and calculate wins at the same time, fewer loops
            // add next possible roll info
            rollSets = rollSets.addPossibleRolls(possibleRollMap.keys)

            rollSets.forEach {
                val keep = when {
                    checksWins -> scoreRollPermutation(it) > 20
                    else -> scoreRollPermutation(it) < 21
                }

                if (keep) {
                    // calculate paths to result and store
                    addWinLoss(turn, it)

                    // store for subtraction from set
                    winLossSets.add(it)
                }
            }

            if (checksWins) rollSets -= winLossSets
            else rollSets = winLossSets
        }
    }

    // add win for p1, loss for p2 on given turn
    fun addWinLoss(turn: Int, rolls: List<Int>) {
        val n = rolls.map { possibleRollMap[it]!! }.reduce { acc, cur -> acc * cur }.toLong()
        winLossMap.merge(turn, n) { a, b -> a + b }
    }
}