package cn.xd.bogr.net.entity

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

/**
 * 一个串,包括其回复
 *
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
    @SerialName("forum")
    override val forum: Int?,
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
    @SerialName("hide_count")
    val hidCount: Int?,
    @SerialName("Reply")
    val reply: List<Reply>,
    @SerialName("reply_count")
    val replyCount: Int,
    @SerialName("root")
    val root: String,
): Content

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
    override val forum: Int?
        get() = null
    override val lock: Int?
        get() = null
    override val title: String?
        get() = null
}