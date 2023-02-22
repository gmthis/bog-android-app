package cn.xd.bogr.ui.page

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.PagingData
import cn.xd.bogr.R
import cn.xd.bogr.net.entity.Content
import cn.xd.bogr.net.entity.Reply
import cn.xd.bogr.ui.view.StrandDetailsView
import cn.xd.bogr.util.rememberViewModel
import cn.xd.bogr.viewmodel.AppStatus
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Details(content: Content, pager: Flow<PagingData<Reply>>, listState: LazyListState){
    Scaffold(
        topBar = { DetailsTopBar(content) },
        bottomBar = { DetailsBottomBar() },
        floatingActionButton = {},
        snackbarHost = {},
    ) {
        Surface(Modifier.padding(it)) {
            StrandDetailsView(content = content, pager, listState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsTopBar(content: Content) {
    val viewModel = rememberViewModel<AppStatus>()
    CenterAlignedTopAppBar(
        title = {
            Text(text = "Po.${content.id}")
        },
        navigationIcon = {
            Icon(
                painter = painterResource(id = R.drawable.back),
                contentDescription = stringResource(id = R.string.back),
                modifier = Modifier
                    .size(viewModel.sIconSize.dp)
            )
        },
        actions = {
            Icon(
                painter = painterResource(id = R.drawable.notifications_off),
                contentDescription = stringResource(id = R.string.remind),
                modifier = Modifier
                    .size(viewModel.sIconSize.dp)
            )
            Icon(
                painter = painterResource(id = R.drawable.sort),
                contentDescription = stringResource(id = R.string.sort),
                modifier = Modifier
                    .size(viewModel.sIconSize.dp)
            )
            Icon(
                painter = painterResource(id = R.drawable.filter_list),
                contentDescription = stringResource(id = R.string.filter),
                modifier = Modifier
                    .size(viewModel.sIconSize.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun DetailsBottomBar() {
    val viewModel = rememberViewModel<AppStatus>()
    val itemsInfo = stringArrayResource(id = R.array.details_bottom_bar_items_info)
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background
    ) {
        DetailsNavigationBarItem(itemsInfo[0], R.drawable.favorite, viewModel){}
        DetailsNavigationBarItem(itemsInfo[1], R.drawable.share, viewModel){}
        DetailsNavigationBarItem(itemsInfo[2], R.drawable.edit, viewModel){}
        DetailsNavigationBarItem(itemsInfo[3], R.drawable.bookmark, viewModel){}
    }
}

@Composable
fun RowScope.DetailsNavigationBarItem(
    itemInfo: String,
    painter: Int,
    viewModel: AppStatus,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = false,
        onClick = onClick,
        label = { Text(text = itemInfo, fontSize = viewModel.sssFontSize.sp) },
        icon = {
            Icon(
                painter = painterResource(id = painter),
                contentDescription = itemInfo,
                modifier = Modifier.size(viewModel.lIconSize.dp)
            )
        },
        colors = NavigationBarItemDefaults.colors(
            unselectedIconColor = MaterialTheme.colorScheme.onSecondary,
            unselectedTextColor = MaterialTheme.colorScheme.onSecondary
        )
    )
}
