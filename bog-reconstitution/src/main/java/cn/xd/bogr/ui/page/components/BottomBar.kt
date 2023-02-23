package cn.xd.bogr.ui.page.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.xd.bogr.R
import cn.xd.bogr.util.rememberViewModel
import cn.xd.bogr.viewmodel.AppStatus

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