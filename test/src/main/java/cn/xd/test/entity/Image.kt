package cn.xd.test.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Image(
    @SerialName("ext")
    val ext: String,
    @SerialName("url")
    val url: String
)