package cn.xd.bogr.ui.view.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import cn.xd.bogr.R
import cn.xd.bogr.util.noRippleClickable
import cn.xd.bogr.util.rememberViewModel
import cn.xd.bogr.viewmodel.AppStatus

@Composable
fun SendCardIcon(
    modifier: Modifier = Modifier,
    icon: Int,
    description: Int,
    viewModel: AppStatus,
    tint: Color = MaterialTheme.colorScheme.onSecondary,
    onClick: () -> Unit
){
    Icon(
        painter = painterResource(id = icon),
        contentDescription = stringResource(id = description),
        modifier = modifier
            .size(viewModel.iconSize.dp)
            .noRippleClickable(onClick = onClick),
        tint = tint
    )
}

@Composable
fun SendCardStateBar(
    viewModel: AppStatus,
    rotate: Float,
    moreOpenStateRevise: () -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SendCardIcon(
                icon = R.drawable.arrow_drop_down,
                description = R.string.more,
                viewModel = viewModel,
                modifier = Modifier
                    .rotate(rotate)
            ) {
                moreOpenStateRevise()
            }
            Text(text = stringResource(id = R.string.send_new))
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SendCardIcon(
                icon = R.drawable.help,
                description = R.string.help,
                viewModel = viewModel
            ) {

            }
            SendCardIcon(
                icon = R.drawable.fullscreen,
                description = R.string.full_screen,
                viewModel = viewModel
            ) {

            }
            SendCardIcon(
                icon = R.drawable.close,
                description = R.string.close,
                viewModel = viewModel
            ) {

            }
        }
    }
}

@Composable
fun MoreInputField(isOpen: Boolean) {
    AnimatedVisibility(visible = isOpen) {

    }
}

@Composable
fun SendCard(

) {
    val viewModel = rememberViewModel<AppStatus>()
    var moreIsOpen by remember {
        mutableStateOf(false)
    }
    Card(
        shape = MaterialTheme.shapes.medium.copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp))
    ) {
        SendCardStateBar(
            viewModel = viewModel,
            rotate = animateFloatAsState(targetValue = if (moreIsOpen) 180f else 0f).value
        ){
            moreIsOpen = !moreIsOpen
        }
        MoreInputField(isOpen = moreIsOpen)
    }
}
