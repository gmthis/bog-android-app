package cn.xd.test.entity


import cn.hutool.core.lang.mutable.Mutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Info(
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
    var reply: MutableList<Reply>,
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

fun Info.toReply(): Reply = Reply(
    admin = admin,
    content = content,
    cookie = cookie,
    emoji = emoji,
    id = id,
    images = images,
    name = name,
    res = res,
    time = time
)