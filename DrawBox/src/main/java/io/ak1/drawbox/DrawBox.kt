package io.ak1.drawbox

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.core.graphics.scale
import kotlin.math.roundToInt

/**
 * Created by akshay on 10/12/21
 * https://ak1.io
 */

val bgColor = Color(0xFF7A7A7A)

@Composable
fun DrawBox(
    drawController: DrawController,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.background,
    ending: (() -> Unit)? = null,
    bitmapCallback: (ImageBitmap?, Throwable?) -> Unit,
    trackHistory: (undoCount: Int, redoCount: Int) -> Unit = { _, _ -> },
) {
    var size = Size.Zero
    LaunchedEffect(drawController) {
        drawController.changeBgColor(backgroundColor)
        drawController.trackBitmaps(size, this, bitmapCallback)
        drawController.trackHistory(this, trackHistory)
    }
    Box(modifier = modifier
        .pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { offset ->
                    val fl = (size.width - size.width / drawController.zoom) / 2
                    val fl2 = (size.height - size.height / drawController.zoom) / 2
                    drawController.insertNewPath(
                        Offset(
                            ((offset.x - drawController.offset.x) / drawController.zoom) + fl,
                            ((offset.y - drawController.offset.y) / drawController.zoom) + fl2
                        )
                    )
                    ending?.invoke()
                }
            ) { change, _ ->
                val fl = (size.width - size.width / drawController.zoom) / 2
                val fl2 = (size.height - size.height / drawController.zoom) / 2
                val offset = change.position
                drawController.updateLatestPath(
                    Offset(
                        ((offset.x - drawController.offset.x) / drawController.zoom) + fl,
                        ((offset.y - drawController.offset.y) / drawController.zoom) + fl2
                    )
                )
            }
        }
        .transformable(rememberTransformableState(onTransformation = { zoomChange, panChange, rotationChange ->
            drawController.zoom *= zoomChange
            drawController.offset += panChange
        }))
        .background(bgColor)
        .graphicsLayer(
            translationX = drawController.offset.x,
            translationY = drawController.offset.y,
            scaleX = drawController.zoom,
            scaleY = drawController.zoom
        )
    ) {
        Canvas(
            modifier = modifier
                .background(drawController.bgColor)
                .graphicsLayer(alpha = 0.99f)
                .fillMaxSize()
        ) {
            size = this.size
            val width = this.size.width
            val height = this.size.height
            if (drawController.bgImage != null) {
                if (drawController.scaleBgImage == null) {
                    val bgWidth = drawController.bgImage!!.width
                    var bgHeight = drawController.bgImage!!.height
                    val proportion = width / bgWidth
                    drawController.scaleBgImage = drawController.bgImage!!.asAndroidBitmap().scale(
                        (bgWidth * proportion).roundToInt(),
                        (bgHeight * proportion).roundToInt(),
                    ).asImageBitmap()
                    bgHeight = drawController.scaleBgImage!!.height
                    var offsetY = 0f
                    if (bgHeight < height) {
                        offsetY = (height - bgHeight) / 2
                    }
                    val offset = Offset(0f, offsetY)
                    drawController.bgOffset = offset
                }
                drawImage(
                    drawController.scaleBgImage!!,
                    topLeft = drawController.bgOffset!!
                )
            }
            drawController.pathList.forEach { pw ->
                drawPath(
                    createPath(pw.points),
                    color = pw.strokeColor,
                    alpha = pw.alpha,
                    style = Stroke(
                        width = pw.strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    ),
                    blendMode = pw.blendMode
                )
            }
        }
    }
}





