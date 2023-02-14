package cn.xd.bogr.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun CoroutineScope.launchIO(block: suspend CoroutineScope.() -> Unit) = launch(Dispatchers.IO, block =  block)

@Composable
inline fun <reified VM: ViewModel> rememberViewModel(): VM {
    return hiltViewModel(LocalContext.current as ViewModelStoreOwner)
}