package cn.xd.bogr

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import cn.xd.bogr.ui.navigation.NavigationMap
import cn.xd.bogr.ui.theme.BogTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ExampleApplication : Application()

@AndroidEntryPoint
class BogMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val uiController = rememberSystemUiController()
            uiController.run {
                isSystemInDarkTheme().let {
                    setNavigationBarColor(
                        darkIcons = !it,
                        color = Color.Transparent,
                        navigationBarContrastEnforced = false
                    )
                    setStatusBarColor(
                        darkIcons = !it,
                        color = Color.Transparent
                    )
                }
            }
            BogTheme {
                NavigationMap()
            }
        }
    }
}