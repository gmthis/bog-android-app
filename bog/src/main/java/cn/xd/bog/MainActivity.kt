package cn.xd.bog

import android.content.SharedPreferences
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import cn.xd.bog.ui.navigation.NavigationModel
import cn.xd.bog.ui.theme.BogTheme
import cn.xd.bog.viewmodel.AppStatus
import cn.xd.bog.viewmodel.Data
import android.graphics.Rect
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            BogTheme {
                NavigationModel()
            }
        }
    }
}
