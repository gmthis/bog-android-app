package cn.xd.test.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TestData(
    @SerialName("code")
    val code: Int,
    @SerialName("info")
    val info: Info,
    @SerialName("type")
    val type: String
)