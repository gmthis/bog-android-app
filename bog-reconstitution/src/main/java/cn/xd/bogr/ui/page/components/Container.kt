package cn.xd.bogr.ui.page.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import cn.xd.bogr.ui.state.ContainerState
import cn.xd.bogr.ui.theme.ExtendedTheme
import cn.xd.bogr.util.noRippleClickable

@Composable
fun Container(
    state: ContainerState,
    background: Color = ExtendedTheme.colors.obscured,
    enter: EnterTransition = fadeIn() + slideInVertically { height -> height * 2 },
    exit: ExitTransition = fadeOut() + slideOutVertically { height -> height * 2 },
    content: @Composable BoxScope.() -> Unit
){
    Box{
        content()
        AnimatedVisibility(
            state.currentStatus,
            enter = enter,
            exit = exit
        ) {
            BackHandler(onBack = state::close)
            Box(modifier = Modifier
                .noRippleClickable(onClick = state::close)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = state::close
                    ) { _, _ -> }
                }
                .fillMaxSize()
                .background(background),
                contentAlignment = state.contentAlignment
            ){
                Box(
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectDragGestures{ _, _ -> }
                        }
                ) {
                    state.composable?.invoke(this)
                }
            }
        }
    }
}