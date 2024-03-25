package cn.xd.bogr.ui.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import cn.xd.bogr.R
import cn.xd.bogr.ui.state.ContainerState
import cn.xd.bogr.ui.theme.ExtendedTheme
import cn.xd.bogr.ui.view.components.Item
import cn.xd.bogr.ui.view.components.Popover
import cn.xd.bogr.ui.view.components.PopoverItem
import cn.xd.bogr.util.rememberViewModel
import cn.xd.bogr.viewmodel.AppStatus

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun ForumView(listState: LazyListState, containerState: ContainerState) {
    val viewModel = rememberViewModel<AppStatus>()
    val pagingItems = viewModel.getForum().collectAsLazyPagingItems()

    val refreshState = rememberPullRefreshState(
        refreshing = pagingItems.loadState.refresh is LoadState.Loading && pagingItems.itemCount > 0,
        onRefresh = { pagingItems.refresh() }
    )

    Box(modifier = Modifier
        .pullRefresh(refreshState)
    ){
        val dimmed = ExtendedTheme.colors.dimmed
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
            state = listState
        ){
            items(pagingItems){it ->
                it?.let {
                    Item(content = it, listState = listState, notIsDetails = true, modifier = Modifier.combinedClickable(
                        indication = null,
                        interactionSource = remember {
                            MutableInteractionSource()
                        },
                        onClick = {
                            viewModel.contentMap[it.id] = it
                            viewModel.saveForumListOffset(listState)
                            viewModel.navController.navigate("details/${it.id}")
                        },
                        onLongClick = {
                            containerState.open {
                                contentAlignment = Alignment.Center
                                obscured = dimmed
                                composable = {
                                    Popover(title = it.content) {
                                        PopoverItem(
                                            prompt = stringResource(id = R.string.shield),
                                            content = "Po.${it.id}"
                                        )
                                        PopoverItem(
                                            prompt = stringResource(id = R.string.shieldCookie),
                                            content = it.cookie
                                        )
                                    }
                                }
                            }
                        }
                    ))
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