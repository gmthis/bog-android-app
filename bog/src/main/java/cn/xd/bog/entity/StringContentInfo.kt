package cn.xd.bog.entity


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StringContentInfo(
    @SerialName("admin")
    override val admin: Int?,
    @SerialName("content")
    override val content: String,
    @SerialName("cookie")
    override val cookie: String,
    @SerialName("emoji")
    override val emoji: String,
    @SerialName("forum")
    override val forum: Int,
    @SerialName("id")
    override val id: Int,
    @SerialName("images")
    override val images: List<Image>?,
    @SerialName("lock")
    override val lock: Int?,
    @SerialName("name")
    override val name: String,
    @SerialName("reply")
    var reply: MutableList<Reply>,
    @SerialName("reply_count")
    val replyCount: Int,
    @SerialName("res")
    override val res: Int,
    @SerialName("root")
    val root: String,
    @SerialName("time")
    override val time: Long,
    @SerialName("title")
    override val title: String
): Info{
    override var quote: MutableMap<String, MutableState<SingleContentInfo?>>? = null
    override var quoteIsOpen: MutableMap<String, MutableState<Boolean>>? = null
}