package cn.xd.bogr.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cn.xd.bogr.net.entity.Content
import cn.xd.bogr.net.paging.StrandPaging
import cn.xd.bogr.ui.page.Details
import cn.xd.bogr.ui.page.Loading
import cn.xd.bogr.ui.page.Main
import cn.xd.bogr.util.rememberViewModel
import cn.xd.bogr.viewmodel.AppStatus
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationMap(){
    val controller = rememberAnimatedNavController()
    val viewModel = rememberViewModel<AppStatus>()
    viewModel.navController = controller

    AnimatedNavHost(navController = controller, startDestination = "loading"){
        composable("loading"){
            Loading()
        }
        composable("main"){
            Main()
        }
        composable(
            "details/{strandId}",
            enterTransition = {slideInHorizontally {it}},
            exitTransition = { slideOutHorizontally{it} }
        ){
            val content: Content = remember {
                viewModel.contentMap[it.arguments?.getString("strandId")!!.toInt()]!!
            }
            val pager = remember {
                viewModel.standPagerMap[content.id] ?: Pager(config = PagingConfig(pageSize = 20)){
                    StrandPaging(content.id)
                }.flow.cachedIn(viewModel.viewModelScope).also {
                    viewModel.standPagerMap[content.id] = it
                }
            }
            val listState = rememberSaveable(saver = LazyListState.Saver) {
                viewModel.listStateMap[content.id] ?: LazyListState().also {
                    viewModel.listStateMap[content.id] = it
                }
            }

            BackHandler {
                viewModel.navController.popBackStack()
                viewModel.contentMap.remove(content.id)
                viewModel.standPagerMap.remove(content.id)
                viewModel.listStateMap.remove(content.id)
            }
            Details(content, pager, listState)
        }
    }
}