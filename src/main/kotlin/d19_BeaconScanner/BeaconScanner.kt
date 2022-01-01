package d19_BeaconScanner

import util.Coord
import util.Input
import util.Output

fun main() {
    val startTime = Output.startTime()
    Output.day(19, "Beacon Scanner")

    val scannerList = Input.parseLines(filename = "/input/d19_scanner_beacon_map.txt")
        .toScannerList()

    scannerList[0].hasShifted = true
    val shiftedScanners = scannerList.take(1).toMutableList()
    val unshiftedScanners = scannerList.drop(1).toMutableList()

    while (unshiftedScanners.isNotEmpty()) {
        unshiftedScanners.forEach { s ->
            shiftedScanners.forEach { s2 ->
                if (s != s2) {
                    s.findOverlapsWith(s2)
                    if (s.hasShifted) {
                        shiftedScanners.add(s)
                        unshiftedScanners.remove(s)
                    }
                }
            }
        }
    }

//    // todo: must reposition a match with 0 before anything else, probably start with a list of scanners ordered based on 0
//    scannerList.forEach { s ->
//        scannerList.forEach { s2 ->
//            // todo: only compare if other scanner has shifted? or something like that.
//            if (s != s2) s.findOverlapsWith(s2)
//        }
//    }

    Output.part(1, "n/a", "n/a")
    Output.part(2, "n/a", "n/a")
    Output.executionTime(startTime)
}

fun List<String>.toScannerList(): List<Scanner> {
    val scanners = mutableListOf<Scanner>()
    var curScanner = Scanner(0)

    forEach { line ->
        when {
            line.startsWith("---") -> {
                curScanner = Scanner(which = line.substringAfter("r ").substringBefore(" -").toInt())
                scanners.add(curScanner)
            }
            line.isNotBlank() -> {
                with(line.split(',').map { it.toInt() }) {
                    curScanner.allBeacons.add(Coord(this[0], this[1]))
                }
            }
            line.isBlank() -> curScanner.calculateBeaconDistances()
        }

        if (line == this.last()) curScanner.calculateBeaconDistances()
    }

    return scanners
}

data class Scanner(
    var which: Int,
) {
    var position: Coord = Coord(0, 0)
    val allBeacons: MutableList<Coord> = mutableListOf()

    //    val overlapMap: MutableMap<Coord, Coord> = mutableMapOf()
//    val orientationShift: Coord = Coord(0, 0)
    var hasShifted: Boolean = false
    val beaconDistances: MutableMap<String, Float> = mutableMapOf()
    // 'which' other scanner || my matching, other matching
//    val overlapMap: MutableMap<Int, List<Pair<Int, Int>>> = mutableMapOf()

    fun calculateBeaconDistances() {
        allBeacons.forEachIndexed { i, c1 ->
            allBeacons.forEachIndexed { j, c2 ->
                val k = listOf(i, j).sorted().joinToString("_")
                if (i != j && k !in beaconDistances) beaconDistances[k] = c1.distanceTo(c2)
            }
        }
    }

    fun findOverlapsWith(other: Scanner) {
        val myMatches = beaconDistances.filterValues { it in other.beaconDistances.values }

        if (myMatches.size >= 3) {
            val otherMatches = other.beaconDistances.filterValues { it in myMatches.values }
            orientScanner(myMatches, otherMatches, other)
            hasShifted = true
        }

    }

    fun orientScanner(myMatches: Map<String, Float>, otherMatches: Map<String, Float>, other: Scanner) {
        val myfirstKeys = myMatches.entries.sortedBy { it.value }.take(2).map { it.key }
        val otherFirstKeys = otherMatches.entries.sortedBy { it.value }.take(2).map { it.key }

        val myCoord = findMatchingCoord(myfirstKeys, allBeacons)
        val otherCoord = findMatchingCoord(otherFirstKeys, other.allBeacons)

        // todo: orient scanner here

        // position current scanner based on coord match
        position = otherCoord.second + myCoord.second.opposite()

        println()
    }

    fun findMatchingCoord(keys: List<String>, beaconList: List<Coord>): Pair<Int, Coord> {
        val indices = keys.flatMap { it.split('_') }.map { it.toInt() }
        val coords = indices.map { beaconList[it] }

        return Pair(
            indices.first { indices.count { it == it } > 1 },
            coords.first { coords.count { it == it } > 1 },
        )
    }
}


// find matching end-to-end connection
// set common connection index to common coordinate, set other to first remaining beacon
// repeat for 'other'
// set self position based on known matches
