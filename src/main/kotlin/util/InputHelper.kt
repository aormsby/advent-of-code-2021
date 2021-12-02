package util

import java.io.File
import java.io.IOException
import java.nio.file.FileSystem
import java.nio.file.Paths
import kotlin.io.path.Path
import kotlin.io.path.name

object InputHelper {
    fun parseLines(filename: String): List<String> =
        this.javaClass.getResourceAsStream(filename)?.bufferedReader()?.readLines()
            ?: throw IOException("read input failed")

    fun parseLinesToInts(filename: String): List<Int> =
        parseLines(filename).map { it.toInt() }
}