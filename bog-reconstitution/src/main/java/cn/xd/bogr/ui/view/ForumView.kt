package cn.xd.bogr.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import cn.xd.bogr.ui.components.Item
import cn.xd.bogr.util.rememberViewModel
import cn.xd.bogr.viewmodel.AppStatus
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ForumView(listState: LazyListState) {
    val viewModel = rememberViewModel<AppStatus>()
    val pagingItems = viewModel.getForum().collectAsLazyPagingItems()

    val refreshState = rememberPullRefreshState(
        refreshing = pagingItems.loadState.refresh is LoadState.Loading && pagingItems.itemCount > 0,
        onRefresh = { pagingItems.refresh() }
    )

    Box(modifier = Modifier
        .pullRefresh(refreshState)
    ){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
            state = listState
        ){
            items(pagingItems){it ->
                it?.let {
                    Item(content = it, listState,true)
                }
            }
            if (pagingItems.loadState.append is LoadState.Loading){
                item {
                    Box(
                        Modifier
                            .fillMaxSize()) {
                        Text(text = "加载中", modifier = Modifier.fillMaxSize())
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

    if (pagingItems.loadState.refresh is LoadState.Loading){
        if (pagingItems.itemCount == 0){
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
    }
}