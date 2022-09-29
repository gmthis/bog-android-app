package cn.xd.bog.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Black,
    onPrimary= White,
    primaryVariant = Purple700,
    secondary = Teal200,
)

private val LightColorPalette = lightColors(
    primary = White,
    onPrimary= Black,

    primaryVariant = Purple700,
    secondary = Teal200

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

val tableColor = lightColors(
    primary = White,
    onPrimary = PinkText
)

val tableColorDark = darkColors(
    primary = Black,
    onPrimary = PinkText,
    onBackground = PinkText
)

val Field = lightColors(
    primary = PinkText,
    surface = WhiteBackgroundVariants
)

val FieldDark = darkColors(
    primary = PinkText,
    surface = BlackGroundVariants,
    onSurface = Color(0xffe2e2e2)
)

@Composable
fun BogTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}