package util

import java.io.IOException

object Input {
    fun parseAllText(filename: String): String =
        this.javaClass.getResourceAsStream(filename)?.bufferedReader()?.readText()
            ?: throw IOException("read input failed")

    fun parseLines(filename: String): List<String> =
        this.javaClass.getResourceAsStream(filename)?.bufferedReader()?.readLines()
            ?: throw IOException("read input failed")

    inline fun <reified T> parseToListOf(
        filename: String? = null,
        rawData: String? = null,
        delimiter: String = ""
    ): List<T> =
        filename?.let {
            parseLines(filename).map {
                when (T::class) {
                    Int::class -> it.toInt() as T
                    else -> it as T     //String
                }
            }
        } ?: rawData?.let {
            it.split(delimiter).filter { s -> s.isNotBlank() }.map { str ->
                when (T::class) {
                    Int::class -> str.toInt() as T
                    else -> it as T     //String
                }
            }
        } ?: throw IllegalArgumentException("no param provided for parse")

    inline fun <reified T, reified R> parseToPairList(
        filename: String,
        pairDelimiter: String = "",
        itemDelimiter: String = ""
    ): List<Pair<T, R>> =
        parseLines(filename).map {
            val l = it.split(pairDelimiter)
            Pair(
                when (T::class) {
                    Int::class -> l[0].toInt() as T
                    Coord::class -> parseToCoord(l[0], delimiter = itemDelimiter) as T
                    else -> l[0] as T   // String
                },
                when (R::class) {
                    Int::class -> l[1].toInt() as R
                    Coord::class -> parseToCoord(l[1], delimiter = itemDelimiter) as R
                    else -> l[1] as R   // String
                }
            )
        }

    /**
     * Returns[Coord] from String
     * @param c current coordinate to parse
     */
    fun parseToCoord(c: String, delimiter: String = ""): Coord =
        with(c.split(delimiter)) {
            Coord(x = this[0].toInt(), y = this[1].toInt())
        }
}