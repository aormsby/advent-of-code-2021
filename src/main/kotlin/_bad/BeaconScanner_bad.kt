package _bad

import util.Coord3d
import util.Input
import util.Output

fun main() {
    val startTime = Output.startTime()
    Output.day(19, "Beacon Scanner")

    val scannerList = Input.parseLines(filename = "/input/d19_scanner_beacon_map.txt")
        .toScannerList()

    // set scanner 0 as keystone
    scannerList[0].apply {
        position = Coord3d(0, 0, 0)
        hasShifted = true
    }

    // find overlaps
    scannerList.forEach { s ->
        scannerList.forEach { s2 ->
            if (s != s2) s.findOverlapsWith(s2)
        }
    }

    println("--- scanner 0 ---")
    scannerList[0].overlapMap.forEach {
        it.value.forEach { v ->
            println("s_${it.key} -> ${scannerList[it.key].allBeacons[v]}")
//            println("s_${it.key} -> ${scannerList[it.key].allBeacons[v.first]} = ${scannerList[0].allBeacons[v.second]}")
        }
    }


//    val sharedBeacons = scannerList.flatMap { scanner ->
//        scanner.overlapMap.map { entry ->
//            entry.value.map { v ->
//                scannerList[entry.key].allBeacons[v]
//            }
//        }.flatten()
//    }.toSet()
//
//    println(sharedBeacons.size)
//    val allBeacons = scannerList.flatMap { it.allBeacons }
//    println(allBeacons.size - sharedBeacons.size)

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
    var position: Coord3d = Coord3d(-1, -1, -1),
    val orientationShift: Coord3d = Coord3d(0, 0, 0),
    var hasShifted: Boolean = false,
    val allBeacons: MutableList<Coord3d> = mutableListOf(),
    val beaconDistances: MutableMap<String, Float> = mutableMapOf(),
    // 'which' other scanner || my matching, other matching
    val overlapMap: MutableMap<Int, List<Int>> = mutableMapOf()
//    val overlapMap: MutableMap<Int, List<Pair<Int, Int>>> = mutableMapOf()
) {
    fun calculateBeaconDistances() {
        allBeacons.forEachIndexed { i, c1 ->
            allBeacons.forEachIndexed { j, c2 ->
                val k = listOf(i, j).sorted().joinToString("_")
                if (i != j && k !in beaconDistances) beaconDistances[k] = c1.distanceTo(c2)
            }
        }
    }

    fun findOverlapsWith(other: Scanner) {
//        val allDistanceMatches = mutableMapOf<String, String>()
//        beaconDistances.forEach { d ->
//            other.beaconDistances.filterValues { it == d.value }.keys.firstOrNull()?.let {
//                allDistanceMatches[d.key] = it
//            }
//        }
//
//        val alignedBeaconMap = allDistanceMatches.keys.flatMap { k -> k.split('_').map { s -> s.toInt() } }.toSet()
//            .zip(allDistanceMatches.values.flatMap { k -> k.split('_').map { s -> s.toInt() } }.toSet())

        val distanceMatches = beaconDistances.filter { it.value in other.beaconDistances.values }
        val beacons = mutableSetOf<Int>()
        beacons.addAll(distanceMatches.keys.flatMap { k -> k.split('_').map { s -> s.toInt() } })

        if (beacons.size >= 12) {
//        if (alignedBeaconMap.size >= 12) {
            overlapMap[other.which] = beacons.toList()
//            overlapMap[other.which] = alignedBeaconMap
            orientScanner()
        }
    }

    fun orientScanner() {
        if (!hasShifted) {
            // todo: implement
        }
    }
}