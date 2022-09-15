package cn.xd.bog.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = Main,
    primaryVariant = PinkMain,
    secondary = Teal200,
    onPrimary = Color.White
)

private val LightColorPalette = lightColors(
    primary = Main,
    primaryVariant = PinkMain,
    secondary = Teal200,
    onPrimary = Color.Black
)

@Composable
fun BogTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            if (darkTheme)
                Color(28,28,28,255)
            else
                Color(255, 255, 255,  255)
        )
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}