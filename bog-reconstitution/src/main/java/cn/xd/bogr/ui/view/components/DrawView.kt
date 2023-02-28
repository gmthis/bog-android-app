package cn.xd.bogr.ui.view.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.xd.bogr.ui.state.DrawState
import cn.xd.bogr.ui.state.rememberDrawState
import cn.xd.bogr.util.launchIO
import io.ak1.drawbox.DrawBox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrawViewMain(
    state: DrawState,
    scope: CoroutineScope
){
    DrawBox(
        modifier = Modifier
            .combinedClickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                },
                onDoubleClick = {
                    scope.launch(Dispatchers.IO) {
                        state.controlBarStatusHandOff()
                    }
                }
            ){
                scope.launch(Dispatchers.IO) {
                    state.closeControlBarMoreInfo()
                }
            },
        drawController = state.drawController,
        bitmapCallback = { imageBitmap, throwable ->
            scope.launchIO {
                state.save(imageBitmap, throwable)
            }
        },
        ending = {
            state.closeControlBarMoreInfo()
        }
    ){ undoCount, redoCount ->
        state.undoStatus = undoCount != 0
        state.redoStatus = redoCount != 0
    }
    ControlBar(state = state)
}

@Composable
fun ControlBar(state: DrawState){
    Box(modifier = Modifier
        .fillMaxHeight()
        .fillMaxWidth(
            animateFloatAsState(
                targetValue = if (state.controlBarStatus) 1f else 0f,
                animationSpec = tween(450)
            ).value
        )
        .padding(bottom = 40.dp, start = 20.dp, end = 20.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card {
            state.controlBarContent(this)
        }
    }
}

@Composable
fun DrawView(
    state: DrawState = rememberDrawState()
) {
    val scope = rememberCoroutineScope()
    Box(
        contentAlignment = Alignment.Center
    ) {
        DrawViewMain(state = state, scope = scope)
    }
}