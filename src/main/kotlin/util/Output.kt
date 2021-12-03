package util

object Output {
    fun day(i: Int, s: String) {
        println("\n*** Day $i: $s ***")
    }

    fun part(i: Int, s: String, result: Any) {
        println("Part $i -> $s = $result")
    }
}