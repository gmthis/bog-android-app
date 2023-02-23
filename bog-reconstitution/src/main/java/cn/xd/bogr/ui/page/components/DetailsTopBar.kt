package cn.xd.bogr.ui.page.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cn.xd.bogr.R
import cn.xd.bogr.net.entity.Content
import cn.xd.bogr.util.rememberViewModel
import cn.xd.bogr.viewmodel.AppStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsTopBar(content: Content) {
    val viewModel = rememberViewModel<AppStatus>()
    CenterAlignedTopAppBar(
        title = {
            Text(text = "Po.${content.id}")
        },
        navigationIcon = {
            Icon(
                painter = painterResource(id = R.drawable.back),
                contentDescription = stringResource(id = R.string.back),
                modifier = Modifier
                    .size(viewModel.sIconSize.dp)
            )
        },
        actions = {
            Icon(
                painter = painterResource(id = R.drawable.notifications_off),
                contentDescription = stringResource(id = R.string.remind),
                modifier = Modifier
                    .size(viewModel.sIconSize.dp)
            )
            Icon(
                painter = painterResource(id = R.drawable.sort),
                contentDescription = stringResource(id = R.string.sort),
                modifier = Modifier
                    .size(viewModel.sIconSize.dp)
            )
            Icon(
                painter = painterResource(id = R.drawable.filter_list),
                contentDescription = stringResource(id = R.string.filter),
                modifier = Modifier
                    .size(viewModel.sIconSize.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}