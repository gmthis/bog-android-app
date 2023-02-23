package cn.xd.bogr.ui.page.components

import androidx.compose.foundation.layout.RowScope
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
fun DetailsBottomBar() {
    val viewModel = rememberViewModel<AppStatus>()
    val itemsInfo = stringArrayResource(id = R.array.details_bottom_bar_items_info)
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background
    ) {
        DetailsNavigationBarItem(itemsInfo[0], R.drawable.favorite, viewModel) {}
        DetailsNavigationBarItem(itemsInfo[1], R.drawable.share, viewModel) {}
        DetailsNavigationBarItem(itemsInfo[2], R.drawable.edit, viewModel) {}
        DetailsNavigationBarItem(itemsInfo[3], R.drawable.bookmark, viewModel) {}
    }
}

@Composable
fun RowScope.DetailsNavigationBarItem(
    itemInfo: String,
    painter: Int,
    viewModel: AppStatus,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = false,
        onClick = onClick,
        label = { Text(text = itemInfo, fontSize = viewModel.sssFontSize.sp) },
        icon = {
            Icon(
                painter = painterResource(id = painter),
                contentDescription = itemInfo,
                modifier = Modifier.size(viewModel.lIconSize.dp)
            )
        },
        colors = NavigationBarItemDefaults.colors(
            unselectedIconColor = MaterialTheme.colorScheme.onSecondary,
            unselectedTextColor = MaterialTheme.colorScheme.onSecondary
        )
    )
}