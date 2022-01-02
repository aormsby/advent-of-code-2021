package d19_BeaconScanner

import util.Coord3d
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

    Output.part(1, "Unique Beacons", scannerList.flatMap { it.allBeacons }.toSet().size)
    Output.part(2, "Largest Scanner Distance", getLargestScannerDistance(scannerList))
    Output.executionTime(startTime)
}

fun getLargestScannerDistance(scanners: List<Scanner>): Int {
    val distMap = mutableMapOf<String, Int>()

    scanners.forEachIndexed { i, s1 ->
        scanners.forEachIndexed { j, s2 ->
            val k = listOf(i, j).sorted().joinToString("_")
            if (i != j && k !in distMap) distMap[k] = s1.position.manhattanDistanceTo(s2.position)
        }
    }

    return distMap.maxOf { it.value }
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
                    curScanner.allBeacons.add(Coord3d(this[0], this[1], this[2]))
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
    var position: Coord3d = Coord3d(0, 0, 0)
    var allBeacons: MutableList<Coord3d> = mutableListOf()
    var orientation: Coord3d = Coord3d(0, 0, 0)
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
        val allMyMatches = beaconDistances.filterValues { it in other.beaconDistances.values }
        val uniqueMatches = allMyMatches.keys.flatMap { k ->
            k.split('_').map { s -> s.toInt() }
        }.toSet()

        if (uniqueMatches.size >= 12) {
            val otherMatches = other.beaconDistances.filterValues { it in allMyMatches.values }
            orientScanner(
                allMyMatches.entries.sortedBy { it.value },
                otherMatches.entries.sortedBy { it.value },
                other
            )
        }
    }

    fun orientScanner(
        myMatches: List<Map.Entry<String, Float>>,
        otherMatches: List<Map.Entry<String, Float>>,
        other: Scanner
    ) {
        // region key setup
        val myKeyIndexList = mutableListOf(myMatches.first())
        myKeyIndexList.add(myMatches.first {
            it.key.contains(myKeyIndexList.first().key.substringAfter('_')) &&
                    it.key.substringAfter('_').length == myKeyIndexList.first().key.substringAfter('_').length &&
                    it != myKeyIndexList[0]
        })
        val myFirstKeys = myKeyIndexList.map { it.key }

        val otherKeyIndexList = mutableListOf(otherMatches.first())
        otherKeyIndexList.add(otherMatches.first { it.value == myKeyIndexList.last().value })
        val otherFirstKeys = otherKeyIndexList.map { it.key }
        // endregion

        // region coord setup
        val myCoordPair = findMatchingCoord(myFirstKeys, allBeacons)
        val myCoord = myCoordPair.second
        val mySecondCoord = allBeacons[myFirstKeys.first()
            .split('_').map { it.toInt() }.first { it != myCoordPair.first }]

        val otherCoordPair = findMatchingCoord(otherFirstKeys, other.allBeacons)
        val otherCoord = otherCoordPair.second
        val otherSecondCoord = other.allBeacons[otherFirstKeys.first()
            .split('_').map { it.toInt() }.first { it != otherCoordPair.first }]
        // endregion

        // region orient and match
        val myDupe = Coord3d(myCoord.x, myCoord.y, myCoord.z)
        val mySecondDupe = Coord3d(mySecondCoord.x, mySecondCoord.y, mySecondCoord.z)
        position = otherCoord + myDupe.opposite()

        while (position + mySecondDupe != otherSecondCoord && orientation.x < 4) {
            listOf(myDupe, mySecondDupe).orientToNext()
            position = otherCoord + myDupe.opposite()
        }
        // endregion

        // position current scanner and beacons based on coord match
        if (orientation.x < 4) {
            allBeacons = allBeacons.map { it.reorient() + position }.toMutableList()
            hasShifted = true
        } else {
            position = Coord3d(0, 0, 0)
            orientation = Coord3d(0, 0, 0)
        }
    }

    fun List<Coord3d>.orientToNext() {
        var nextRot = Coord3d(orientation.x, orientation.y, orientation.z)
        updateScannerOrientation()
        nextRot = nextRot.diffWith(orientation)

        if (nextRot.x == -3) nextRot.x = 1
        if (nextRot.y == -3) nextRot.y = 1
        if (nextRot.z == -3) nextRot.z = 1

        forEach { it.rotate(nextRot) }
    }

    fun updateScannerOrientation() {
        orientation.z += 1

        if (orientation.z > 3) {
            orientation.y += 1
            orientation.z = 0
        }

        if (orientation.y > 3) {
            orientation.x += 1
            orientation.y = 0
        }
    }

    fun Coord3d.reorient(): Coord3d {
        val o = Coord3d(orientation.x, orientation.y, orientation.z)

        while (o.x > 0) {
            o.x--
            this.rotate(Coord3d(1, 0, 0))
        }

        while (o.y > 0) {
            o.y--
            this.rotate(Coord3d(0, 1, 0))
        }

        while (o.z > 0) {
            o.z--
            this.rotate(Coord3d(0, 0, 1))
        }

        return this
    }

    fun Coord3d.rotate(rotation: Coord3d) {
        if (rotation.z == 1) {
            val temp = y
            y = x
            x = temp
            y *= -1
        }

        if (rotation.y == 1) {
            val temp = x
            x = z
            z = temp
            x *= -1
        }

        if (rotation.x == 1) {
            val temp = z
            z = y
            y = temp
            z *= -1
        }
    }

    fun findMatchingCoord(keys: List<String>, beaconList: List<Coord3d>): Pair<Int, Coord3d> {
        val indices = keys.flatMap { it.split('_') }.map { it.toInt() }
        val coords = indices.map { beaconList[it] }

        return Pair(
            indices.groupingBy { it }.eachCount().filter { it.value > 1 }.keys.first(),
            coords.groupingBy { it }.eachCount().filter { it.value > 1 }.keys.first(),
        )
    }
}