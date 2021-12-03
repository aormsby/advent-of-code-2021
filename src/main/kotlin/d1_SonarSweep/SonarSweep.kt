package d1_SonarSweep

import util.Input
import util.Output

fun main() {
    Output.day(1, "Sonar Sweep")

    val depthList = Input.parseLinesTo<Int>("/input/d1_sea_floor_depths.txt")

    Output.part(1, "Deepenings", depthList.twoWindowSlide())
    Output.part(2, "Sliding Deepenings", depthList.threeWindowSlide())
}

fun List<Int>.twoWindowSlide(): Int =
    this.windowed(2) {
        it[1] > it[0]
    }.count { x -> x }  // where x is true

fun List<Int>.threeWindowSlide(): Int =
    this.windowed(3) { it.sum() }.twoWindowSlide()
