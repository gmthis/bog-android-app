package cn.xd.bog.entity

import kotlinx.serialization.Serializable

@Serializable
data class PlateEntity(
    val type: String,
    val code: Int,
    val info: List<PlateInfo>
)

@Serializable
data class PlateInfo(
    val id: Int,
    val rank: Int,
    val name: String,
    val name2: String,
    val headimg: String,
    val info: String,
    val hide: Boolean,
    val timeline: Boolean,
    val close: Boolean
)