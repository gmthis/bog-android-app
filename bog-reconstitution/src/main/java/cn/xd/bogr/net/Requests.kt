package cn.xd.bogr.net

import cn.xd.bogr.net.entity.*
import cn.xd.bogr.util.json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import java.io.IOException

inline fun <reified V> request(
    apiPath: ApiPath,
    params: Map<String, String>,
    timeOut: V,
    errorStructure: (code: Int, type: String) -> V
): V{
    val requestBody = try {
        syncRequest(apiPath, params)
    } catch (e: IOException){
        return timeOut
    }

    val jsonObject = json.parseToJsonElement(requestBody).jsonObject
    return if (jsonObject["info"] == null){
        errorStructure(jsonObject["code"].toString().toInt(), jsonObject["type"].toString())
    }else{
        json.decodeFromJsonElement(jsonObject)
    }
}

fun requestForumlist(): ForumList = request(ApiPath.ForumList, emptyMap(),
    requestForumListTimeOut
) { code, type -> ForumList(code, type, emptyList()) }

fun requestForum(
    id: Int,
    page: Int,
    page_def: Int = 20
): Forum = request(ApiPath.Forum, mapOf(
    "id" to id.toString(),
    "page" to page.toString(),
    "page_def" to page_def.toString()
), requestForumTimeOut){code, type -> Forum(code, emptyList(), type) }

fun requestStrand(
    id: Int,
    page: Int,
    page_def: Int = 20,
    order: Int = 0
): StrandContent = request(ApiPath.Threads, mapOf(
    "id" to id.toString(),
    "page" to page.toString(),
    "page_def" to page_def.toString(),
    "order" to order.toString()
), requestStrandTimeOut){ code, type -> StrandContent(code, errorStrand, type) }

fun requestSingleContent(id: String): SingleContent = request(ApiPath.Thread, mapOf(
    "id" to id
), requestSingleContentTimeOut){code, type ->
    SingleContent(code, errorSingle, type)
}