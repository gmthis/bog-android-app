package cn.xd.bogr.ui.state

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import cn.xd.bogr.ui.theme.ExtendedTheme

class ContainerState(
    private var obscured: Color
){
    private var _composable: (@Composable BoxScope.() -> Unit)? = null
    private var endBlock: (() -> Unit)? = null
    private var isOpen by mutableStateOf(false)
    private var _contentAlignment = Alignment.BottomCenter
    private var _obscuredColor = obscured
    val currentStatus
        get() = isOpen
    val composable
        get() = _composable
    val contentAlignment
        get() = _contentAlignment
    val obscuredColor
        get() = _obscuredColor

    fun open(config: (Configuration.() -> Unit)? = null){
        if (!isOpen){
            Configuration(obscured = obscured).also{ config?.let { _ ->
                it.config()
                _obscuredColor = it.obscured
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
            _obscuredColor = obscuredColor
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

    class Configuration(
        var obscured: Color
    ){
        var composable: (@Composable BoxScope.() -> Unit)? = null
        var endBlock: (() -> Unit)? = null
        var contentAlignment = Alignment.BottomCenter
    }
}



@Composable
fun rememberContainerState(obscured: Color = ExtendedTheme.colors.obscured) = remember {
    ContainerState(obscured = obscured)
}