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
        unshiftedScanners.forEach shifted@{ s ->
            shiftedScanners.forEach { s2 ->
                if (s != s2) {
                    s.findOverlapsWith(s2)
                    if (s.hasShifted) return@shifted
                }
            }
        }

        val newShifts = unshiftedScanners.filter { it.hasShifted }
        shiftedScanners.addAll(newShifts)
        newShifts.forEach { unshiftedScanners.remove(it) }
    }

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
//                    curScanner.allBeacons.add(Coord3d(this[0], this[1], this[2]))
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

    //    var position: Coord3d = Coord3d(0, 0, 0)
    var allBeacons: MutableList<Coord> = mutableListOf()

    //    var allBeacons: MutableList<Coord3d> = mutableListOf()
    var orientationShift: Coord = Coord(0, 0)
    var hasShifted: Boolean = false
    val beaconDistances: MutableMap<String, Float> = mutableMapOf()

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
        val mySortedMatches = myMatches.entries.sortedBy { it.value }.take(2)
        val myfirstKeys = mySortedMatches.map { it.key }
        val otherFirstKeys = otherMatches.entries.sortedBy { it.value }.take(2).map { it.key }

        val myCoord = findMatchingCoord(myfirstKeys, allBeacons)
        val mySecondCoord = allBeacons[myfirstKeys.first()
            .split('_').map { it.toInt() }.first { it != myCoord.first }]

        val otherCoord = findMatchingCoord(otherFirstKeys, other.allBeacons)
        val otherSecondCoord = other.allBeacons[otherFirstKeys.first()
            .split('_').map { it.toInt() }.first { it != otherCoord.first }]

        val scannerToBeacon = listOf(myCoord.second, mySecondCoord).map { it.distanceTo(position) }
        position = otherCoord.second + myCoord.second.opposite()

        while (position.distanceTo(otherCoord.second) != scannerToBeacon[0]
            || position.distanceTo(otherSecondCoord) != scannerToBeacon[1]
        ) {
            rotatePositiveYAxis()
            position = otherCoord.second + myCoord.second.reorient().opposite()
        }

        // position current scanner based on coord match
        allBeacons = allBeacons.map { it.reorient() + position }.toMutableList()
    }

    // todo: test flipping 2d axes next! (z rotation?)
    fun rotatePositiveYAxis() {
        orientationShift += Coord(0, 1)
    }

    fun Coord.reorient(): Coord {
        val c = Coord(this.x, this.y)
        for (i in 1..orientationShift.y) {
            val temp = c.x
            c.x = c.y
            c.y = temp
            c.x *= -1
        }
        return c
    }

    fun findMatchingCoord(keys: List<String>, beaconList: List<Coord>): Pair<Int, Coord> {
//    fun findMatchingCoord(keys: List<String>, beaconList: List<Coord3d>): Pair<Int, Coord3d> {
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
