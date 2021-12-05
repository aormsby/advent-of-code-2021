package d4_GiantSquid

import util.CollectionHelper
import util.Input
import util.Output

// share size of board across functions
var boardSize = 0

fun main() {
    lateinit var bingoDrawings: List<Int>
    val bingoBoards = mutableListOf<Pair<List<MutableList<Int>>, List<MutableList<Int>>>>()

    // set up number call and board collections
    with(Input.parseLines("/input/d4_bingo_subsystem.txt")) {
        // get bingo drawings as Ints
        bingoDrawings = Input.parseToListOf(rawData = this[0], delimiter = ",")

        // get boards as 2d lists
        val tempBoards = with(this.drop(2).chunked(6)) { transformTo2D() }

        // set board size
        boardSize = tempBoards.first().first().size

        // flatten boards and their transposed forms
        tempBoards.forEach { board ->
            bingoBoards.add(
                Pair(board, CollectionHelper.transposeList(board))
            )
        }
    }

    // for storing index of winning board
    var winningBoard = -1
    var lastNum = -1

    run win@{
        bingoDrawings.forEach { num ->
            for (b in bingoBoards.indices) {
                var x: Int
                var y: Int

                for (row in bingoBoards[b].first.indices) {
                    val col = bingoBoards[b].first[row].indexOf(num)
                    if (col != -1) {   // num found
                        x = row
                        y = col

                        // update board pairs with marked space (-1)
                        bingoBoards[b].first[x][y] = -1
                        bingoBoards[b].second[y][x] = -1

                        if (isWinningBoard(bingoBoards[b], x, y)) {
                            winningBoard = b
                            lastNum = num
                            return@win  // return outer loop if winner found
                        }
                    }
                }
            }
        }
    }

    Output.part(1, "Winning Board Remainder Sum",
        bingoBoards[winningBoard].first.fold(0) { acc, list -> acc + list.filterNot { it == -1 }.sum() } * lastNum)
}

fun isWinningBoard(boardPair: Pair<List<MutableList<Int>>, List<MutableList<Int>>>, row: Int, col: Int): Boolean {
    return when {
        boardPair.first[row].all { it == -1 } -> true
        boardPair.second[col].all { it == -1 } -> true
        else -> false
    }
}

fun List<List<String>>.transformTo2D() = this.map { board ->
    board.mapNotNull { line ->
        if (line.isBlank()) null
        else line.trim(' ')
            .replace("  ", " ")
            .split(" ")
            .map { it.toInt() } as MutableList
    }
}
