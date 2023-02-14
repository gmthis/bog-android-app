package cn.xd.bogr.net

import cn.xd.bogr.net.entity.ForumList
import cn.xd.bogr.net.entity.requestForumListTimeOut
import cn.xd.bogr.util.json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import java.io.IOException

fun requestForumlist(): ForumList {
    val requestBody = try {
        syncRequest(ApiPath.ForumList, emptyMap())
    } catch (e: IOException){
        return requestForumListTimeOut
    }

    val jsonObject = json.parseToJsonElement(requestBody).jsonObject
    return if (jsonObject["code"].toString() != "6001"){
        ForumList(jsonObject["code"].toString().toInt(), jsonObject["type"].toString(), emptyList())
    }else{
        json.decodeFromJsonElement(jsonObject)
    }
}