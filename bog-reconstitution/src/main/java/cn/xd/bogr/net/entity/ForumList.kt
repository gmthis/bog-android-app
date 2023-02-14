package cn.xd.bogr.net.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 所有板块,由[cn.xd.bogr.net.ApiPath.ForumList]的请求返回
 *
 * @property code 状态码,6001为成功
 * @property type 忘记了
 * @property info [ForumListItem]
 */
@Serializable
data class ForumList(
    @SerialName("code")
    val code: Int,
    @SerialName("type")
    val type: String,
    @SerialName("info")
    val info: List<ForumListItem>,
)

val requestForumListTimeOut = ForumList(
    code = -1,
    type = "timeout",
    info = emptyList()
)

/**
 * 板块信息项,由[ForumList](cn.xd.bogr.net.ApiPath.ForumList)返回的[info](ForumList.info)携带
 *
 * @property id 板块ID
 * @property rank 板块默认排序,越小越靠前,可不遵循
 * @property name 板块名
 * @property name2 别名,一般没用
 * @property headImg 头图,需要时请求/static/header/+[headImg]
 * @property info 板块描述
 * @property isHide 是否隐藏
 * @property isTimeline 是否会出现在时间线中
 * @property isClose 是否为禁止发布内容的板块
 */
@Serializable
data class ForumListItem(
    @SerialName("id")
    val id: Int,
    @SerialName("rank")
    val rank: Int,
    @SerialName("name")
    val name: String,
    @SerialName("name2")
    val name2: String,
    @SerialName("headimg")
    val headImg: String,
    @SerialName("info")
    val info: String,
    @SerialName("hide")
    val isHide: Boolean,
    @SerialName("timeline")
    val isTimeline: Boolean,
    @SerialName("close")
    val isClose: Boolean,
)