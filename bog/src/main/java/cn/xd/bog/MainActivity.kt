package cn.xd.bog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import cn.xd.bog.ui.navigation.NavigationModel
import cn.xd.bog.ui.theme.BogTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val uiController = rememberSystemUiController()
            uiController.run {
                setNavigationBarColor(
                    darkIcons = !isSystemInDarkTheme(),
                    color = Color.Transparent,
                    navigationBarContrastEnforced = false
                )
                setStatusBarColor(
                    Color.Transparent,
                    darkIcons = !isSystemInDarkTheme()

                )
            }
            BogTheme {
                NavigationModel()
            }
        }
    }
}