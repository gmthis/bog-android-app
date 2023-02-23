package cn.xd.bogr.ui.state

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

class ContainerState(
    var composable: (@Composable () -> Unit)? = null,
    var endBlock: (() -> Unit)? = null
){
    var isOpen by mutableStateOf(false)
    var contentAlignment = Alignment.BottomCenter
}

@Composable
fun rememberContainerState(
    composable: (@Composable () -> Unit)? = null,
    endBlock: (() -> Unit)? = null
) = remember {
    ContainerState(composable, endBlock)
}