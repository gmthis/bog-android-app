package cn.xd.bog.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Forum(
    @SerialName("code")
    val code: Int,
    @SerialName("info")
    val info: List<ForumInfo>,
    @SerialName("type")
    val type: String
)

fun emptyForum(code: Int? = null, type:String? = null) = Forum(
    code = code ?: 6010,
    info = emptyList(),
    type = type ?: "TimeOut"
)