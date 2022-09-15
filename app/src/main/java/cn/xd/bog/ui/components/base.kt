package cn.xd.bog.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import cn.xd.bog.ui.theme.PinkMainVariant
import cn.xd.bog.ui.theme.Unselected

@Composable
fun BottomItemIcon(item: String, id: Int, isChecked: Boolean){
    Icon(
        imageVector = ImageVector.vectorResource(
            id = id),
        contentDescription = item,
        tint = if (isChecked) {
            PinkMainVariant
        } else {
            Unselected
        },
        modifier = Modifier.size(24.dp)
    )
}