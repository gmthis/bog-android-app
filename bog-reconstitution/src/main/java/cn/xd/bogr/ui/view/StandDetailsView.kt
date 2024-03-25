package cn.xd.bogr.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.*
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import cn.xd.bogr.net.entity.Content
import cn.xd.bogr.net.entity.Image
import cn.xd.bogr.net.entity.Reply
import cn.xd.bogr.ui.view.components.Item
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StrandDetailsView(
    content: Content,
    pager: Flow<PagingData<Reply>>,
    images: LinkedHashSet<Image>,
    listState: LazyListState
) {
    val pagingItems = pager.collectAsLazyPagingItems()

    val refreshState = rememberPullRefreshState(
        refreshing = pagingItems.loadState.refresh is LoadState.Loading && pagingItems.itemCount > 0,
        onRefresh = { pagingItems.refresh() }
    )

    Box(modifier = Modifier
        .fillMaxSize()
        .pullRefresh(refreshState)
    ){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
            state = listState
        ){
            item {
                Item(content = content, listState = listState, notIsDetails = false)
            }
            items(pagingItems){it ->
                it?.let {
                    Item(content = it, listState = listState, notIsDetails = false)
                }
            }
            if (pagingItems.loadState.append is LoadState.Loading){
                item {
                    Box(Modifier.fillMaxSize()) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                }
            }
        }
        PullRefreshIndicator(
            refreshing = pagingItems.loadState.refresh is LoadState.Loading && pagingItems.itemCount > 0,
            state = refreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            contentColor = MaterialTheme.colorScheme.onSecondary
        )
    }
}