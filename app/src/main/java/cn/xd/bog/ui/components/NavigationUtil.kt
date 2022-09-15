package cn.xd.bog.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavType
import androidx.navigation.compose.LocalOwnersProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import cn.xd.bog.UIMain
import cn.xd.bog.data.viewmodel.MainViewModel

@Composable
fun NavigableUtil(viewModel: MainViewModel){
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    NavHost(navController = navController, startDestination = "main"){
        composable("main"){
            UIMain(navController, scope)
        }
        composable(
            "details/{contentId}",
            arguments = listOf(
                navArgument("contentId"){type = NavType.IntType}
            )
        ){
            val contentId = it.arguments?.getInt("contentId")
            Details(viewModel = viewModel, contentId, navController, scope)
        }
    }
}