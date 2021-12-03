package util

import java.io.IOException

object InputHelper {
    fun parseLines(filename: String): List<String> =
        this.javaClass.getResourceAsStream(filename)?.bufferedReader()?.readLines()
            ?: throw IOException("read input failed")

    inline fun <reified T> parseLinesTo(filename: String): List<T> =
        parseLines(filename).map {
            when (T::class) {
                Int::class -> it.toInt() as T
                else -> it as T     //String
            }
        }

    inline fun <reified T, reified R> parseToPairList(filename: String, separator: String = " "): List<Pair<T, R>> =
        parseLines(filename).map {
            val l = it.split(separator)
            Pair(
                when (T::class) {
                    Int::class -> l[0].toInt() as T
                    else -> l[0] as T   // String
                },
                when (R::class) {
                    Int::class -> l[1].toInt() as R
                    else -> l[1] as R   // String
                }
            )
        }
}