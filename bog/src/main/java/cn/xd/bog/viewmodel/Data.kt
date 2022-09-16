package cn.xd.bog.viewmodel

import android.content.SharedPreferences
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import cn.xd.bog.controller.Pull
import cn.xd.bog.controller.impl.PullImpl
import cn.xd.bog.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class Data(private val sharedPreferences: SharedPreferences): ViewModel() {
    private val pull: Pull = PullImpl()
    private val _forum: MutableState<Forum?> = mutableStateOf(null)
    val forum: Forum?
        get() = _forum.value
    val forumMap: MutableMap<Int, String> = LinkedHashMap() //mutableMapOf()

    fun pullForum(id: Int){
        pull.pullForum(_forum){
            forum!!.info.forEach{
                forumMap[it.id] = it.name
            }
            pullForumContent(forum!!.info[id].id)
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
}