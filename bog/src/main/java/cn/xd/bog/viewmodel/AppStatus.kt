package cn.xd.bog.viewmodel

import android.content.SharedPreferences
import androidx.compose.material.*
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.xd.bog.entity.SingleContent
import cn.xd.bog.entity.SingleContentInfo
import cn.xd.bog.ui.page.DrawPageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class AppStatus(
    private val sharedPreferences: SharedPreferences,
    private val data: Data
): ViewModel() {
    val scaffoldState = ScaffoldState(DrawerState(DrawerValue.Closed), SnackbarHostState())
    val snackbarHostState = SnackbarHostState()

    val drawPageInfo: DrawPageInfo = DrawPageInfo(viewModelScope)
    private val _fontSize = mutableStateOf(
        sharedPreferences.getInt(DataKeys.fontSize, 16)
    )
    var fontSize: Int
        get() = _fontSize.value
        set(value) {
            _fontSize.value = value
            sharedPreferences.edit().putInt(DataKeys.fontSize, value).apply()
        }

    private val _selectedItem = mutableStateOf(0)
    var selectedItem: Int
        get() = _selectedItem.value
        set(value) {
            _selectedItem.value = value
        }

    private val _forumSelectedItem = mutableStateOf(
        sharedPreferences.getInt(DataKeys.forumSelectedItem, 0)
    )
    var forumSelectedItem: Int
        get() = _forumSelectedItem.value
        set(value){
            _forumSelectedItem.value = value
            forumSelectedId = data.forum!!.info[value].id
            sharedPreferences.edit().putInt(DataKeys.forumSelectedItem, value).apply()
            currentPage = 1
            data.pullForumContent(forumSelectedId)
        }
    private val _forumSelectedId = mutableStateOf(
        sharedPreferences.getInt(DataKeys.forumSelectedId, 0)
    )
    var forumSelectedId: Int
        get() = _forumSelectedId.value
        set(value) {
            _forumSelectedId.value = value
            sharedPreferences.edit().putInt(DataKeys.forumSelectedId, value).apply()
        }

    private var currentPage = 1

    fun nextPage(
        loading: () -> Unit,
        error: () -> Unit
    ){
        currentPage++
        data.pullNextForumContent(forumSelectedId, currentPage, loading, error)
    }

    fun refresh(loading: () -> Unit){
        currentPage = 1
        data.pullForumContent(forumSelectedId, loading)
    }

    fun getStringContent(
        stringId: Int,
        page: Int,
        itemNum: Int,
        order: Int,
        after: (() -> Unit)? = null
    ){
        data.pullStringContent(
            stringId,
            page,
            itemNum,
            order,
            after
        )
    }

    fun stringContentNext(
        stringId: Int,
        loading: () -> Unit,
        error: (Int, String) -> Unit
    ){
        data.pullStringContentNextPage(
            stringId,
            error,
            loading
        )
    }

    fun stringContentRefresh(
        stringId: Int,
        order: Int,
        loading: () -> Unit
    ){
        data.stringContentMap.remove(stringId.toString())
        data.pullStringContent(
            stringId = stringId,
            page = 1,
            itemNum = 20,
            order = order,
            loading
        )
    }

    fun pullSingleContent(
        id: String,
        container: MutableState<SingleContentInfo?>,
        error: (Int, String) -> Unit,
        after: (() -> Unit)?
    ){
        data.pullSingleContent(
            id, container, error, after
        )
    }

    private var _selected by mutableStateOf(if (forumSelectedItem == 0){
        1
    }else{
        forumSelectedItem
    })

    var selected
        get() = _selected
        set(value) {
            form = data.forum!!.info[value].name
            _selected = value
        }

    var form by mutableStateOf("")

    init {
        data.pullForum(forumSelectedItem){
            form = data.forum!!.info[selected].name
        }
    }

    var cookieSelected by mutableStateOf(0)

}
