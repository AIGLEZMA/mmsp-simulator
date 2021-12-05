package me.aiglez.mmspsimulator

fun readLineWithDefault(default: String): String {
    val line = readLine()
    return if (line != null && line.isNotBlank()) line else default
}