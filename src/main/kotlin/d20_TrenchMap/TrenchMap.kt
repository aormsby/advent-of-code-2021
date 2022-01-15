package d20_TrenchMap

import util.Coord
import util.Input
import util.Output

fun main() {
    Output.day(20, "Trench Map")
    val startTime = Output.startTime()

    val input = Input.parseLines(filename = "/input/d20_trench_map_input.txt")
    val algorithm = input[0].map { if (it == '#') '1' else '0' }.joinToString("")
    var image = input.drop(2).map { s ->
        s.map { c -> if (c == '#') '1' else '0' }.joinToString("")
    }

    val flipVoid = algorithm[0] == '1'
    var voidDefault = '0'

    for (step in 1..2) {
        if (flipVoid) voidDefault = if (step % 2 == 0) '1' else '0'
        image = image.enhance(algorithm, voidDefault)
    }

    val twoStepLit = image.fold(0) { sum, line ->
        sum + line.fold(0) { acc, cur ->
            acc + if (cur == '1') 1 else 0
        }
    }

    for (step in 3..50) {
        if (flipVoid) voidDefault = if (step % 2 == 0) '1' else '0'
        image = image.enhance(algorithm, voidDefault)
    }

    val fiftyStepLit = image.fold(0) { sum, line ->
        sum + line.fold(0) { acc, cur ->
            acc + if (cur == '1') 1 else 0
        }
    }

    Output.part(1, "Number Lit, 2 Steps", twoStepLit)
    Output.part(1, "Number Lit, 50 Steps", fiftyStepLit)
    Output.executionTime(startTime)
}

val neighbors = Coord(0, 0).allNeighborsWithSelf()
    .sortedWith(compareBy<Coord> { it.x }.thenBy { it.y })

fun List<String>.getEnhancementIndex(x: Int, y: Int, voidDefault: Char): Int {
    var binaryString = ""
    val squareSize = this.size

    neighbors.forEach { n ->
        val c = Coord(x, y) + n
        binaryString += if (c.x < 0 || c.y < 0 || c.x + 1 > squareSize || c.y + 1 > squareSize) voidDefault
        else this[c.x][c.y]
    }

    return binaryString.toInt(2)
}

fun List<String>.enhance(algo: String, voidDefault: Char): List<String> {
    val newImage = mutableListOf<String>()

    for (x in -1..size) {
        var s = ""
        for (y in -1..first().length) {
            s += algo[this.getEnhancementIndex(x, y, voidDefault)]
        }
        newImage.add(s)
    }

    return newImage
}
