package cn.xd.bogr

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.hilt.navigation.compose.hiltViewModel
import cn.xd.bogr.ui.theme.BogTheme
import cn.xd.bogr.viewmodel.AppStatus
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltAndroidApp
class ExampleApplication : Application()

@AndroidEntryPoint
class BogMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            BogTheme {
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
            }
        }
    }
}

val Context.dataStore by preferencesDataStore(name = "status")