package cn.xd.bogr.net.entity

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateMapOf
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 所有内容抽象出来的接口,包含了一个内容的基本信息
 *
 * @property id 串号
 * @property res 回复目标,0则表示其为主内容
 * @property time 内容发布的时间
 * @property name 昵称,绝大数情况下是空的
 * @property emoji 饼干默认的emoji代码
 * @property cookie 饼干号
 * @property admin 是否为管理员,0不是,1是
 * @property title 标题,绝大多数情况下是没有标题的
 * @property content 内容
 * @property lock 是否被锁定,如果被锁定则无法被回复
 * @property images 内容中附有的图片
 */
@Serializable
sealed interface Content {
    @SerialName("id")
    val id: Int
    @SerialName("res")
    val res: Int
    @SerialName("time")
    val time: Long
    @SerialName("name")
    val name: String
    @SerialName("emoji")
    val emoji: String
    @SerialName("cookie")
    val cookie: String
    @SerialName("admin")
    val admin: Int?
    @SerialName("title")
    val title: String?
    @SerialName("content")
    val content: String
    @SerialName("lock")
    val lock: Int?
    @SerialName("images")
    val images: List<Image>?

    val cite: MutableMap<String, MutableState<Content?>>
    val citeIsOpen: MutableMap<String, MutableState<Boolean>>
}

/**
 * 表示一个远程图片文件
 *
 * @property ext 扩展名,包括点
 * @property url url 图片名
 */
@Serializable
data class Image(
    @SerialName("ext")
    val ext: String?,
    @SerialName("url")
    val url: String
){
    /**
     * 返回缩略图的请求地址
     * @return [String] 缩略图的请求地址
     */
    fun thumbnail() = if (ext != null)
        "http://bog.ac/image/thumb/$url$ext"
    else {
        "http://bog.ac/image/thumb/nodata.jpg"
    }

    /**
     * 返回大图的请求地址
     * @return [String] 大图的请求地址
     */
    fun largePicture() = if (ext != null)
        "http://bog.ac/image/large/$url$ext"
    else {
        "http://bog.ac/image/large/nodata.jpg"
    }

    companion object{
        val errorImage = "http://bog.ac/image/large/nodata.jpg"
    }
}