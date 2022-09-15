package cn.xd.bog.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cn.xd.bog.R
import cn.xd.bog.ui.theme.PinkBackground
import cn.xd.bog.ui.theme.White

@Composable
fun FloatingButton(
    isOpen: Boolean,
    toggle: () -> Unit
) {
    val animateState by animateFloatAsState(if (isOpen) 45f else 0f)

    FloatingActionButton(
        onClick = { toggle() },
        backgroundColor = PinkBackground
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.add),
            contentDescription = stringResource(id = R.string.more),
            tint = White,
            modifier = Modifier
                .size(30.dp)
                .rotate(animateState)
        )
    }
}