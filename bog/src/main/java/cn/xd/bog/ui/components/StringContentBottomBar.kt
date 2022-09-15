package cn.xd.bog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.xd.bog.R
import cn.xd.bog.ui.theme.Black
import cn.xd.bog.ui.theme.PinkText
import cn.xd.bog.ui.theme.Unselected
import cn.xd.bog.ui.theme.White

@Composable
fun StringContentBottomBar(
    fontSize: Int
) {
    val items = stringArrayResource(id = R.array.stringContentBottomItems)
    Column{
        BottomNavigation(
            elevation = 0.dp,
        ) {
            items.forEachIndexed { index, item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(0.25f)
                ) {
                    StringContentBottomBarIcon(
                        item = item,
                        when(index){
                            0 -> R.drawable.favorite
                            1 -> R.drawable.share
                            2 -> R.drawable.edit
                            else -> R.drawable.bookmark
                        }
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 5.dp))
                    Text(
                        text = item,
                        color = PinkText,
                        fontSize = fontSize.sp,
                    )
                }
            }
        }
        Spacer(
            modifier = Modifier
                .windowInsetsBottomHeight(WindowInsets.navigationBars)
                .fillMaxWidth()
                .background(
                    if (isSystemInDarkTheme()) {
                        Black
                    } else {
                        White
                    }
                )
        )
    }
}

@Composable
fun StringContentBottomBarIcon(item: String, id: Int) {
    Row {
        Icon(
            imageVector = ImageVector.vectorResource(
                id = id),
            contentDescription = item,
            tint = PinkText,
            modifier = Modifier.size(24.dp)
        )
    }
}