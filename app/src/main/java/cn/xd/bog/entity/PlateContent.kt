package cn.xd.bog.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlateContent(
    @SerialName("code")
    val code: Int,
    @SerialName("info")
    val info: List<PlateContentInfo>,
    @SerialName("type")
    val type: String
)

@Serializable
data class PlateContentInfo(
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
    @SerialName("hide_count")
    val hideCount: Int,
    @SerialName("id")
    val id: Int,
    @SerialName("images")
    val images: List<Image>?,
    @SerialName("lock")
    val lock: Int?,
    @SerialName("name")
    val name: String,
    @SerialName("reply")
    val reply: List<Reply>,
    @SerialName("reply_count")
    val replyCount: Int,
    @SerialName("res")
    val res: Int,
    @SerialName("root")
    val root: String,
    @SerialName("time")
    val time: Long,
    @SerialName("title")
    val title: String
)

@Serializable
data class Image(
    @SerialName("ext")
    val ext: String?,
    @SerialName("url")
    val url: String
)

@Serializable
data class Reply(
    @SerialName("admin")
    val admin: Int?,
    @SerialName("content")
    val content: String,
    @SerialName("cookie")
    val cookie: String,
    @SerialName("emoji")
    val emoji: String,
    @SerialName("id")
    val id: Int,
    @SerialName("images")
    val images: List<Image>?,
    @SerialName("name")
    val name: String,
    @SerialName("res")
    val res: Int,
    @SerialName("time")
    val time: Long
)