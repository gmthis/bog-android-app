package cn.xd.bog.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForumContent(
    @SerialName("code")
    val code: Int,
    @SerialName("info")
    val info: List<ForumContentInfo>,
    @SerialName("type")
    val type: String
)

fun emptyForumContent(code: Int? = null, type:String? = null) = ForumContent(
    code = code ?: 6010,
    info = emptyList(),
    type = type ?: "TimeOut"
)