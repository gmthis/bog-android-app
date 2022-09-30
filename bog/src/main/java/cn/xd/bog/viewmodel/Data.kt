package cn.xd.bog.viewmodel

import android.content.SharedPreferences
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import cn.xd.bog.controller.Pull
import cn.xd.bog.controller.impl.PullImpl
import cn.xd.bog.entity.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Data(private val sharedPreferences: SharedPreferences): ViewModel() {
    private val pull: Pull = PullImpl()
    private val _forum: MutableState<Forum?> = mutableStateOf(null)
    val forum: Forum?
        get() = _forum.value
    val forumMap: MutableMap<Int, String> = mutableMapOf()

    fun pullForum(id: Int, block: () -> Unit){
        pull.pullForum(_forum){
            forum!!.info.forEach{
                forumMap[it.id] = it.name
            }
            pullForumContent(forum!!.info[id].id)
            block()
        }
    }

    private val _forumContent: MutableState<ForumContent?> = mutableStateOf(null)
    val forumContent: ForumContent?
        get() = _forumContent.value
    val forumContentInfoList: MutableList<ForumContentInfo> = mutableStateListOf()

    fun pullForumContent(id: Int, loading: (() -> Unit)? = null){
        forumContentInfoList.clear()
        pull.pullForumContent(_forumContent, id){
            forumContentInfoList += forumContent!!.info
            loading?.invoke()
        }
    }

    fun pullNextForumContent(
        id: Int,
        page: Int,
        loading: () -> Unit,
        error: () -> Unit
    ){
        pull.pullForumContent(
            forumContentInfoList,
            id,
            page,
            { code, type ->
                println("code: $code")
                println("type: $type")
                error()
            }
        ) {
            loading()
        }
    }

    val stringContentMap: MutableMap<String, StringContent> = mutableStateMapOf()
    fun pullStringContent(
        stringId: Int,
        page: Int,
        itemNum: Int,
        order: Int,
        after: (() -> Unit)? = null
    ){
        pull.pullStringContent(
            stringContentMap,
            stringId,
            page,
            itemNum,
            order,
            after
        )
    }

    fun pullStringContentNextPage(
        stringId: Int,
        error: (Int, String) -> Unit,
        loading: () -> Unit,
    ){
        val stringContent = stringContentMap[stringId.toString()]!!
        pull.pullStringContentNextPage(
            stringId,
            ++(stringContent.currentPageNumber),
            20,
            stringContent.sort,
            block = {
                stringContent.info!!.reply += it.info!!.reply
            },
            error = error,
            after = loading
        )
    }

    fun pullSingleContent(
        id: String,
        container: MutableState<SingleContentInfo?>,
        error: (Int, String) -> Unit,
        after: (() -> Unit)?
    ){
        pull.pullSingleContent(
            id = id,
            block = {
                container.value = it.info
            },
            error = error,
            after = after
        )
    }

    var content by mutableStateOf(TextFieldValue())
    var nick by mutableStateOf(TextFieldValue())
    var title by mutableStateOf(TextFieldValue())
    val images = mutableStateListOf<ImageBitmap>()

    private val _cookies = mutableStateListOf<Cookie>().also {
        for (cookie in Json.decodeFromString<List<Cookie>>(
            sharedPreferences.getString(
                "cookies",
                "[]"
            )!!
        )) {
            it.add(cookie)
        }
    }

    val cookies = object: MutableList<Cookie> by _cookies{
        override fun add(element: Cookie): Boolean {
            val isAdd = _cookies.add(element)
            if (isAdd){
                sharedPreferences.edit().putString("cookies", Json.encodeToString(_cookies)).apply()
            }
            return isAdd
        }
    }

}