package cn.xd.bogr.ui.page

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cn.xd.bogr.ui.navigation.BottomNavigationMap
import cn.xd.bogr.ui.navigation.Container
import cn.xd.bogr.ui.page.components.BottomBar
import cn.xd.bogr.ui.page.components.Drawer
import cn.xd.bogr.ui.page.components.FloatingButton
import cn.xd.bogr.ui.page.components.TopBar
import cn.xd.bogr.ui.state.rememberContainerState
import cn.xd.bogr.ui.theme.ExtendedTheme
import cn.xd.bogr.util.rememberViewModel
import cn.xd.bogr.viewmodel.AppStatus
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main() {
    val viewModel = rememberViewModel<AppStatus>()
    val context = LocalContext.current as ComponentActivity
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val containerState = rememberContainerState()
    BackHandler{
        context.moveTaskToBack(true)
    }

    BackHandler(
        enabled = viewModel.drawerState.isOpen
    ) {
        scope.launch {
            viewModel.drawerState.close()
        }
    }

    ModalNavigationDrawer(
        modifier = Modifier.fillMaxSize(),
        drawerState = viewModel.drawerState,
        drawerContent = { Drawer(listState) },
        gesturesEnabled = viewModel.pageSelected == 0,
        scrimColor = ExtendedTheme.colors.obscured
    ) {
        Scaffold(
            topBar = { TopBar() },
            bottomBar = { BottomBar() },
            floatingActionButton = {
                AnimatedVisibility(
                    visible = viewModel.pageSelected == 0,
                    enter = slideInHorizontally(initialOffsetX = {(it * 1.5).toInt()}) ,
                    exit = slideOutHorizontally(targetOffsetX = {(it * 1.5).toInt()})
                ) {
                    FloatingButton(containerState)
                }
            },
            snackbarHost = {},
        ) {
            Box(Modifier.padding(it)) {
                Container(state = containerState) {
                    BottomNavigationMap(listState, containerState)
                }
            }
        }
    }
}