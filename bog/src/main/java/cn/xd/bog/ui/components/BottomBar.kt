package cn.xd.bog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.xd.bog.R
import cn.xd.bog.ui.theme.Black
import cn.xd.bog.ui.theme.PinkText
import cn.xd.bog.ui.theme.Unselected
import cn.xd.bog.ui.theme.White

@Composable
fun BottomBar(selectedItem: Int, select: (Int) -> Unit) {
    val items = stringArrayResource(id = R.array.bottomItems)
    Column{
        BottomNavigation(
            elevation = 0.dp,
        ) {
            items.forEachIndexed { index, item ->
                val isChecked = selectedItem == index
                BottomNavigationItem(
                    icon = {
                        when (index) {
                            0 -> {
                                BottomItemIcon(item = item, id = R.drawable.home, isChecked)
                            }
                            1 -> {
                                BottomItemIcon(item = item, id = R.drawable.favorite, isChecked)
                            }
                            2 -> {
                                BottomItemIcon(item = item, id = R.drawable.history, isChecked)
                            }
                            else -> {
                                BottomItemIcon(item = item, id = R.drawable.settings, isChecked)
                            }
                        }
                    },
                    label = {
                        Text(
                            text = item,
                            color = if (isChecked) {
                                PinkText
                            } else {
                                Unselected
                            },
                            fontSize = 10.sp,
                        )
                    },
                    selected = isChecked,
                    onClick = { select(index) }
                )
            }
        }
        Spacer(
            modifier = Modifier
                .windowInsetsBottomHeight(WindowInsets.navigationBars)
                .fillMaxWidth()
                .background(
                    if(isSystemInDarkTheme()){
                        Black
                    }else{
                        White
                    }
                )
        )
    }
}