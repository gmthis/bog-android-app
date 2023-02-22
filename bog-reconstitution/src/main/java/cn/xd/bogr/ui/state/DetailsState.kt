package cn.xd.bogr.ui.state

import androidx.compose.foundation.lazy.LazyListState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cn.xd.bogr.net.entity.Content
import cn.xd.bogr.net.paging.StrandPaging
import kotlinx.coroutines.CoroutineScope

class DetailsState(
    val content: Content
) {
    private val pager = Pager(config = PagingConfig(pageSize = 20)){ StrandPaging(content.id) }.flow
    fun getPager(scope: CoroutineScope) = pager.cachedIn(scope)

    val listState = LazyListState()
    var listFirst = 0
    var listFirstOffset = 0
}