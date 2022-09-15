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

@Serializable
data class SingleContentInfo(
    @SerialName("admin")
    val admin: Int?,
    @SerialName("content")
    val content: String,
    @SerialName("cookie")
    val cookie: String,
    @SerialName("emoji")
    val emoji: String,
    @SerialName("forum")
    val forum: Int,
    @SerialName("id")
    val id: Int,
    @SerialName("images")
    val images: List<Image>?,
    @SerialName("lock")
    val lock: Int?,
    @SerialName("name")
    val name: String,
    @SerialName("res")
    val res: Int,
    @SerialName("time")
    val time: Long,
    @SerialName("title")
    val title: String
)