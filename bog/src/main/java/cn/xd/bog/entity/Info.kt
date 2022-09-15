package cn.xd.bog.entity

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Info {
    @SerialName("admin")
    val admin: Int?
    @SerialName("content")
    val content: String
    @SerialName("cookie")
    val cookie: String
    @SerialName("emoji")
    val emoji: String
    @SerialName("forum")
    val forum: Int?
    @SerialName("id")
    val id: Int
    @SerialName("images")
    val images: List<Image>?
    @SerialName("lock")
    val lock: Int?
    @SerialName("name")
    val name: String
    @SerialName("res")
    val res: Int
    @SerialName("time")
    val time: Long
    @SerialName("title")
    val title: String?

    var quote: MutableMap<String, MutableState<SingleContentInfo?>>?
    var quoteIsOpen: MutableMap<String, MutableState<Boolean>>?
}