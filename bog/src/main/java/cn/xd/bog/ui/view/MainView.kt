package cn.xd.bog.ui.view

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import cn.xd.bog.entity.Forum
import cn.xd.bog.entity.ForumContentInfo
import cn.xd.bog.entity.SingleContentInfo
import cn.xd.bog.ui.components.*
import cn.xd.bog.ui.page.*
import cn.xd.bog.ui.theme.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.net.ssl.SSLEngineResult

@Composable
fun MainView(
    scaffoldState: ScaffoldState,
    scope: CoroutineScope,
    forum: Forum?,
    forumContentInfoList: MutableList<ForumContentInfo>,
    fontSize: Int,
    selectedItem: Int,
    selected: (Int) -> Unit,
    sidebarSelectedItem: Int,
    sidebarSelected: (Int) -> Unit,
    forumMap: Map<Int, String>,
    loading: (() -> Unit, () -> Unit) -> Unit,
    refresh: (() -> Unit) -> Unit,
    jump: ((Int) -> Unit)?,
    pullContent: (id: String,
                  container: MutableState<SingleContentInfo?>,
                  error: (Int, String) -> Unit,
                  after: (() -> Unit)?) -> Unit,
    imageDetails: (String, Int) -> Unit
) {
    val lazyListState = rememberLazyListState()
    if (scaffoldState.drawerState.isOpen) {
        val coroutineScope = rememberCoroutineScope()
        BackHandler {
            coroutineScope.launch{
                scaffoldState.drawerState.close()
            }
        }
    }
    var addIsOpen by remember {
        mutableStateOf(false)
    }
    var projectOpenItem by remember {
        mutableStateOf(0)
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopBar(
                    title = forum?.info?.getOrNull(sidebarSelectedItem)?.name,
                    fontSize = fontSize,
                    drawerState = scaffoldState.drawerState,
                    scope = scope
                )
            },
            bottomBar = {
                BottomBar(
                    selectedItem = selectedItem,
                    select = selected
                )
            },
            drawerContent = if (selectedItem == 0) {
                {
                    SideBar(
                        forum?.info ?: emptyList(),
                        sidebarSelectedItem,
                        sidebarSelected,
                        fontSize
                    )
                }
            } else null,
            snackbarHost = {},
            floatingActionButton = {
                if (selectedItem == 0){
                    Column(
                        horizontalAlignment = Alignment.End
                    ){
                        AnimatedVisibility(
                            visible = addIsOpen,
                            enter = fadeIn() + slideInVertically { height ->
                                height * 2
                            },
                            exit = fadeOut() + slideOutVertically { height ->
                                height * 2
                            }
                        ){
                            More(
                                fontSize = fontSize,
                                onClink = {
                                    projectOpenItem = it
                                    addIsOpen = !addIsOpen
                                }
                            )
                        }
                        FloatingButton(addIsOpen) {
                            addIsOpen = !addIsOpen
                        }
                    }
                }
            },
            backgroundColor = ContentBackground

        ) {
            Surface(
                modifier = Modifier
                    .padding(it)
            ) {
                when (selectedItem) {
                    0 -> {
                        ForumContentPage(
                            forumContentInfoList,
                            forumMap,
                            fontSize,
                            loading,
                            refresh,
                            jump,
                            pullContent,
                            imageDetails,
                            lazyListState
                        )
                        AnimatedVisibility(
                            visible = addIsOpen,
                            enter = fadeIn()
                                    + slideInVertically { height ->
                                height * 2
                            },
                            exit = fadeOut() + slideOutVertically { height ->
                                height * 2
                            }
                        ) {
                            Box(
                                Modifier
                                    .background(WhiteVariants)
                                    .fillMaxSize(1f)
                                    .clickable(
                                        indication = null,
                                        interactionSource = MutableInteractionSource(),
                                        onClick = {
                                            addIsOpen = !addIsOpen
                                        }
                                    )
                                    .pointerInput(Unit) {
                                        detectDragGestures(
                                            onDragEnd = {
                                                addIsOpen = !addIsOpen
                                            }
                                        ) { _, _ -> }
                                    }
                            )
                        }
                    }
                    1 -> {
                        Collect()
                    }
                    2 -> {
                        History()
                    }
                    else -> Setting()
                }
            }
        }
        AnimatedVisibility(
            visible = projectOpenItem != 0,
            enter = fadeIn()
                    + slideInVertically { height ->
                height * 2
            },
            exit = fadeOut() + slideOutVertically { height ->
                height * 2
            }
        ) {
            BackHandler {
                projectOpenItem = 0
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlackVariants)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = {
                            projectOpenItem = 0
                        }
                    )
                    .statusBarsPadding(),
                contentAlignment = Alignment.BottomCenter
            ) {
                when(projectOpenItem){
                    1 -> {
                        Text(text = "1")
                    }
                    2 -> {
                        SendString(
                            forums = forum,
                            forumsSelectedItem = sidebarSelectedItem,
                            fontSize = fontSize
                        )
                    }
                    3 -> {
                        Text(text = "3")
                    }
                }
            }
        }
    }
}