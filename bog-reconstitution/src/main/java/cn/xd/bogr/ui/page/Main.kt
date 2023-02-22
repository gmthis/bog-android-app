package cn.xd.bogr.ui.page

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.xd.bogr.R
import cn.xd.bogr.ui.navigation.BottomNavigationMap
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
    BackHandler(
    ) {
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
        gesturesEnabled = viewModel.pageSelected == 0
    ) {
        Scaffold(
            topBar = { TopBar() },
            bottomBar = { BottomBar() },
            floatingActionButton = {},
            snackbarHost = {},
        ) {
            Surface(Modifier.padding(it)) {
                BottomNavigationMap(listState)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    val viewModel = rememberViewModel<AppStatus>()
    val scope = rememberCoroutineScope()
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = viewModel.forumList[viewModel.forumSelected].name,
                fontSize = viewModel.l4FontSize.sp,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            Icon(
                painter = painterResource(id = R.drawable.menu),
                contentDescription = stringResource(id = R.string.plate_menu),
                modifier = Modifier
                    .size(viewModel.iconSize.dp)
                    .clickable {
                        scope.launch {
                            viewModel.drawerState.open()
                        }
                    }
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
    )
}

@Composable
fun BottomBar() {
    val viewModel = rememberViewModel<AppStatus>()
    val itemsInfo = stringArrayResource(id = R.array.navigation_bar_items_info)
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        itemsInfo.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = viewModel.pageSelected == index,
                onClick = {
                    viewModel.pageSelected = index
                    viewModel.bottomNavController.popBackStack()
                    viewModel.bottomNavController.navigate(index.toString())
                },
                label = { Text(text = item, fontSize = viewModel.lFontSize.sp) },
                icon = {
                    Icon(
                        painter = when (index) {
                            0 -> painterResource(id = R.drawable.home)
                            1 -> painterResource(id = R.drawable.favorite)
                            2 -> painterResource(id = R.drawable.history)
                            else -> painterResource(id = R.drawable.settings)
                        },
                        contentDescription = item,
                        modifier = Modifier.size(viewModel.iconSize.dp)
                    )
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.secondary,
                    selectedIconColor = MaterialTheme.colorScheme.onSecondary,
                    selectedTextColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    }
}

@Composable
fun Drawer(listState: LazyListState) {
    val viewModel = rememberViewModel<AppStatus>()
    val scope = rememberCoroutineScope()

    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxWidth(0.8f)
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(horizontal = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.all_forum),
                    fontSize = viewModel.l4FontSize.sp
                )
                Icon(
                    painter = painterResource(id = R.drawable.settings),
                    contentDescription = stringResource(id = R.string.forum_setting),
                    modifier = Modifier.size(viewModel.iconSize.dp)
                )
            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(viewModel.itemsSpacing.dp),
                contentPadding = PaddingValues(horizontal = 15.dp)
            ) {
                itemsIndexed(viewModel.forumList) { index, item ->
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = item.name,
                                fontSize = viewModel.fontSize.sp
                            )
                        },
                        selected = viewModel.forumSelected == index,
                        onClick = {
                            viewModel.saveForumListOffset(listState)
                            viewModel.selected(index, listState)
                            scope.launch {
                                viewModel.drawerState.close()
                            }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondary,
                            unselectedContainerColor = MaterialTheme.colorScheme.background,
                            selectedTextColor = MaterialTheme.colorScheme.onSecondary
                        )
                    )
                }
            }
        }
    }
}