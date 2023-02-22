package cn.xd.bogr.util

import org.jsoup.Jsoup
import org.jsoup.nodes.Attributes

val HTMLEscapeMap = buildMap {
    this["&gt;"] = ">"
    this["&lt;"] = "<"
    this["&amp;"] = "&"
    this["&quot;"] = "\""
    this[";"] = ";"
}

fun htmlUnescape(s: String): String{
    if (s.trim().isEmpty()) return s

    val stringBuilder = StringBuilder()
    val escape = StringBuilder()
    var isEscape = false

    for (c in s) {
        when(c){
            '&' -> {
                escape.append(c)
                isEscape = true
            }
            ';' -> {
                escape.append(c)
                stringBuilder.append(HTMLEscapeMap[escape.toString()])
                escape.clear()
                isEscape = false
            }
            else -> {
                if (isEscape){
                    escape.append(c)
                }else{
                    stringBuilder.append(c)
                }
            }
        }
    }
    return stringBuilder.toString()
}

fun htmlSegmentation(s: String): List<String>{
    if (s.trim().isEmpty()) return emptyList()

    val list = mutableListOf<String>()
    val stringBuilder = StringBuilder()
    val escape = StringBuilder()
    var isEscape = false

    for (c in s) {
        when(c){
            '<' -> {
                escape.append(c)
                isEscape = true
            }
            '>' -> {
                escape.append(c)
                val toString = escape.toString()
                if (toString == "<br />"){
                    list.add(stringBuilder.toString())
                    stringBuilder.clear()
                }else{
                    stringBuilder.append(toString).toString()
                }
                escape.clear()
                isEscape = false
            }
            else -> {
                if (isEscape){
                    escape.append(c)
                }else{
                    stringBuilder.append(c)
                }
            }
        }
    }
    if (stringBuilder.isNotEmpty() || escape.isNotEmpty()){
        list.add(stringBuilder.append(escape).toString())
    }
    return list
}

data class Paragraph(
    val content: String,
    var htmlLabel: MutableList<HTMLLabel>? = null
)

data class HTMLLabel(
    val content: String,
    val start: Int,
    val labelName: String,
    val attr: Attributes? = null
)

fun htmlLabelExtract(s: String): Paragraph {
    val paragraphs = Paragraph(s)

    val labelPrimitive = mutableListOf<Pair<String, Int>>()
    val labelCache = StringBuilder()
    var isLabel = false

    for (i in s.indices) {
        when(val c = s[i]){
            '<' -> {
                labelCache.append(c)
                isLabel = true
            }
            '>' -> {
                labelCache.append(c)
                labelPrimitive += labelCache.toString() to (i + 1) - labelCache.length
                labelCache.clear()
                isLabel = false
            }
            else -> {
                if (isLabel){
                    labelCache.append(c)
                }
            }
        }
    }
    if (labelPrimitive.isNotEmpty()){
        paragraphs.htmlLabel = mutableListOf()
        paragraphs.htmlLabel!! +=
            htmlLabelsPrimitiveParse(
                labelPrimitive
                    .filter {
                        !(it.first.getOrNull(1) == 'i' || it.first.getOrNull(2) == 'i')
                    },
                s
            )
    }

    return paragraphs
}

fun htmlLabelsPrimitiveParse(
    labelPrimitive: List<Pair<String, Int>>,
    s: String
): List<HTMLLabel>{
    val result = mutableListOf<HTMLLabel>()
    var count = 0
    var start = 0

    for (pair in labelPrimitive) {
        if (count == 0){
            start = pair.second
            count = 1
        }else{
            val end = pair.second + pair.first.length
            val substring = s.substring(start, end)
            result += htmlLabelPrimitiveParse(substring, start)
            count = 2
        }
        if (count == 2){
            count = 0
            start = 0
        }

    }

    return result
}

fun htmlLabelPrimitiveParse(s: String, start: Int): HTMLLabel{
    val parse = Jsoup.parse(s).getElementsByIndexEquals(1)[0].getElementsByIndexEquals(0)[0]
    return HTMLLabel(s, labelName = parse.tagName(), start = start, attr = parse.attributes())
}