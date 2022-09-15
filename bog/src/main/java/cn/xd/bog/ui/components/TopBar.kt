package cn.xd.bog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.xd.bog.R
import cn.xd.bog.ui.theme.Black
import cn.xd.bog.ui.theme.White
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun TopBar(
    title: String? = null,
    fontSize: Int? = null,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    Column(
    ){
        TopAppBar(
            elevation = 0.dp,
            contentPadding = WindowInsets
                .statusBars
                .only(
                    WindowInsetsSides.Horizontal +
                            WindowInsetsSides.Top
                )
                .asPaddingValues()
        ){
            Box{
                LoadingText(
                    text = title,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    style = MaterialTheme.typography.h6,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = ((fontSize ?: 16) + 5).sp
                )
                IconButton(onClick = {
                    scope.launch {
                        drawerState.open()
                    }
                }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(
                            id = R.drawable.menu
                        ),
                        contentDescription = stringResource(
                            id = R.string.plate_menu),
                        modifier = Modifier
                            .fillMaxHeight()
                            .size(24.dp)
                    )
                }
            }
        }
    }
}