package cn.xd.bog.entity


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StringContent(
    @SerialName("code")
    val code: Int,
    @SerialName("info")
    val info: StringContentInfo?,
    @SerialName("type")
    val type: String,
){
    var filter: Boolean by mutableStateOf(false)
    var isNotEnd: Boolean by mutableStateOf(true)
    var currentPageNumber: Int = 1
    var sort: Int by mutableStateOf(0)
}

fun emptyStringContent(code: Int? = null, type:String? = null) = StringContent(
    code = code ?: 6010,
    info = null,
    type = type ?: "TimeOut"
)