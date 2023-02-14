package io.ak1.drawbox

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

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
    var size by remember {
        mutableStateOf(Size.Zero)
    }
    LaunchedEffect(drawController) {
        drawController.changeBgColor(backgroundColor)
        drawController.trackBitmaps(size, this, bitmapCallback)
        drawController.trackHistory(this, trackHistory)
    }
    BoxWithConstraints {
        Box(modifier = modifier
            .background(bgColor)
            .fillMaxSize()
            .wrapContentSize(unbounded = true)
        ) {

            Canvas(
                modifier = modifier
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->

                                val x = drawController.offset.x
                                val y = drawController.offset.y
                                val xo = (size.width - size.width / drawController.zoom) / 2
                                val yo = (size.height - size.height / drawController.zoom) / 2
                                val ox = (size.width / 2)
                                val oy = (size.height / 2)
                                val r = abs(drawController.rotation).let {
                                    val fit = when {
                                        it < 5 -> 0f
                                        it < 50 && it > 40 -> 45f
                                        it < 95 && it > 85 -> 90f
                                        it < 140 && it > 130 -> 135f
                                        it < 185 && it > 175 -> 180f
                                        it < 230 && it > 220 -> 225f
                                        it < 275 && it > 265 -> 270f
                                        it < 320 && it > 310 -> 315f
                                        it > 355 -> 360f
                                        else -> it
                                    }
                                    if (drawController.rotation < 0) -fit else fit
                                }

                                val xc =
                                    ((offset.x - x) / drawController.zoom) + xo
                                val yc =
                                    ((offset.y - y) / drawController.zoom) + yo


                                val xr =
                                    (xc - ox) * cos(-r * PI / 180) - (yc - oy) * sin(
                                        -r * PI / 180
                                    ) + ox
                                val yr =
                                    (yc - oy) * cos(-r * PI / 180) + (xc - ox) * sin(
                                        -r * PI / 180
                                    ) + oy

                                println("----------------------------------------------------\n" +
                                        "屏幕宽度: ${size.width}\n" +
                                        "屏幕高度: ${size.height}\n" +
                                        "按下的位置: ofx: ${offset.x} ofy: ${offset.y}\n" +
                                        "旋转角度: r: ${drawController.rotation}\n" +
                                        "缩放比值: t: ${drawController.zoom}\n" +
                                        "偏移量: x: $x y: $y\n" +
                                        "画布中心: ox: $ox oy: $oy\n" +
                                        "带有缩放的偏移量: xo: $xo yo: $yo\n" +
                                        "计算旋转之后的位置: xr: $xr yr: $yr\n" +
                                        "计算偏移之后的位置: xc: $xc yc: $yc")

                                drawController.insertNewPath(
                                    Offset(
                                        xr.toFloat(),
                                        yr.toFloat()
                                    )
                                )
                                ending?.invoke()
                            },
                        ) { change, _ ->
                            val offset = change.position

                            val x = drawController.offset.x
                            val y = drawController.offset.y
                            val xo = (size.width - size.width / drawController.zoom) / 2
                            val yo = (size.height - size.height / drawController.zoom) / 2
                            val ox = (size.width / 2)
                            val oy = (size.height / 2)
                            val r = abs(drawController.rotation).let {
                                val fit = when {
                                    it < 5 -> 0f
                                    it < 50 && it > 40 -> 45f
                                    it < 95 && it > 85 -> 90f
                                    it < 140 && it > 130 -> 135f
                                    it < 185 && it > 175 -> 180f
                                    it < 230 && it > 220 -> 225f
                                    it < 275 && it > 265 -> 270f
                                    it < 320 && it > 310 -> 315f
                                    it > 355 -> 360f
                                    else -> it
                                }
                                if (drawController.rotation < 0) -fit else fit
                            }

                            val xc =
                                ((offset.x - x) / drawController.zoom) + xo
                            val yc =
                                ((offset.y - y) / drawController.zoom) + yo


                            val xr =
                                (xc - ox) * cos(-r * PI / 180) - (yc - oy) * sin(
                                    -r * PI / 180
                                ) + ox
                            val yr =
                                (yc - oy) * cos(-r * PI / 180) + (xc - ox) * sin(
                                    -r * PI / 180
                                ) + oy


                            drawController.updateLatestPath(
                                Offset(
                                    xr.toFloat(),
                                    yr.toFloat()
                                )
                            )
                        }
                    }
                    .transformable(rememberTransformableState(onTransformation = { zoomChange, panChange, rotationChange ->
                        drawController.zoom *= zoomChange
                        drawController.offset += panChange
                        val r = drawController.rotation + rotationChange
                        if (r >= -360 && r <= 360) {
                            drawController.rotation += rotationChange
                            return@rememberTransformableState
                        }
                        if (r > 360) {
                            drawController.rotation = r - 360
                            return@rememberTransformableState
                        }
                        drawController.rotation = r + 360

                    }))
                    .graphicsLayer(
                        translationX = drawController.offset.x,
                        translationY = drawController.offset.y,
                        scaleX = drawController.zoom,
                        scaleY = drawController.zoom,
                        rotationZ = abs(drawController.rotation).let {
                            val fit = when {
                                it < 5 -> 0f
                                it < 50 && it > 40 -> 45f
                                it < 95 && it > 85 -> 90f
                                it < 140 && it > 130 -> 135f
                                it < 185 && it > 175 -> 180f
                                it < 230 && it > 220 -> 225f
                                it < 275 && it > 265 -> 270f
                                it < 320 && it > 310 -> 315f
                                it > 355 -> 360f
                                else -> it
                            }
                            if (drawController.rotation < 0) -fit else fit
                        },
                        alpha = 0.9999f
                    )
                    .background(drawController.bgColor)
                    .run {
                        if (drawController.bgImage != null){
                            with(LocalDensity.current){
                                this@run.size(drawController.bgImage!!.width.toDp(), drawController.bgImage!!.height.toDp())
                            }
                        }else{
                            size(this@BoxWithConstraints.maxWidth, this@BoxWithConstraints.maxHeight)
                        }
                    }
            ) {
                size = if (drawController.bgImage != null){
                    Size(drawController.bgImage!!.width.toFloat(), drawController.bgImage!!.height.toFloat())
                }else{
                    this.size
                }
                val width = size.width
                val height = size.height
                if (drawController.bgImage != null) {
//                if (drawController.scaleBgImage == null) {
//                    val bgWidth = drawController.bgImage!!.width
//                    var bgHeight = drawController.bgImage!!.height
//                    val proportion = width / bgWidth
//                    drawController.scaleBgImage = drawController.bgImage!!.asAndroidBitmap().scale(
//                        (bgWidth * proportion).roundToInt(),
//                        (bgHeight * proportion).roundToInt(),
//                    ).asImageBitmap()
//                    bgHeight = drawController.scaleBgImage!!.height
//                    var offsetY = 0f
//                    if (bgHeight < height) {
//                        offsetY = (height - bgHeight) / 2
//                    }
//                    val offset = Offset(0f, offsetY)
//                    drawController.bgOffset = offset
//                }
                    drawImage(
                        drawController.bgImage!!,
                        topLeft = Offset.Zero
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

}