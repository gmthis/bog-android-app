package cn.xd.bog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import cn.xd.bog.data.viewmodel.MainViewModel
import cn.xd.bog.entity.PlateInfo
import cn.xd.bog.ui.components.*
import cn.xd.bog.ui.theme.BogTheme
import cn.xd.bog.ui.theme.PinkMainVariant
import cn.xd.bog.ui.theme.PinkSecondary
import cn.xd.bog.ui.theme.Unselected
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private val viewModel = MainViewModel()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BogTheme {
                NavigableUtil(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun UIMain(navController: NavHostController, scope: CoroutineScope) {
    val scaffoldState = rememberScaffoldState()
    val lazyListState = rememberLazyListState()

    var selectedItem by remember {
        mutableStateOf(0)
    }
    val items = stringArrayResource(id = R.array.bottomItems)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar {
                Box {
                    Text(
                        text = viewModel.plateMap[viewModel.plateID.value]?.name ?: "",
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                        style = MaterialTheme.typography.h6,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = {
                        scope.launch {
                            scaffoldState.drawerState.open()
                        }
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(
                                id = R.drawable.menu
                            ),
                            contentDescription = "板块选取",
                            modifier = Modifier
                                .fillMaxHeight()
                                .size(24.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            BottomNavigation {
                items.forEachIndexed { index, item ->
                    val isChecked = selectedItem == index
                    BottomNavigationItem(
                        icon = {
                            when (index) {
                                0 -> {
                                    BottomItemIcon(item = item, id = R.drawable.home, isChecked)
                                }
                                1 -> {
                                    BottomItemIcon(item = item, id = R.drawable.favorite, isChecked)
                                }
                                2 -> {
                                    BottomItemIcon(item = item, id = R.drawable.history, isChecked)
                                }
                                else -> {
                                    BottomItemIcon(item = item, id = R.drawable.settings, isChecked)
                                }
                            }
                        },
                        label = {
                            Text(
                                text = item,
                                color = if (isChecked) {
                                    PinkMainVariant
                                } else {
                                    Unselected
                                },
                                fontSize = 10.sp,
                            )
                        },
                        selected = isChecked,
                        onClick = { selectedItem = index }
                    )
                }
            }
        },
        drawerContent = {
            DrawerContent(viewModel.plateList, scaffoldState.drawerState, scope, lazyListState)
        },
        snackbarHost = {
            SnackbarHost(
                hostState = it
            ) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    elevation = 2.dp
                ) {
                    Snackbar(
                        backgroundColor = Color.White
                    ) {
                        Text(
                            text = it.message,
                            color = PinkMainVariant
                        )
                    }
                }
            }
        }
    ) {
        Surface(
            modifier = Modifier.padding(it)
        ) {
            when (selectedItem) {
                0 -> {
                    Plate(
                        viewModel,
                        navController,
                        lazyListState,
                        scaffoldState.snackbarHostState,
                        scope
                    )
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
}

@Composable
fun DrawerContent(
    plateEntity: MutableList<PlateInfo>,
    drawerState: DrawerState,
    scope: CoroutineScope,
    lazyListState: LazyListState
) {
    var checked by remember {
        mutableStateOf(0)
    }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(id = R.string.sidebarTitle),
                fontSize = 20.sp
            )
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ){
                Icon(
                    imageVector = ImageVector.vectorResource(
                        id = R.drawable.settings
                    ),
                    contentDescription = "板块设置",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            onClick = {

                            },
                            indication = null,
                            interactionSource = MutableInteractionSource()
                        )
                )
            }
        }
        LazyColumn {
            itemsIndexed(plateEntity) { index, item ->
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .size(60.dp)
                        .padding(15.dp, 2.dp, 15.dp, 2.dp)
                        .clickable(
                            onClick = {
                                checked = index
                                viewModel.plateID.value = item.id
                                viewModel.noMore.value = false
                                scope.launch {
                                    lazyListState.scrollToItem(0)
                                    drawerState.close()
                                }
                                viewModel.pullNewPlateContent(item.id, 1)
                            },
                            indication = null,
                            interactionSource = MutableInteractionSource()
                        ),
                    backgroundColor = if (index == checked)
                        PinkSecondary
                    else
                        MaterialTheme.colors.background,
                    elevation = 0.dp
                ) {
                    Text(
                        text = item.name,
                        color = when {
                            checked == index -> {
                                PinkMainVariant
                            }
                            isSystemInDarkTheme() -> {
                                MaterialTheme.colors.onPrimary
                            }
                            !isSystemInDarkTheme() -> {
                                MaterialTheme.colors.onPrimary
                            }
                            else -> {
                                MaterialTheme.colors.onPrimary
                            }
                        },
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentHeight(Alignment.CenterVertically)
                            .padding(start = 30.dp)
                    )
                }
            }
        }
    }
}