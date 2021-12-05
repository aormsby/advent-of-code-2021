package util

import java.io.IOException

object Input {
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
            it.split(delimiter).map { str ->
                when (T::class) {
                    Int::class -> str.toInt() as T
                    else -> it as T     //String
                }
            }
        } ?: throw IllegalArgumentException("no param provided for parse")

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

//    inline fun <reified T> parseToListsOf(filename: String, delimiter: String = ""): List<List<T>> {
//        val f = parseLines(filename)
//        println(f)
//
//        println(
//            f.map {
//                it.split(delimiter).map { i -> if (!i.isBlank()) i.toInt() else i }
//            }
//        )
////            when (val t = T::class) {
////                Int::class -> it.split(delimiter).map { i -> i.toInt() } as List<T>
////                else -> it.split(delimiter) as List<T>     //String
////            }
//        return listOf()
//    }
}