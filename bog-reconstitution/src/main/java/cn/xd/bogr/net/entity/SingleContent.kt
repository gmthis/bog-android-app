package cn.xd.bogr.net.entity

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
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

val errorSingle = Single(
    -1, "error", "error", "error", -1, null, null, "error", -1, -1, "error", null
)

val requestSingleContentTimeOut = SingleContent(
    code = -1,
    type = "timeout",
    info = errorSingle
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
    override val title: String,
    @SerialName("forum")
    val forum: Int?,
): Content{
    override val cite: MutableMap<String, MutableState<Content?>> by lazy {
        mutableStateMapOf()
    }
    override val citeIsOpen: MutableMap<String, MutableState<Boolean>> by lazy {
        mutableStateMapOf()
    }
}