package d16_PacketDecoder

import util.Input
import util.Output

val BITSMap = mapOf(
    '0' to "0000",
    '1' to "0001",
    '2' to "0010",
    '3' to "0011",
    '4' to "0100",
    '5' to "0101",
    '6' to "0110",
    '7' to "0111",
    '8' to "1000",
    '9' to "1001",
    'A' to "1010",
    'B' to "1011",
    'C' to "1100",
    'D' to "1101",
    'E' to "1110",
    'F' to "1111",
)

fun main() {
    Output.day(16, "Packet Decoder")
    val startTime = Output.startTime()

    val transmissionBits = ArrayDeque(
        Input.parseToListOf<Char>(
            rawData = Input.parseAllText(filename = "/input/d16_BITS_transmision.txt")
        ).flatMap {
            BITSMap[it]!!.split("")
                .mapNotNull { c -> if (c.isNotBlank()) c.single() else null }
        }
    )

    val decodedTransmission = transmissionBits.getPackets()

    Output.part(1, "Sum of Version Numbers", decodedTransmission.first)
    Output.part(2, "Evaluated Transmission", decodedTransmission.second)

    Output.executionTime(startTime)
}

fun ArrayDeque<Char>.getPackets(): Pair<Int, Long> {
    var curBits = ""
    val subPackets = mutableListOf<Long>()

    for (i in 1..3) curBits += removeFirst()
    var version = curBits.toInt(2)
    curBits = ""

    for (i in 1..3) curBits += removeFirst()
    val typeId = curBits.toInt(2)
    curBits = ""

    when (typeId) {
        4 -> {  // literal value
            val literalChunks = mutableListOf<String>()
            do {
                while (curBits.length < 5)
                    curBits += removeFirst()

                literalChunks.add(curBits.takeLast(5))
                curBits = ""
            } while (!literalChunks.last().startsWith('0'))

            subPackets += literalChunks.joinToString("") { it.takeLast(4) }.toLong(2)

            // no need to recurse for literal values,
            // hop down to end return :)
        }
        else -> {   // operator
            when (removeFirst().toBitLength()) {
                11 -> {
                    for (i in 1..11) curBits += removeFirst()
                    val numSubPackets = curBits.toInt(2)

                    for (i in 1..numSubPackets) {
                        val p = getPackets()
                        version += p.first
                        subPackets += p.second
                    }
                }
                else -> {
                    for (i in 1..15) curBits += removeFirst()
                    val subPacketsLength = curBits.toInt(2)

                    val subPacketBits = ArrayDeque<Char>()
                    for (i in 1..subPacketsLength)
                        subPacketBits += removeFirst()

                    while (subPacketBits.size > 0) {
                        if (subPacketBits.all { it == '0' })
                            break

                        val p = subPacketBits.getPackets()
                        version += p.first
                        subPackets += p.second
                    }
                }
            }
        }
    }

    return Pair(version, subPackets.operateBy(typeId))
}

private fun Char.toBitLength(): Int = with(digitToInt()) {
    if (this == 0) 15
    else 11
}

fun MutableList<Long>.operateBy(id: Int) =
    when (id) {
        0 -> sumOf { it }
        1 -> reduce { acc, cur -> acc * cur }
        2 -> minOf { it }
        3 -> maxOf { it }
        5 -> if (this[0] > this[1]) 1 else 0
        6 -> if (this[0] < this[1]) 1 else 0
        7 -> if (this[0] == this[1]) 1 else 0
        else -> first() // return literal (4)
    }