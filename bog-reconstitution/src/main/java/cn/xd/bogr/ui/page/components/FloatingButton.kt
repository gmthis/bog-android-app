package cn.xd.bogr.ui.page.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.xd.bogr.R
import cn.xd.bogr.ui.state.ContainerState
import cn.xd.bogr.ui.view.components.SendCard
import cn.xd.bogr.util.noRippleClickable
import cn.xd.bogr.util.rememberViewModel
import cn.xd.bogr.viewmodel.AppStatus

@Composable
fun FloatingButton(
    containerState: ContainerState,
    interiorContainerState: ContainerState
) {
    val viewModel = rememberViewModel<AppStatus>()
    val animateState by animateFloatAsState(if (interiorContainerState.currentStatus) 45f else 0f)
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {

        AnimatedVisibility(
            visible = interiorContainerState.currentStatus,
            enter = fadeIn() + slideInVertically { height ->
                height * 2
            },
            exit = fadeOut() + slideOutVertically { height ->
                height * 2
            }
        ) {
            FloatingList(containerState, close = interiorContainerState::close)
        }

        FloatingActionButton(
            onClick = {
                interiorContainerState.handoff()
            },
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add),
                contentDescription = stringResource(id = R.string.more),
                tint = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .size(viewModel.lllIconSize.dp)
                    .rotate(animateState)
            )
        }
    }
}

@Composable
fun FloatingList(containerState: ContainerState, close: () -> Unit) {
    val viewModel = rememberViewModel<AppStatus>()
    Column(
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        FloatingListItem(
            text = stringResource(id = R.string.recommend),
            icon = R.drawable.list,
            description = R.string.recommend,
            viewModel = viewModel
        ){}
        FloatingListItem(
            text = stringResource(id = R.string.send_new),
            icon = R.drawable.edit,
            description = R.string.send_new,
            viewModel = viewModel
        ){
            close()
            containerState.open{
                composable = { SendCard{
                    containerState.close()
                } }
            }
        }
        FloatingListItem(
            text = stringResource(id = R.string.search),
            icon = R.drawable.search,
            description = R.string.search,
            viewModel = viewModel
        ){}
    }
}

@Composable
fun FloatingListItem(
    text: String,
    icon: Int,
    description: Int,
    viewModel: AppStatus,
    onClick: () -> Unit
){
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.noRippleClickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically
        ){
            Box(modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.secondary)
            ){
                Text(
                    text = text,
                    fontSize = viewModel.fontSize.sp,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.padding(start = 10.dp))
            Box(modifier = Modifier
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.secondary)
            ){
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = stringResource(id = description),
                    tint = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier
                        .padding(4.dp)
                        .size(viewModel.lIconSize.dp)
                )
            }
        }
    }
}