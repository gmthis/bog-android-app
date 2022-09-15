package cn.xd.bog

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        var unescape = "123<b>123</b>1234"
        println(unescape.indexOf("<b>"))
        println(unescape.indexOf("</b>"))
        if (unescape.indexOf("<b>") != -1){
            unescape = unescape.replace(
                unescape.substring(
                    unescape.indexOf("<b>"),
                    unescape.indexOf("</b>") + 4
                ),
                "🎲 " + unescape.substring(
                    unescape.indexOf("<b>") + 3,
                    unescape.indexOf("</b>")
                )
            )
        }
        println(unescape)
    }
}