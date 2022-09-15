package cn.xd.test.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Reply(
    @SerialName("admin")
    val admin: Int?,
    @SerialName("content")
    val content: String,
    @SerialName("cookie")
    val cookie: String,
    @SerialName("emoji")
    val emoji: String,
    @SerialName("id")
    val id: Int,
    @SerialName("images")
    val images: List<Image>?,
    @SerialName("name")
    val name: String,
    @SerialName("res")
    val res: Int,
    @SerialName("time")
    val time: Long
)