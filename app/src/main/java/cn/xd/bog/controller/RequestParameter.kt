package cn.xd.bog.controller

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


interface RequestParameter{

    fun toMap(): Map<String, String>

    data class PlateContentRequestParameter(
        val id: Int,
        val pageIndex: Int,
        val num: Int
    ): RequestParameter {
        override fun toMap(): Map<String, String> {
            return mapOf(
                "id" to id.toString(),
                "page" to pageIndex.toString(),
                "page_def" to num.toString()
            )
        }
    }

    data class ContentRequestParameter(
        val id: Int,
        val pageIndex: Int,
        val num: Int,
        val order:Int
    ): RequestParameter{
        override fun toMap(): Map<String, String> {
            return mapOf(
                "id" to id.toString(),
                "page" to pageIndex.toString(),
                "page_def" to num.toString(),
                "order" to order.toString()
            )
        }

    }

}