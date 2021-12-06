package util

object Output {
    fun day(i: Int, s: String) {
        println("\n*** Day $i: $s ***")
    }

    fun part(i: Int, s: String, result: Any) {
        println("Part $i -> $s = $result")
    }

    private var startTime = 0L
    fun setStartTime() {
        startTime = System.currentTimeMillis()
    }

    fun executionTime() {
        println("execution time -> ${System.currentTimeMillis() - startTime} milliseconds")
    }
}