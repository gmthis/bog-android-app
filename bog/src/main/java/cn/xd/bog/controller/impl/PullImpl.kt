package cn.xd.bog.controller.impl

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.toMutableStateList
import cn.xd.bog.controller.Pull
import cn.xd.bog.controller.util.request
import cn.xd.bog.entity.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

val json = Json {
    ignoreUnknownKeys = true
}

class PullImpl: Pull {

    private inline fun <reified E, T: MutableState<E>> universalAsyncPull(
        apiName: String,
        parameter: Map<String, String>,
        mutableState: T,
        noinline emptyMaker: (Int?, String?) -> E,
        noinline after: (() -> Unit)?
    ){
        request(apiName, parameter){ response, e ->
            e?.run {
                printStackTrace()
                mutableState.value = emptyMaker(null, null)
            }
            response?.run {
                val string = body!!.string()
                val jsonObject = json.parseToJsonElement(string).jsonObject
                if (jsonObject["code"].toString() == "6001") {
                    mutableState.value = json.decodeFromString(string)
                }else{
                    mutableState.value = emptyMaker(
                        jsonObject["code"].toString().toInt(),
                        jsonObject["type"].toString()
                    )
                }
            }
            after?.invoke()
        }
    }

    override fun pullForum(
        forum: MutableState<Forum?>,
        after: (() -> Unit)?
    ) {
        universalAsyncPull(
            "/api/forumlist",
            emptyMap(),
            forum,
            ::emptyForum,
            after
        )
    }

    override fun pullForumContent(
        forumContent: MutableState<ForumContent?>,
        id: Int,
        after: (() -> Unit)?
    ) {
        universalAsyncPull(
            "/api/forum",
            mapOf(
                "id" to id.toString(),
                "page" to "1"
            ),
            forumContent,
            ::emptyForumContent,
            after
        )
    }

    override fun pullForumContent(
        forumContentInfoList: MutableList<ForumContentInfo>,
        id: Int,
        page: Int,
        error: ((Int, String) -> Unit),
        after: (() -> Unit)
    ){
        request("/api/forum", mapOf(
            "id" to id.toString(),
            "page" to page.toString()
        )){ response, e ->
            e?.run {
                printStackTrace()
                error(6010, "TimeOut")
            }
            response?.run {
                val string = body!!.string()
                val jsonObject = json.parseToJsonElement(string).jsonObject
                if (jsonObject["code"].toString() == "6001") {
                    forumContentInfoList += json.decodeFromString<ForumContent>(string).info
                }else{
                    error(jsonObject["code"].toString().toInt(),
                        jsonObject["type"].toString())
                }
            }
            after()
        }

    }

    override fun pullStringContent(
        stringContentMap: MutableMap<String, StringContent>,
        stringId: Int,
        page: Int,
        itemNum: Int,
        order: Int,
        after: (() -> Unit)?
    ){
        request("/api/threads", mapOf(
            "id" to stringId.toString(),
            "page" to page.toString(),
            "page_def" to itemNum.toString(),
            "order" to order.toString()
        )){ response, e ->
            e?.run {
                printStackTrace()
                stringContentMap[stringId.toString()] = emptyStringContent()
            }
            response?.run {
                val string = body!!.string()
                val jsonObject = json.parseToJsonElement(string).jsonObject
                if (jsonObject["code"].toString() == "6001") {
                    stringContentMap[stringId.toString()] = json.decodeFromString<StringContent>(string).also {
                        it.info!!.reply = it.info.reply.toMutableStateList()
                    }
                }else{
                    stringContentMap[stringId.toString()] = emptyStringContent(
                        jsonObject["code"].toString().toInt(),
                        jsonObject["type"].toString()
                    )
                }
            }
            after?.invoke()
        }
    }

    override fun pullStringContentNextPage(
        stringId: Int,
        page: Int,
        itemNum: Int,
        order: Int,
        block: (StringContent) -> Unit,
        error: (Int, String) -> Unit,
        after: (() -> Unit)?
    ) {
        request("/api/threads", mapOf(
            "id" to stringId.toString(),
            "page" to page.toString(),
            "page_def" to itemNum.toString(),
            "order" to order.toString()
        )){ response, e ->
            e?.run {
                printStackTrace()
                error(6010, "TimeOut")
            }
            response?.run {
                val string = body!!.string()
                val jsonObject = json.parseToJsonElement(string).jsonObject
                if (jsonObject["code"].toString() == "6001") {
                    block(json.decodeFromString(string))
                }else{
                    error(
                        jsonObject["code"].toString().toInt(),
                        jsonObject["type"].toString()
                    )
                }
            }
            after?.invoke()
        }
    }

    override fun pullSingleContent(
        id: String,
        block: (SingleContent) -> Unit,
        error: (Int, String) -> Unit,
        after: (() -> Unit)?
    ){
        request("/api/thread", mapOf(
            "id" to id
        )){ response, e ->
            e?.run {
                printStackTrace()
                error(6010, "TimeOut")
            }
            response?.run {
                val string = body!!.string()
                val jsonObject = json.parseToJsonElement(string).jsonObject
                if (jsonObject["code"].toString() == "6001") {
                    block(json.decodeFromString(string))
                }else{
                    error(
                        jsonObject["code"].toString().toInt(),
                        jsonObject["type"].toString()
                    )
                }
            }
            after?.invoke()
        }
    }
}