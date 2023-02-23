package cn.xd.bogr.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import cn.xd.bogr.ui.state.ContainerState
import cn.xd.bogr.ui.theme.ExtendedTheme

@Composable
fun Container(
    state: ContainerState,
    content: @Composable () -> Unit
){
    content()
    val back = {
        state.isOpen = false
        state.endBlock?.invoke()
        Unit
    }
    AnimatedVisibility(
        state.isOpen,
        enter = fadeIn()
                + slideInVertically { height ->
            height * 2
        },
        exit = fadeOut() + slideOutVertically { height ->
            height * 2
        }
    ) {
        BackHandler(onBack = back)
        Box(modifier = Modifier
            .fillMaxSize()
            .background(ExtendedTheme.colors.obscured)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
            ){
                back()
            }
            .pointerInput(Unit){
               detectDragGestures(
                   onDragEnd = {
                       back()
                   }
               ){_,_ ->}
            },
            contentAlignment = state.contentAlignment
        ){
            state.composable?.invoke()
        }
    }
}