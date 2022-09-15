package cn.xd.bog.data.viewmodel

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.xd.bog.controller.Data
import cn.xd.bog.controller.impl.DataImpl
import cn.xd.bog.entity.*
import com.google.accompanist.swiperefresh.SwipeRefreshState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var plateList: MutableList<PlateInfo> = mutableStateListOf()
    var plateMap: MutableMap<Int, PlateInfo> = mutableStateMapOf()
    var plateID: MutableState<Int> = mutableStateOf(0)
    var plateContentList: MutableList<PlateContentInfo> = mutableStateListOf()

    private var pageIndex = 1
    var isBottomLoading = mutableStateOf(false)
    var isNewLoading = mutableStateOf(false)
    var noMore = mutableStateOf(false)

    private val data: Data = DataImpl()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            plateList += data.requestPlateList().info
            plateMap += plateList.associateBy { it.id }
            pullNewPlateContent(plateList[0].id, 1)
        }
    }

    fun pullNewPlateContent(
        id: Int,
        pageIndex: Int,
        num: Int = 20,
        swipeRefreshState: SwipeRefreshState? = null
    ) {
        this@MainViewModel.pageIndex = pageIndex
        viewModelScope.launch(Dispatchers.IO) {
            val (plateContent, errorInfo) = data.requestPlateContent(id, pageIndex, num)
            plateContent?.run {
                plateContentList.clear()
                plateContentList += info
            }
            swipeRefreshState?.isRefreshing = false
        }
    }

    fun pullPlateContent(num: Int = 20) {

        isBottomLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            pageIndex++
            val (plateContent, errorInfo) = data.requestPlateContent(plateID.value, pageIndex, num)
            plateContent?.run {
                plateContentList += info
            }
            errorInfo?.run {
                noMore.value = true
            }
            isBottomLoading.value = false
        }
    }

    val content: MutableState<ContentInfo?> = mutableStateOf(null)
    val contentReplay: MutableList<Reply> = mutableStateListOf()
    var contentPageIndex: Int = 1
    var contentID: MutableState<Int> = mutableStateOf(0)
    var contentOrder: Int = 0

    fun pullNewContent(
        id: Int,
        pageIndex: Int,
        num: Int = 20,
        order: Int = 0,
        lazyListState: LazyListState,
        swipeRefreshState: SwipeRefreshState? = null
    ) {
        this.contentID.value = id
        contentOrder = order
        this@MainViewModel.contentPageIndex = pageIndex
        if (swipeRefreshState == null){
            content.value = null
            contentReplay.clear()
        }
        viewModelScope.launch(Dispatchers.IO) {
            lazyListState.scrollToItem(0)
            val (_content, errorInfo) = data.requestContent(id, pageIndex, num, order)
            _content?.run {
                val info = _content.info
                content.value = info
                if (swipeRefreshState != null){
                    content.value = null
                    contentReplay.clear()
                }
                contentReplay += info.reply.also {
                    (it as MutableList).add(0, Reply(
                        info.admin,
                        info.content,
                        info.cookie,
                        info.emoji,
                        info.id,
                        info.images,
                        info.name,
                        info.res,
                        info.time
                    ))
                }
            }
            errorInfo?.run {
                val info = pullSingleContent(id).info
                content.value = ContentInfo(
                    info.admin,
                    info.content,
                    info.cookie,
                    info.emoji,
                    info.forum,
                    info.id,
                    info.images,
                    info.lock,
                    info.name,
                    emptyList(),
                    0,
                    info.res,
                    "",
                    info.time,
                    info.title
                )
                contentReplay += Reply(
                    info.admin,
                    info.content,
                    info.cookie,
                    info.emoji,
                    info.id,
                    info.images,
                    info.name,
                    info.res,
                    info.time
                )
            }
            swipeRefreshState?.isRefreshing = false
        }
    }

    fun pullContent(num: Int = 20) {
        val remaining = ((content.value?.replyCount
            ?: -99999) - (contentReplay.size - 1)).toDouble() / num.toDouble()

        if (remaining <= 0){
            return
        }

        isBottomLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            contentPageIndex++
            val info = data.requestContent(contentID.value, contentPageIndex, num, contentOrder).first?.info
            isBottomLoading.value = false
            contentReplay += info?.reply ?: emptyList()
        }
    }

    fun pullSingleContent(id: Int): SingleContent {
        return data.requestSingle(id)
    }

}