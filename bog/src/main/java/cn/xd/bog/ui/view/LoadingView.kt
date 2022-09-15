package cn.xd.bog.ui.view

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import cn.xd.bog.ui.theme.Black
import cn.xd.bog.ui.theme.PinkBackground
import cn.xd.bog.ui.theme.White
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun LoadingView(go: () -> Unit) {
    val uiController = rememberSystemUiController()
    uiController.setNavigationBarColor(
        darkIcons = !isSystemInDarkTheme(),
        color = if (isSystemInDarkTheme()){
            Black
        }else{
            PinkBackground
        }
    )
    uiController.setStatusBarColor(
        Color.Transparent,
        darkIcons = !isSystemInDarkTheme()
    )
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = if (isSystemInDarkTheme()){
            Black
        }else{
            PinkBackground
        }
    ) {
        Text(
            text = "B",
            fontSize = 150.sp,
            color = White,
            modifier = Modifier
                .wrapContentSize(Alignment.Center)
                .navigationBarsPadding()
        )
        LaunchedEffect(Unit){
            go()
        }
    }
}