package util

object Output {
    fun day(i: Int, s: String) {
        println("\n*** Day $i: $s ***")
    }

    fun part(i: Int, s: String, result: Any) {
        println("Part $i -> $s = $result")
    }

    fun startTime(): Long = System.currentTimeMillis()

    fun executionTime(startTime: Long, label: String = "execution time") {
        println("----- $label -> ${System.currentTimeMillis() - startTime} milliseconds")
    }
}