package cn.xd.bogr.ui.page.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.xd.bogr.R
import cn.xd.bogr.util.rememberViewModel
import cn.xd.bogr.viewmodel.AppStatus
import kotlinx.coroutines.launch

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