package d6_Lanternfish

import util.Input
import util.Output

fun main() {
    Output.day(6, "Lanternfish")
    Output.setStartTime()

    // get number of times each day occurs in input
    val initialState = with(Input.parseLines("/input/d6_lanternfish_initial_state.txt")) {
        Input.parseToListOf<Int>(rawData = this[0], delimiter = ",").groupingBy { it }.eachCount()
    }

    // map of days left to amount of fish spawning
    // note: Long is required because these numbers get BIG
    val spawnPool = mutableMapOf(
        0 to 0L, 1 to 0L, 2 to 0L, 3 to 0L, 4 to 0L,
        5 to 0L, 6 to 0L, 7 to 0L, 8 to 0L
    )

    // init spawn pool with input
    initialState.forEach {
        spawnPool[it.key] = it.value.toLong()
    }

    val firstCountDay = 80
    var earlySpawnCount = 0L

    val numSpawningDays = 256
    for (i in 1..numSpawningDays) {
        // separate 0s from the pack
        val zeroDayFish = spawnPool[0]!!

        // shift all fish down one day
        spawnPool.keys.forEach {
            if (it != 0)
                spawnPool[it - 1] = spawnPool[it]!!
        }

        // add 0s into 8s (new spawn)
        spawnPool[8] = zeroDayFish

        // merge 0s into 6s (resets 0s)
        spawnPool.merge(6, zeroDayFish) { a, b -> a + b }

        if (i == firstCountDay) earlySpawnCount = spawnPool.values.sum()
    }

    Output.part(1, "80-day Spawn", earlySpawnCount)
    Output.part(1, "256-day Spawn", spawnPool.values.sum())
    Output.executionTime()
}