package cn.xd.bog.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForumInfo(
    @SerialName("close")
    val close: Boolean,
    @SerialName("headimg")
    val headimg: String,
    @SerialName("hide")
    val hide: Boolean,
    @SerialName("id")
    val id: Int,
    @SerialName("info")
    val info: String,
    @SerialName("name")
    val name: String,
    @SerialName("name2")
    val name2: String,
    @SerialName("rank")
    val rank: Int,
    @SerialName("timeline")
    val timeline: Boolean
)