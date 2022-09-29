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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.xd.bog.entity.Forum
import cn.xd.bog.entity.ForumContentInfo
import cn.xd.bog.entity.SingleContentInfo
import cn.xd.bog.ui.components.*
import cn.xd.bog.ui.page.*
import cn.xd.bog.ui.theme.*
import cn.xd.bog.viewmodel.AppStatus
import cn.xd.bog.viewmodel.Data
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
    appStatus: AppStatus,
    data: Data,
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
    var addIsOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var projectOpenItem by rememberSaveable {
        mutableStateOf(0)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
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
            snackbarHost = {
                SnackbarHost(hostState = it)
            },
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
                                    .background(
                                        if (isSystemInDarkTheme())
                                            BlackVariants
                                        else
                                            WhiteVariants
                                    )
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
                    .statusBarsPadding(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = {
                                    projectOpenItem = 0
                                }
                            )
                    )
                    when(projectOpenItem){
                        1 -> {
                            Text(text = "1")
                        }
                        2 -> {
                            SendString(
                                forums = forum,
                                forumsSelectedItem = sidebarSelectedItem,
                                fontSize = fontSize,
                                close = {
                                    projectOpenItem = 0
                                },
                                appStatus = appStatus,
                                data = data
                            )
                        }
                        3 -> {
                            Text(text = "3")
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentHeight(Alignment.Top)
                ){
                    SnackbarHost(
                        hostState = appStatus.snackbarHostState,
                        snackbar = {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 20.dp, vertical = 15.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colors.background)
                            ) {
                                Text(
                                    text = it.message,
                                    modifier = Modifier.padding(10.dp).fillMaxWidth()
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}