package d4_GiantSquid

import util.CollectionHelper
import util.Input
import util.Output

var boardSize = 0

fun main() {
    var bingoDrawings: List<Int>
    var bingoBoards = mutableListOf<BingoBoard>()

    // set up number call and board collections
    with(Input.parseLines("/input/d4_bingo_subsystem.txt")) {
        // get bingo drawings as Ints
        bingoDrawings = Input.parseToListOf<Int>(rawData = this[0], delimiter = ",")

        // get boards as 2d lists
        val tempBoards = with(this.drop(2).chunked(6)) { transformTo2D() }

        // flatten boards and their transposed forms
        tempBoards.forEach { board ->
            bingoBoards.add(
                BingoBoard(board, CollectionHelper.transposeList(board))
            )
        }

        // set board size
        boardSize = bingoBoards.first().rows.first().size
    }

    // to store board and called number data
    var firstWinBoard = BingoBoard(listOf(), listOf())
    var firstWinNum = -1
    var lastWinBoard = BingoBoard(listOf(), listOf())
    var lastWinNum = -1

    run end@{
        // call numbers
        bingoDrawings.forEach { num ->
            // mark boards
            bingoBoards.forEach {
                mark(it, num)

                if (it.isWinner && firstWinBoard.rows.isEmpty()) {
                    firstWinBoard = BingoBoard(it.rows, it.columns)
                    firstWinNum = num
                }
            }

            if (bingoBoards.size == 1 && bingoBoards.first().isWinner) {
                lastWinBoard = bingoBoards.first()
                lastWinNum = num
                return@end
            }
            // remove winners from play
            bingoBoards = bingoBoards.filterNot { it.isWinner }.toMutableList()
        }
    }

    Output.part(1, "Winning Board Remainder Sum",
        firstWinBoard.rows.fold(0) { acc, list -> acc + list.filterNot { it == -1 }.sum() } * firstWinNum)

    Output.part(1, "Winning Board Remainder Sum",
        lastWinBoard.rows.fold(0) { acc, list -> acc + list.filterNot { it == -1 }.sum() } * lastWinNum)
}

fun mark(board: BingoBoard, num: Int) {
    var x = -1
    var y = -1

    // mark nums
    for (row in board.rows.indices) {
        val col = board.rows[row].indexOf(num)
        if (col != -1) {   // num found
            x = row
            y = col

            board.rows[x][y] = -1
            board.columns[y][x] = -1
            break
        }
    }

    // check if winner
    if (x != -1) {
        board.isWinner = board.rows[x].all { it == -1 } || board.columns[y].all { it == -1 }
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

data class BingoBoard(
    val rows: List<MutableList<Int>>,       // rows from input
    val columns: List<MutableList<Int>>,    // transposed rows
    var isWinner: Boolean = false           // to mark for removal from game
)