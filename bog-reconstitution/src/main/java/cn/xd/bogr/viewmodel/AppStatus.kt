package cn.xd.bogr.viewmodel

import android.content.SharedPreferences
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.xd.bogr.net.entity.Content
import cn.xd.bogr.net.entity.ForumListItem
import cn.xd.bogr.net.entity.Reply
import cn.xd.bogr.net.entity.Strand
import cn.xd.bogr.net.paging.ForumPaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppStatus @Inject constructor(
    private val store: SharedPreferences
): ViewModel() {

    private var _iconSize = mutableStateOf(
        store.getInt("iconSize", 24)
    )
    var iconSize
        get() = _iconSize.value
        set(value) {
            store.edit().putInt("iconSize", 24).apply()
            _iconSize.value = value
        }
    val sIconSize
        get() = iconSize - 2
    val lIconSize
        get() = iconSize + 2

    private var _logoSize = mutableStateOf(
        store.getInt("logoSize", 250)
    )
    var logoSize
        get() = _logoSize.value
        set(value) {
            store.edit().putInt("logoSize", value).apply()
            _logoSize.value = value
        }

    private var _fontSize = mutableStateOf(
        store.getInt("logoSize", 14)
    )
    var fontSize
        get() = _fontSize.value
        set(value) {
            store.edit().putInt("logoSize", value).apply()
            _fontSize.value = value
        }
    val sFontSize
        get() = _fontSize.value - 1
    val ssFontSize
        get() = _fontSize.value - 2
    val sssFontSize
        get() = _fontSize.value - 3
    val s4FontSize
        get() = _fontSize.value - 4
    val lFontSize
        get() = _fontSize.value + 2
    val llFontSize
        get() = _fontSize.value + 4
    val lllFontSize
        get() = _fontSize.value + 6
    val l4FontSize
        get() = _fontSize.value + 8
    val l5FontSize
        get() = _fontSize.value + 10

    private val _itemsSpacing = mutableStateOf(4)
    val itemsSpacing
        get() = _itemsSpacing.value

    lateinit var navController: NavHostController
    lateinit var bottomNavController: NavHostController

    val drawerState = DrawerState(DrawerValue.Closed)

    var forumList = mutableStateListOf<ForumListItem>()
    var forumMap = mutableStateMapOf<Int, ForumListItem>()
    private var _forumSelected = mutableStateOf(
        store.getInt("forumSelected", 0)
    )
    var forumSelected
        get() = _forumSelected.value
        set(value) {
            store.edit().putInt("forumSelected", value).apply()
            _forumSelected.value = value
        }
    fun selected(value: Int, lazyListState: LazyListState){
        forumSelected = value
        launchMain {
            val offset = getForumListOffset()
            delay(50)
            lazyListState.scrollToItem(offset.first, offset.second)
        }
    }

    private val forum = mutableStateMapOf<Int, Flow<PagingData<Strand>>>()
    fun getForum(): Flow<PagingData<Strand>> {
        var paging = forum[forumSelected]
        if(paging == null){
            paging = Pager(config = PagingConfig(pageSize = 20)){
                ForumPaging(forumList[forumSelected].id)
            }.flow.cachedIn(viewModelScope)
            forum[forumSelected] = paging
        }
        return paging
    }

    private val forumListOffset = mutableStateMapOf<Int, Pair<Int, Int>>()
    fun getForumListOffset(): Pair<Int, Int>{
        var listOffset = forumListOffset[forumSelected]
        if (listOffset == null){
            listOffset = 0 to 0
            forumListOffset[forumSelected] = listOffset
        }
        return listOffset
    }
    fun saveForumListOffset(lazyListState: LazyListState){
        forumListOffset[forumSelected] = lazyListState.firstVisibleItemIndex to lazyListState.firstVisibleItemScrollOffset
    }

    val contentMap = mutableStateMapOf<Int, Content>()
    val standPagerMap = mutableStateMapOf<Int, Flow<PagingData<Reply>>>()
    val listOffsetMap = mutableStateMapOf<Int, Pair<Int, Int>>()

    var pageSelected by mutableStateOf(0)

    fun launchIO(block: suspend CoroutineScope.() -> Unit){
        viewModelScope.launch(Dispatchers.IO, block =  block)
    }
    fun launchMain(block: suspend CoroutineScope.() -> Unit){
        viewModelScope.launch(Dispatchers.Main, block =  block)
    }
}
