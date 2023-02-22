package cn.xd.bogr.net.entity

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 一个板块的一次请求返回的内容
 *
 * @property code 状态码
 * @property info 内容
 * @property type 忘了
 */
@Serializable
data class Forum(
    @SerialName("code")
    val code: Int,
    @SerialName("info")
    val info: List<Strand>,
    @SerialName("type")
    val type: String
)

val requestForumTimeOut = Forum(
    code = -1,
    type = "timeout",
    info = emptyList()
)

/**
 * 请求某个串的详细信息时返回的对象
 *
 * @property code 状态码
 * @property info 内容
 * @property type 忘了
 * @constructor Create empty String content
 */
@Serializable
data class StrandContent(
    @SerialName("code")
    val code: Int,
    @SerialName("info")
    val info: Strand,
    @SerialName("type")
    val type: String,
)

val errorStrand = Strand(-1, -1, -1, "error", "error", "error", null, null, "error", null, null, -1, null, emptyList(), -1, "error")


val requestStrandTimeOut = StrandContent(
    code = -1,
    type = "timeout",
    info = errorStrand
)
/**
 * 一个串,包括其回复
 *
 * @property forum 所属板块ID
 * @property hidCount 隐藏计数,即[replyCount] - 5
 * @property reply 回复,如果不是详情页则是最后五条
 * @property replyCount 回复数量
 * @property root 最后被回复的时间
 */
@Serializable
data class Strand(
    @SerialName("id")
    override val id: Int,
    @SerialName("res")
    override val res: Int,
    @SerialName("time")
    override val time: Long,
    @SerialName("name")
    override val name: String,
    @SerialName("emoji")
    override val emoji: String,
    @SerialName("cookie")
    override val cookie: String,
    @SerialName("admin")
    override val admin: Int?,
    @SerialName("title")
    override val title: String?,
    @SerialName("content")
    override val content: String,
    @SerialName("lock")
    override val lock: Int?,
    @SerialName("images")
    override val images: List<Image>?,
    @SerialName("forum")
    val forum: Int,
    @SerialName("hide_count")
    val hidCount: Int?,
    @SerialName("reply")
    val reply: List<Reply>,
    @SerialName("reply_count")
    val replyCount: Int,
    @SerialName("root")
    val root: String,
): Content{
    override val cite: MutableMap<String, MutableState<Content?>> by lazy {
        mutableStateMapOf()
    }
    override val citeIsOpen: MutableMap<String, MutableState<Boolean>> by lazy {
        mutableStateMapOf()
    }
}

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
): Content {
    override val lock: Int?
        get() = null
    override val cite: MutableMap<String, MutableState<Content?>> by lazy {
        mutableStateMapOf()
    }
    override val citeIsOpen: MutableMap<String, MutableState<Boolean>> by lazy {
        mutableStateMapOf()
    }
    override val title: String?
        get() = null
}