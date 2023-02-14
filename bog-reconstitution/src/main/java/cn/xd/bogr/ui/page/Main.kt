package cn.xd.bogr.ui.page

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.xd.bogr.R
import cn.xd.bogr.util.rememberViewModel
import cn.xd.bogr.viewmodel.AppStatus
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main() {
    val viewModel = rememberViewModel<AppStatus>()
    val context = LocalContext.current as ComponentActivity
    val scope = rememberCoroutineScope()

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
        drawerContent = { Drawer() }
    ) {
        Scaffold(
            topBar = { TopBar() },
            bottomBar = {},
            floatingActionButton = {},
            snackbarHost = {}
        ) {
            Surface(Modifier.padding(it)) {

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(){
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
        }
    )
}

@Composable
fun Drawer() {
    val viewModel = rememberViewModel<AppStatus>()

    ModalDrawerSheet(
        modifier = Modifier.fillMaxWidth(0.6f)
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
                    fontSize = viewModel.lFontSize.sp
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
            ){
                itemsIndexed(viewModel.forumList){ index, item ->
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = item.name,
                                fontSize = viewModel.fontSize.sp
                            )
                        },
                        selected = viewModel.forumSelected == index,
                        onClick = {
                            viewModel.forumSelected = index
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondary,
                        )
                    )
                }
            }
        }
    }
}