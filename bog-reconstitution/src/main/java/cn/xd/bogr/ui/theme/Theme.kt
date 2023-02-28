package cn.xd.bogr.ui.theme

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController

data class ExtendedColors(
    val obscured: Color,
    val dimmed: Color
)

private val defaultLightExtendedColors = ExtendedColors(
    obscured = lightObscured,
    dimmed = lightDimmed
)
private val defaultDarkExtendedColors = ExtendedColors(
    obscured = darkObscured,
    dimmed = darkDimmed,
)

val LocalExtendedColors = staticCompositionLocalOf {
    defaultLightExtendedColors
}

object ExtendedTheme{
    val colors
        @Composable
        get() = LocalExtendedColors.current
}

private val DarkColorScheme = darkColorScheme(
    primary = Primary_Pink_Dark,
    onPrimary = OnPrimary_white_Dark,
    secondary = Secondary_Pink_Dark,
    onSecondary = OnSecondary_Pink_Dark,
    tertiary = Tertiary_Pink_Dark,
    onTertiary = OnTertiary_Pink_Dark,

    background = Background_Dark,
    scrim = Scrim_Dark
)

private val LightColorScheme = lightColorScheme(
    primary = Primary_Pink,
    onPrimary = OnPrimary_white,
    secondary = Secondary_Pink,
    onSecondary = OnSecondary_Pink,
    tertiary = Tertiary_Pink,
    onTertiary = OnTertiary_Pink,

    background = Background_White,
    scrim = Scrim
)


object BogRippleTheme: RippleTheme{
    @Composable
    override fun defaultColor(): Color = MaterialTheme.colorScheme.secondary

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleTheme.defaultRippleAlpha(
        Color.Black,
        lightTheme = !isSystemInDarkTheme()
    )

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    rememberSystemUiController().setSystemBarsColor(
        darkIcons = !darkTheme,
        color = Color.Transparent,
        isNavigationBarContrastEnforced = false
    )

    val extendedColors = when{
        darkTheme -> defaultDarkExtendedColors
        else -> defaultLightExtendedColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
    ){
        CompositionLocalProvider(
            LocalRippleTheme provides BogRippleTheme,
            LocalExtendedColors provides extendedColors,
            LocalOverscrollConfiguration provides null,
            content = content
        )
    }
}

