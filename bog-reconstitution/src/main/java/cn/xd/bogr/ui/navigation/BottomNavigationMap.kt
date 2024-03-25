package cn.xd.bogr.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cn.xd.bogr.ui.state.ContainerState
import cn.xd.bogr.ui.view.ForumView
import cn.xd.bogr.util.rememberViewModel
import cn.xd.bogr.viewmodel.AppStatus
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BottomNavigationMap(
    listState: LazyListState,
    interiorContainerState: ContainerState,
    containerState: ContainerState
) {
    val controller = rememberAnimatedNavController()
    val viewModel = rememberViewModel<AppStatus>()

    viewModel.bottomNavController = controller

    AnimatedNavHost(navController = controller, startDestination = "0"){
        composable(
            "0",
            enterTransition = {slideInHorizontally(initialOffsetX = {it})},
            exitTransition = {slideOutHorizontally(targetOffsetX = {it})}
        ){
            ForumView(listState, containerState)
            LaunchedEffect(Unit){
                val offset = viewModel.getForumListOffset()
                delay(50)
                listState.scrollToItem(offset.first, offset.second)
            }
        }
        composable(
            "1",
            enterTransition = {slideInHorizontally(initialOffsetX = {it})},
            exitTransition = {slideOutHorizontally(targetOffsetX = {it})}
        ){
            viewModel.saveForumListOffset(listState)
            interiorContainerState.close()
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Red)){
                Text(text = "favorite")
            }

        }
        composable(
            "2",
            enterTransition = {slideInHorizontally(initialOffsetX = {it})},
            exitTransition = {slideOutHorizontally(targetOffsetX = {it})}
        ){
            viewModel.saveForumListOffset(listState)
            interiorContainerState.close()
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Green)){
                Text(text = "history")
            }
        }
        composable(
            "3",
            enterTransition = {slideInHorizontally(initialOffsetX = {it})},
            exitTransition = {slideOutHorizontally(targetOffsetX = {it})}
        ){
            viewModel.saveForumListOffset(listState)
            interiorContainerState.close()
            Box(modifier = Modifier
                .fillMaxSize()
                .background(Color.Blue)){
                Text(text = "settings")
            }
        }
    }
}