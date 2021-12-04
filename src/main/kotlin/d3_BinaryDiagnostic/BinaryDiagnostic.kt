package d3_BinaryDiagnostic

import util.Input
import util.Output

fun main() {
    Output.day(3, "Binary Diagnostic")

    val report = Input.parseLines("/input/d3_diagnostic_report.txt")
    val transposedReport = transposeMatrix(report)

    val gammaStr = transposedReport.fold("") { str, cur ->
        if (cur.count { it == '0' } > cur.count { it == '1' }) str + '0'
        else str + '1'
    }

    val gamma = gammaStr.toInt(2)
    val bitMask = Array(gammaStr.length) { '1' }.joinToString("")
    val epsilonStr = gamma.xor(bitMask.toInt(2))

    Output.part(1, "Power Consumption", gamma * epsilonStr)
    Output.part(2, "Life Support Rating", getO2Rating(report) * getCO2Rating(report))
}

fun transposeMatrix(list: List<String>): List<MutableList<Char>> {
    val transposed = mutableListOf<MutableList<Char>>()

    list.forEachIndexed { x, str ->
        str.forEachIndexed { y, ch ->
            if (transposed.size <= y) transposed.add(y, mutableListOf(ch))
            else transposed[y].add(ch)
        }
    }

    return transposed
}

fun getO2Rating(list: List<String>): Int {
    var l = list
    for (i in 0 until l.first().length) {
        val highBit = getMostCommonBit(l.groupingBy { it[i] }.eachCount())
        l = l.filter { it[i] == highBit }
        if (l.size <= 1) break
    }

    return l.joinToString("").toInt(2)
}

fun getCO2Rating(list: List<String>): Int {
    var l = list
    for (i in 0 until l.first().length) {
        val highBit = getMostCommonBit(l.groupingBy { it[i] }.eachCount())
        l = l.filter { it[i] != highBit }
        if (l.size <= 1) break
    }

    return l.joinToString("").toInt(2)
}

fun getMostCommonBit(countMap: Map<Char, Int>, tie: Char = '1'): Char =
    when {
        countMap['0'] == countMap['1'] -> tie
        else -> countMap.maxByOrNull { it.value }?.key ?: tie
    }