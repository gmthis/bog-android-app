package cn.xd.bogr

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import cn.xd.bogr.ui.navigation.Container
import cn.xd.bogr.ui.navigation.NavigationMap
import cn.xd.bogr.ui.theme.BogTheme
import cn.xd.bogr.util.rememberViewModel
import cn.xd.bogr.viewmodel.AppStatus
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
            BogTheme {
                NavigationMap()
            }
        }
    }
}