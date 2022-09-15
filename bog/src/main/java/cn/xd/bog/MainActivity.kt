package cn.xd.bog

import android.content.SharedPreferences
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import cn.xd.bog.ui.navigation.NavigationModel
import cn.xd.bog.ui.theme.BogTheme
import cn.xd.bog.viewmodel.AppStatus
import cn.xd.bog.viewmodel.Data


class MainActivity : ComponentActivity() {
    private var sharedPreferences: SharedPreferences? = null
    private var  data: Data? = null
    private var  appStatus: AppStatus? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (sharedPreferences == null){
            sharedPreferences = getSharedPreferences("app_status", MODE_PRIVATE)
            data = Data(sharedPreferences!!)
            appStatus = AppStatus(sharedPreferences!!, data!!)
        }
        setContent {
            BogTheme {
//                BackHandler {
//                    moveTaskToBack(true)
//                }
                NavigationModel(data!!, appStatus!!, back = {
                    moveTaskToBack(true)
                })
            }
        }
    }
}
