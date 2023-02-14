package cn.xd.bogr.viewmodel

import android.content.SharedPreferences
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import cn.xd.bogr.net.entity.ForumListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    val _itemsSpacing = mutableStateOf(4)
    val itemsSpacing
        get() = _itemsSpacing.value

    lateinit var navCollection: NavHostController
    val drawerState = DrawerState(DrawerValue.Closed)

    var forumList = mutableStateListOf<ForumListItem>()
    private var _forumSelected = mutableStateOf(
        store.getInt("forumSelected", 0)
    )
    var forumSelected
        get() = _forumSelected.value
        set(value) {
            store.edit().putInt("forumSelected", value).apply()
            _forumSelected.value = value
        }

    fun launchIO(block: suspend CoroutineScope.() -> Unit){
        viewModelScope.launch(Dispatchers.IO, block =  block)
    }
    fun launchMain(block: suspend CoroutineScope.() -> Unit){
        viewModelScope.launch(Dispatchers.Main, block =  block)
    }
}