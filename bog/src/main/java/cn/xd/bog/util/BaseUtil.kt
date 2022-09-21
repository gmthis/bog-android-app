package cn.xd.bog.util

operator fun String.get(start: Int, end: Int): String =
    this.substring(start, end)

operator fun String.get(intRange: IntRange): String =
    this.substring(intRange)