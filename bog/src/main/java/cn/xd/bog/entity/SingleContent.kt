package cn.xd.bog.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SingleContent(
    @SerialName("code")
    val code: Int,
    @SerialName("info")
    val info: SingleContentInfo,
    @SerialName("type")
    val type: String
)