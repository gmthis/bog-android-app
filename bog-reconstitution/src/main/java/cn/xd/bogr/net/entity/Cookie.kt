package cn.xd.bogr.net.entity

import cn.xd.bogr.util.DateSerializable
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Cookie(
    val cookie: String,
    val token: String
){
    val complete = "${cookie}#${token}"

    var point = 0
    var exp = 0
    var signCount = 0

    @Serializable(with = DateSerializable::class) var nextSignTime: Date? = null
}

@Serializable
data class CookieResponse(
    val type: String,
    val code: Int,
    val info: CookieResponseContent
)

@Serializable
data class CookieResponseContent(
    val sign: Int,
    @Serializable(with = DateSerializable::class) val signtime: Date,
    val point: Int,
    val exp: Int
)