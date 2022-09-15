package cn.xd.bog.entity


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Reply(
    @SerialName("admin")
    override val admin: Int?,
    @SerialName("content")
    override val content: String,
    @SerialName("cookie")
    override val cookie: String,
    @SerialName("emoji")
    override val emoji: String,
    @SerialName("id")
    override val id: Int,
    @SerialName("images")
    override val images: List<Image>?,
    @SerialName("name")
    override val name: String,
    @SerialName("res")
    override val res: Int,
    @SerialName("time")
    override val time: Long
): Info {
    override val forum: Int?
        get() = null
    override val lock: Int?
        get() = null
    override val title: String?
        get() = null
    override var quote: MutableMap<String, MutableState<SingleContentInfo?>>? = null
    override var quoteIsOpen: MutableMap<String, MutableState<Boolean>>? = null
}

fun Reply.toSingleContentInfo(): SingleContentInfo = SingleContentInfo(
    admin = admin,
    content = content,
    cookie = cookie,
    emoji = emoji,
    forum = 0,
    id = id,
    images = images,
    lock = null,
    name = name,
    res = res,
    time = time,
    title = ""
)