package cn.xd.bogr.net.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 请求一个内容返回的对象
 *
 * @property code 状态码
 * @property info 内容
 * @property type 忘了
 */
@Serializable
data class SingleContent(
    @SerialName("code")
    val code: Int,
    @SerialName("info")
    val info: Single,
    @SerialName("type")
    val type: String
)

/**
 * 一个内容,没有前后文
 */
@Serializable
data class Single(
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
    @SerialName("res")
    override val res: Int,
    @SerialName("time")
    override val time: Long,
    @SerialName("title")
    override val title: String
): Content