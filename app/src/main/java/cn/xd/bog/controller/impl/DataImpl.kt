package cn.xd.bog.controller.impl

import cn.xd.bog.controller.Data
import cn.xd.bog.controller.RequestParameter
import cn.xd.bog.entity.Content
import cn.xd.bog.entity.PlateContent
import cn.xd.bog.entity.PlateEntity
import cn.xd.bog.entity.SingleContent
import cn.xd.bog.util.api
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

class DataImpl : Data {
    override fun requestPlateList(): PlateEntity {
        var plateEntity: PlateEntity? = null
        api("forumlist", isSync = true) { response, e ->
            e?.printStackTrace()
            response?.run {
                val body = this.body!!.string()
                plateEntity = Json.decodeFromString<PlateEntity>(body)

            }
        }
        return plateEntity!!
    }

    override fun requestPlateContent(id: Int, pageIndex: Int, num: Int): Pair<PlateContent?, String?> {
        var plateContent: PlateContent? = null
        var errorInfo: String? = null
        api(
            "forum",
            RequestParameter.PlateContentRequestParameter(id, pageIndex, num),
            isSync = true
        ){ response, e ->
            e?.printStackTrace()
            response?.run {
                val responseBody = response.body!!.string()
                val jsonObject = Json.parseToJsonElement(responseBody).jsonObject
                if (jsonObject["code"].toString() == "6001"){
                    plateContent = Json.decodeFromString(responseBody)
                }else{
                    errorInfo = jsonObject["code"].toString()
                }

            }
        }
        return Pair(plateContent, errorInfo)
    }

    override fun requestContent(id: Int, pageIndex: Int, num: Int, order: Int): Pair<Content?, String?> {
        var content: Content? = null
        var errorInfo: String? = null
        api(
            "threads",
            RequestParameter.ContentRequestParameter(id, pageIndex, num, order),
            isSync = true
        ){ response, e ->
            e?.printStackTrace()
            response?.run {
                val responseBody = response.body!!.string()
                val jsonObject = Json.parseToJsonElement(responseBody).jsonObject
                if (jsonObject["code"].toString() == "6001"){
                    content = Json.decodeFromString(responseBody)
                }else{
                    errorInfo = jsonObject["code"].toString()
                }
            }
        }
        return Pair(content, errorInfo)
    }

    override fun requestSingle(id: Int): SingleContent {
        var content: SingleContent? = null
        api(
            "thread",
            object: RequestParameter{
                override fun toMap(): Map<String, String> {
                    return mapOf(
                        "id" to id.toString()
                    )
                }
            },
            isSync = true
        ){ response, e ->
            e?.printStackTrace()
            response?.run {
                content = Json.decodeFromString(response.body!!.string())
            }
        }
        return content!!
    }
}