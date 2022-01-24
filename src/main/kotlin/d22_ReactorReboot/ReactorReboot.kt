package d22_ReactorReboot

import util.Input
import util.Output

fun main() {
    Output.day(22, "Reactor Reboot")
    val startTime = Output.startTime()

    val instructions = Input.parseLines(filename = "/input/d22_reboot_steps.txt")
        .map { Cuboid.of(it) } as MutableList
    val initializationLimits = Cuboid(true, -50..50, -50..50, -50..50)
    var cuboids = mutableListOf<Cuboid>()

    instructions.filter { it.intersectsWith(initializationLimits) }.forEach { c ->
        cuboids.addAll(cuboids.mapNotNull { it.intersectOf(c) })
        if (c.isOn) cuboids.add(c)
    }

    Output.part(1, "Initialization: Active Cubes", cuboids.sumOf { it.volume() })

    cuboids = mutableListOf<Cuboid>()
    instructions.forEach { c ->
        cuboids.addAll(cuboids.mapNotNull { it.intersectOf(c) })
        if (c.isOn) cuboids.add(c)
    }

    Output.part(2, "Reboot: Active Cubes", cuboids.sumOf { it.volume() })
    Output.executionTime(startTime)
}

class Cuboid(
    val isOn: Boolean,
    private val x: IntRange,
    private val y: IntRange,
    private val z: IntRange
) {
    companion object {
        private val pattern =
            """^(on|off) x=(-?\d+)\.\.(-?\d+),y=(-?\d+)\.\.(-?\d+),z=(-?\d+)\.\.(-?\d+)$""".toRegex()

        fun of(input: String): Cuboid {
            val (on, x1, x2, y1, y2, z1, z2) = pattern.matchEntire(input)?.destructured
                ?: error("Cannot parse input: $input")
            return Cuboid(
                on == "on",
                x1.toInt()..x2.toInt(),
                y1.toInt()..y2.toInt(),
                z1.toInt()..z2.toInt()
            )
        }
    }

    fun intersectsWith(other: Cuboid): Boolean =
        x.intersectsWith(other.x) && y.intersectsWith(other.y) && z.intersectsWith(other.z)

    fun intersectOf(other: Cuboid): Cuboid? =
        if (!intersectsWith(other)) null
        else Cuboid(!isOn, x.intersectOf(other.x), y.intersectOf(other.y), z.intersectOf(other.z))

    fun volume(): Long =
        (x.size().toLong() * y.size().toLong() * z.size().toLong()) * if (isOn) 1 else -1
}

fun IntRange.intersectsWith(other: IntRange): Boolean =
    first <= other.last && last >= other.first

fun IntRange.intersectOf(other: IntRange): IntRange =
    maxOf(first, other.first)..minOf(last, other.last)

fun IntRange.size(): Int =
    last - first + 1