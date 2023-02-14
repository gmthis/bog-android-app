package cn.xd.bogr.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
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
    viewModel.navCollection = controller

    AnimatedNavHost(navController = controller, startDestination = "loading"){
        composable("loading"){
            Loading()
        }
        composable("main"){
            Main()
        }
    }
}