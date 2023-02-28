package cn.xd.bogr.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import cn.xd.bogr.net.entity.Content
import cn.xd.bogr.net.entity.Image
import cn.xd.bogr.net.paging.StrandPaging
import cn.xd.bogr.ui.page.Details
import cn.xd.bogr.ui.page.ImageDetails
import cn.xd.bogr.ui.page.Loading
import cn.xd.bogr.ui.page.Main
import cn.xd.bogr.util.rememberViewModel
import cn.xd.bogr.viewmodel.AppStatus
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationMap(){
    val controller = rememberAnimatedNavController()
    val viewModel = rememberViewModel<AppStatus>()
    viewModel.navController = controller

    AnimatedNavHost(navController = controller, startDestination = "loading"){
        composable("loading",
            enterTransition = {slideInHorizontally {it} + fadeIn()},
            exitTransition = {slideOutHorizontally{it} + fadeOut()}
        ){
            Loading()
        }
        composable("main",
            enterTransition = {slideInHorizontally {it} + fadeIn()},
            exitTransition = {slideOutHorizontally{it} + fadeOut()}
        ){
            Main()
        }
        composable(
            "details/{strandId}",
            enterTransition = {slideInHorizontally {it} + fadeIn()},
            exitTransition = {slideOutHorizontally{it} + fadeOut()}
        ){
            val strandId = it.arguments?.getString("strandId")!!.toInt()
            val content: Content = remember {
                viewModel.contentMap[strandId]!!
            }
            val images = remember {
                viewModel.strandImageMap[content.id] ?: LinkedHashSet<Image>().also { images ->
                    content.images?.let { list -> images.addAll(list) }
                    viewModel.strandImageMap[content.id] = images
                }
            }
            val pager = remember {
                viewModel.strandPagerMap[content.id] ?: Pager(config = PagingConfig(pageSize = 20)){
                    StrandPaging(content.id, images)
                }.flow.cachedIn(viewModel.viewModelScope).also {
                    viewModel.strandPagerMap[content.id] = it
                }
            }
            val listState = rememberLazyListState()

            BackHandler {
                viewModel.navController.popBackStack()
                viewModel.contentMap.remove(strandId)
                viewModel.strandPagerMap.remove(strandId)
                viewModel.listOffsetMap.remove(strandId)
                viewModel.strandImageMap.remove(strandId)
            }
            Details(content, pager, images, listState)
        }
        composable(
            "imageDetails/{strandId}/{selected}/{isDetails}",
            enterTransition = {slideInHorizontally {it} + fadeIn()},
            exitTransition = {slideOutHorizontally{it} + fadeOut()}
        ){
            val strandId = it.arguments?.getString("strandId")!!.toInt()
            val selected = it.arguments?.getString("selected")!!.toInt()
            val isDetails = it.arguments?.getString("isDetails")!!.toBoolean()
            val images = remember {
                viewModel.strandImageMap[strandId]!!
            }

            val back = {
                viewModel.navController.popBackStack()
                if (!isDetails) viewModel.strandImageMap.clear()
            }

            BackHandler(onBack = back)

            ImageDetails(images = images, selected =selected, back = back)
        }
    }
}