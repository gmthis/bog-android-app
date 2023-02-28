package cn.xd.bogr.util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat
import java.util.*

object DateSerializable: KSerializer<Date> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("java.util.Date")
    private val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

    override fun deserialize(decoder: Decoder): Date {
        return format.parse(decoder.decodeString())!!
    }

    override fun serialize(encoder: Encoder, value: Date) {
        encoder.encodeString(format.format(value))
    }
}