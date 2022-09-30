package cn.xd.bog.ui.navigation

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import cn.xd.bog.entity.Image
import cn.xd.bog.ui.page.ImageDetails
import cn.xd.bog.ui.view.LoadingView
import cn.xd.bog.ui.view.MainView
import cn.xd.bog.ui.view.StringContentView
import cn.xd.bog.viewmodel.AppStatus
import cn.xd.bog.viewmodel.Data
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationModel() {
    val context = LocalContext.current as ComponentActivity
    val preferences =
        context.getSharedPreferences("app_status", ComponentActivity.MODE_PRIVATE)
    val data: Data = viewModel(context, factory = viewModelFactory {
        addInitializer(Data::class){
            Data(preferences)
        }
    })

    val appStatus: AppStatus = viewModel(context, factory = viewModelFactory {
        addInitializer(AppStatus::class){
            AppStatus(preferences, data)
        }
    })

    val back = {
        context.moveTaskToBack(true)
    }

    val navController = rememberAnimatedNavController()
    val scope = rememberCoroutineScope()

    AnimatedNavHost(navController = navController, startDestination = "loading"){
        composable("loading"){
            LoadingView {
                scope.launch {
                    delay(1000)
                    navController.popBackStack()
                    navController.navigate("main")
                }
            }
        }
        composable("main"){
            BackHandler {
                back()
            }
            MainView(
                scaffoldState = appStatus.scaffoldState,
                scope = scope,
                forum = data.forum,
                appStatus = appStatus,
                data = data,
                forumContentInfoList = data.forumContentInfoList,
                fontSize = appStatus.fontSize,
                selectedItem = appStatus.selectedItem,
                selected = {appStatus.selectedItem = it},
                sidebarSelectedItem = appStatus.forumSelectedItem,
                sidebarSelected = {
                    appStatus.forumSelectedItem = it
                    scope.launch{
                        appStatus.scaffoldState.drawerState.close()
                    }
                },
                forumMap = data.forumMap,
                loading = appStatus::nextPage,
                refresh = appStatus::refresh,
                jump = {
                    navController.navigate("content/${it}")
                },
                pullContent = appStatus::pullSingleContent,
                imageDetails = { images, selected ->
                    navController.navigate("imageDetails/${
                        Json.encodeToString(images)
                    }/$selected")
                }
            )
        }
        composable(
            "content/{stringId}",
            enterTransition = {
                slideInVertically(initialOffsetY = {
                    it
                })
            },
            exitTransition = {
                slideOutVertically(targetOffsetY = {
                    it
                })
            }
        ){
            val lazyListState = rememberLazyListState()
            val stringId = it.arguments?.getString("stringId")!!
            if (data.stringContentMap[stringId] == null) {
                appStatus.getStringContent(
                    stringId.toInt(),
                    1,
                    20,
                    0
                )
            }
            StringContentView(
                stringId = stringId,
                stringContents = data.stringContentMap,
                scaffoldState = rememberScaffoldState(),
                fontSize = appStatus.fontSize,
                forum = data.forumMap,
                back = {
                    data.stringContentMap.remove(stringId)
                    navController.popBackStack()
                },
                refresh = appStatus::stringContentRefresh,
                next = appStatus::stringContentNext,
                pullContent = appStatus::pullSingleContent,
                imageDetails = { images, selected ->
                    navController.navigate("imageDetails/${
                        Json.encodeToString(images)
                    }/$selected")
                },
                jump = {
                    navController.navigate("content/${it}")
                },
                sort = { sort ->
                    scope.launch {
                        lazyListState.animateScrollToItem(0)
                    }
                    appStatus.getStringContent(
                        stringId.toInt(),
                        1,
                        20,
                        sort
                    ){
                        data.stringContentMap[stringId]!!.sort = sort
                    }
                },
                lazyListState = lazyListState
            )
        }
        composable(
            "imageDetails/{images}/{selected}",
            enterTransition = {
                slideInVertically(initialOffsetY = {
                    it
                })
            },
            exitTransition = {
                slideOutVertically(targetOffsetY = {
                    it
                })
            }
        ){
            val imagesString = it.arguments?.getString("images")!!.run {
                substring(1, this.length-1).replace("\\\"", "\"")
            }
            val images: List<Image> = Json.decodeFromString(imagesString)
            val selected = it.arguments?.getString("selected")?.toInt() ?: 0
            ImageDetails(
                images = images,
                selected = selected,
                back = {
                    navController.popBackStack()
                }
            )
        }
    }
}