package cn.xd.bogr.util

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

val formatter =
    SimpleDateFormat("yy-MM-dd HH:mm", Locale.CHINA)

private const val minute = 60 * 1000
private const val hour = 60 * minute
private const val day = 24 * hour
private const val sevenDay = 7 * day

fun Long.getTimeDifference(): String{

    val timeCache = Date().time - this
    return when{
        timeCache > sevenDay -> {
            formatter.format(Date(this))
        }
        timeCache > day -> {
            "${timeCache / day}天前"
        }
        timeCache > hour -> {
            "${timeCache / hour}小时前"
        }
        timeCache > minute -> {
            "${timeCache / minute}分钟前"
        }
        else -> "刚刚"
    }
}