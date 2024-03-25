package cn.xd.bogr.ui.view.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.xd.bogr.util.rememberViewModel
import cn.xd.bogr.viewmodel.AppStatus

@OptIn(ExperimentalFoundationApi::class)
@Composable
inline fun Popover(
    title: String,
    crossinline block: @Composable ColumnScope.() -> Unit
) {
    val viewModel = rememberViewModel<AppStatus>()
    Card(
        modifier = Modifier.fillMaxWidth(0.8f)
            .combinedClickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                },
                onClick = {},
                onLongClick = {},
                onDoubleClick = {}
            ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(13.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = title, maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = viewModel.fontSize.sp
            )
            Divider()
            block()
        }
    }
}

@Composable
fun PopoverItem(
    modifier: Modifier = Modifier,
    prompt: String,
    content: String,
){
    val viewModel = rememberViewModel<AppStatus>()
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = prompt,
            fontSize = viewModel.fontSize.sp
        )
        Text(
            text = content,
            fontSize = viewModel.fontSize.sp
        )
    }
}