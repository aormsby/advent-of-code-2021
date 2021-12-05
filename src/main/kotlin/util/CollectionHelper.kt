package util

object CollectionHelper {
    inline fun <reified T> transposeList(list: List<List<T>>): List<MutableList<T>> {
        val transposed = mutableListOf<MutableList<T>>()

        list.forEach { line ->
            line.forEachIndexed { i, value ->
                if (transposed.size <= i) transposed.add(i, mutableListOf(value))
                else transposed[i].add(value)
            }
        }

        return transposed
    }
}