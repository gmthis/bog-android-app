package cn.xd.bog.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Content(
    @SerialName("code")
    val code: Int,
    @SerialName("info")
    val info: ContentInfo,
    @SerialName("type")
    val type: String
)

@Serializable
data class ContentInfo(
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