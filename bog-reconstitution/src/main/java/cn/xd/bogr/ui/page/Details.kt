package cn.xd.bogr.ui.page

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.paging.PagingData
import cn.xd.bogr.net.entity.Content
import cn.xd.bogr.net.entity.Image
import cn.xd.bogr.net.entity.Reply
import cn.xd.bogr.ui.page.components.DetailsBottomBar
import cn.xd.bogr.ui.page.components.DetailsTopBar
import cn.xd.bogr.ui.view.StrandDetailsView
import cn.xd.bogr.util.rememberViewModel
import cn.xd.bogr.viewmodel.AppStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Details(content: Content, pager: Flow<PagingData<Reply>>, images: LinkedHashSet<Image>, listState: LazyListState){
    val viewModel = rememberViewModel<AppStatus>()
    Scaffold(
        topBar = { DetailsTopBar(content) },
        bottomBar = { DetailsBottomBar() },
        floatingActionButton = {},
        snackbarHost = {},
    ) {
        Surface(Modifier.padding(it)) {
            StrandDetailsView(content = content, pager, images, listState)
            viewModel.listOffsetMap[content.id]?.run {
                LaunchedEffect(Unit){
                    delay(50)
                    listState.scrollToItem(first, second)
                }
            }
        }
    }
}