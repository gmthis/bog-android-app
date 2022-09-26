package io.ak1.drawbox

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Created by akshay on 10/12/21
 * https://ak1.io
 */


@Composable
fun DrawBox(
    drawController: DrawController,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.background,
    ending: (() -> Unit)? = null,
    bitmapCallback: (ImageBitmap?, Throwable?) -> Unit,
    trackHistory: (undoCount: Int, redoCount: Int) -> Unit = { _, _ -> },
) = AndroidView(
    factory = {
        ComposeView(it).apply {
            setContent {
                LaunchedEffect(drawController) {
                    drawController.changeBgColor(backgroundColor)
                    drawController.trackBitmaps(this@apply, this, bitmapCallback)
                    drawController.trackHistory(this, trackHistory)
                }
                Canvas(modifier = modifier
                    .background(drawController.bgColor)
                    .graphicsLayer(alpha = 0.99f)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                drawController.insertNewPath(offset)
                                ending?.invoke()
                            }
                        ) { change, _ ->
                            val newPoint = change.position
                            drawController.updateLatestPath(newPoint)
                        }
                    }.fillMaxSize()) {
                    if (drawController.bgImage != null){
                        if (drawController.scaleBgImage == null){
                            val bgWidth = drawController.bgImage!!.width
                            var bgHeight = drawController.bgImage!!.height
                            val proportion = width.toFloat() / bgWidth
                            val matrix = Matrix()
                            matrix.setScale(proportion, proportion)
                            val createBitmap = Bitmap.createBitmap(
                                drawController.bgImage!!.asAndroidBitmap(),
                                0,
                                0,
                                bgWidth,
                                bgHeight,
                                matrix,
                                false
                            )
                            bgHeight = createBitmap.height
                            var offsetY = 0f
                            if (bgHeight < height){
                                offsetY = (height.toFloat() - bgHeight) / 2
                            }
                            val offset = Offset(0f,offsetY)
                            drawController.scaleBgImage = createBitmap.asImageBitmap()
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
    },
    modifier = modifier
)





