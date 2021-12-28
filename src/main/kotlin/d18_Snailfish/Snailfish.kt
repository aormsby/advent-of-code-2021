package d18_Snailfish

import util.Input
import util.Output
import kotlin.math.ceil
import kotlin.math.floor

fun main() {
    Output.day(18, "Snailfish")
    val startTime = Output.startTime()

    val homework = Input.parseLines(filename = "/input/d18_homework.txt")

    val sumMagnitude = homework
        .map { SnailfishNumber.parse(it) }
        .reduce { a, b -> a + b }
        .magnitude()

    Output.part(1, "Magnitude", sumMagnitude)

    val largestSnailAddMagnitude = homework
        .mapIndexed { i, left ->
            homework.drop(i + 1).map { right ->
                listOf(
                    SnailfishNumber.parse(left) to SnailfishNumber.parse(right),
                    SnailfishNumber.parse(right) to SnailfishNumber.parse(left)
                )
            }.flatten()
        }.flatten()
        .maxOf { (it.first + it.second).magnitude() }

    Output.part(2, "Largest Magnitude from Snail Number Pairs", largestSnailAddMagnitude)
    Output.executionTime(startTime)
}

sealed class SnailfishNumber {
    var parent: SnailfishNumber? = null
    abstract fun regularsInOrder(): List<RegularNumber>
    abstract fun regularsAsPairs(depth: Int = 0): List<NumberPairs>
    abstract fun split(): Boolean
    abstract fun magnitude(): Int

    private fun root(): SnailfishNumber =
        parent?.root() ?: this

    operator fun plus(other: SnailfishNumber) =
        PairNumber(this, other).apply { reduced() }

    fun reduced() {
        do {
            val reduced = explode() || split()
        } while (reduced)
    }

    private fun explode(): Boolean {
        root().regularsAsPairs().firstOrNull { it.depth == 4 }?.pair?.let { exploder ->
            val regulars = root().regularsInOrder()

            val leftChange = regulars.indexOfFirst { it === exploder.left } - 1
            if (leftChange >= 0)
                regulars[leftChange].value += (exploder.left as RegularNumber).value

            val rightChange = regulars.indexOfFirst { it === exploder.right } + 1
            if (rightChange < regulars.size)
                regulars[rightChange].value += (exploder.right as RegularNumber).value

            (exploder.parent as PairNumber).childHasExploded(exploder)
            return true
        } ?: return false
    }

    companion object {
        fun parse(n: String): SnailfishNumber {
            val list = mutableListOf<SnailfishNumber>()
            n.forEach { c ->
                when {
                    c.isDigit() -> list.add(RegularNumber(c.digitToInt()))
                    c == ']' -> {
                        val right = list.removeLast()
                        val left = list.removeLast()
                        list.add(PairNumber(left, right))
                    }
                }
            }
            return list.removeFirst()
        }
    }
}

data class RegularNumber(
    var value: Int
) : SnailfishNumber() {
    override fun regularsInOrder(): List<RegularNumber> = listOf(this)
    override fun regularsAsPairs(depth: Int): List<NumberPairs> = emptyList()
    override fun split(): Boolean = false
    override fun magnitude(): Int = value

    fun splitToPair(splitParent: SnailfishNumber): PairNumber =
        PairNumber(
            RegularNumber(floor(value.toDouble() / 2.0).toInt()),
            RegularNumber(ceil(value.toDouble() / 2.0).toInt())
        ).apply { this.parent = splitParent }
}

data class PairNumber(
    var left: SnailfishNumber,
    var right: SnailfishNumber
) : SnailfishNumber() {
    init {
        left.parent = this
        right.parent = this
    }

    override fun regularsInOrder(): List<RegularNumber> =
        this.left.regularsInOrder() + this.right.regularsInOrder()

    override fun regularsAsPairs(depth: Int): List<NumberPairs> =
        this.left.regularsAsPairs(depth + 1) +
                listOf(NumberPairs(depth, this)) +
                this.right.regularsAsPairs(depth + 1)

    fun childHasExploded(child: PairNumber) {
        val replacement = RegularNumber(0).apply { parent = this@PairNumber.parent }
        when {
            left === child -> left = replacement
            else -> right = replacement
        }
    }

    override fun split(): Boolean {
        if (left is RegularNumber) {
            val actualLeft = left as RegularNumber
            if (actualLeft.value >= 10) {
                left = actualLeft.splitToPair(this)
                return true
            }
        }

        val didSplit = left.split()
        if (didSplit) return true

        if (right is RegularNumber) {
            val actualRight = right as RegularNumber
            if (actualRight.value >= 10) {
                right = actualRight.splitToPair(this)
                return true
            }
        }
        return right.split()
    }

    override fun magnitude(): Int = (left.magnitude() * 3) + (right.magnitude() * 2)
}

data class NumberPairs(
    var depth: Int,
    var pair: PairNumber
)