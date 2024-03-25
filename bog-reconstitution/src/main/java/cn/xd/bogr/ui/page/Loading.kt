package cn.xd.bogr.ui.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import cn.xd.bogr.net.requestForumlist
import cn.xd.bogr.ui.view.components.SnackBar
import cn.xd.bogr.util.launchIO
import cn.xd.bogr.util.rememberViewModel
import cn.xd.bogr.viewmodel.AppStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Loading() {
    val viewModel = rememberViewModel<AppStatus>()
    val hostState: SnackbarHostState = remember {
        SnackbarHostState()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "B",
            fontSize = viewModel.logoSize.sp,
            modifier = Modifier
                .navigationBarsPadding(),
            color = MaterialTheme.colorScheme.onPrimary
        )
        SnackbarHost(
            hostState = hostState,
            modifier = Modifier.wrapContentSize(Alignment.BottomCenter)
        ){
            SnackBar(data = it)
        }
    }
    LaunchedEffect(Unit){
        launchIO {
            var forumList = requestForumlist()

            while (forumList.code != 6001){
                hostState.showSnackbar("错误: ${forumList.code}, 讯息: ${forumList.type}")
                delay(2000)
                forumList = requestForumlist()
            }
            viewModel.forumList += forumList.info.sortedBy {
                it.rank
            }
            viewModel.forumMap += viewModel.forumList.associateBy { it.id }
            viewModel.sendForum = viewModel.forumList.indexOf(viewModel.forumMap[viewModel.forumSelected])
            viewModel.allowNewStrandForum += viewModel.forumList.filter {
                it.id != 0 && it.isClose
            }
            launch(Dispatchers.Main){
                viewModel.navController.popBackStack()
                viewModel.navController.navigate("main")
            }
        }
    }
}