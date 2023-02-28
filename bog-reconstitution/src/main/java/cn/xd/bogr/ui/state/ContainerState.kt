package cn.xd.bogr.ui.state

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment

class ContainerState{
    private var _composable: (@Composable BoxScope.() -> Unit)? = null
    private var endBlock: (() -> Unit)? = null
    private var isOpen by mutableStateOf(false)
    private var _contentAlignment = Alignment.BottomCenter
    val currentStatus
        get() = isOpen
    val composable
        get() = _composable
    val contentAlignment
        get() = _contentAlignment

    fun open(config: (Configuration.() -> Unit)? = null){
        if (!isOpen){
            Configuration().also{ config?.let { _ ->
                it.config()
                _composable = it.composable
                endBlock = it.endBlock
                _contentAlignment = it.contentAlignment
            } }
            isOpen = true
        }
    }

    fun close(){
        if (isOpen){
            isOpen = false
            endBlock?.invoke().also {
                endBlock = null
            }
            _composable = null
            _contentAlignment = Alignment.BottomCenter
        }
    }

    fun handoff(config: (Configuration.() -> Unit)? = null){
        if (isOpen){
            close()
        }else{
            open(config)
        }
    }

    class Configuration{
        var composable: (@Composable BoxScope.() -> Unit)? = null
        var endBlock: (() -> Unit)? = null
        var contentAlignment = Alignment.BottomCenter
    }
}



@Composable
fun rememberContainerState() = remember {
    ContainerState()
}