package cn.xd.bog.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.xd.bog.R

@Composable
fun LeftTextRightIcon(
    fontSize: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LoadingText(
            text = stringResource(id = R.string.sidebarTitle),
            fontSize = (fontSize + 6).sp
        )
        IconButton(onClick = {}) {
            Icon(
                imageVector = ImageVector.vectorResource(
                    id = R.drawable.settings
                ),
                contentDescription = stringResource(id = R.string.sidebar_setting),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}