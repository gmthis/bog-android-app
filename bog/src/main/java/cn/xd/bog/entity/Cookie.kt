package cn.xd.bog.entity

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class Cookie(
    val cookie: String,
    val token: String
){
    fun complete() = "$cookie#$token"

    var point: Int = 0
    var exp: Int = 0

    var signCount: Int = 0
    @Serializable(with = DateSerializable::class) var nextSignTim: Date? = null
}

object DateSerializable: KSerializer<Date>{

    private val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

    override fun deserialize(decoder: Decoder): Date {
        return format.parse(decoder.decodeString())!!
    }

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("java.util.Date")

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeString(format.format(value))
    }

}

@Serializable
data class CookieResponse(
    val type: String,
    val code: Int,
    val info: CookieResponseInfo
)

@Serializable
data class CookieResponseInfo(
    val sign: Int,
    @Serializable(with = DateSerializable::class) val signtime: Date,
    val point: Int,
    val exp: Int
)