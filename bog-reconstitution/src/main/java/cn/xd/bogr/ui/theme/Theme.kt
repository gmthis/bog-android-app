package cn.xd.bogr.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColorScheme(
    primary = Primary_Pink_Dark,
    onPrimary = OnPrimary_white_Dark,
    secondary = Secondary_Pink_Dark,
    onSecondary = OnSecondary_Pink_Dark,
    tertiary = Tertiary_Pink_Dark,

    background = Background_Black
)

private val LightColorScheme = lightColorScheme(
    primary = Primary_Pink,
    onPrimary = OnPrimary_white,
    secondary = Secondary_Pink,
    onSecondary = OnSecondary_Pink,
    tertiary = Tertiary_Pink,

    background = Background_White

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
    ){
        CompositionLocalProvider(
            LocalRippleTheme provides BogRippleTheme,
            content = content
        )
    }
}

