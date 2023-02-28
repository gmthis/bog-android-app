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

fun cross(p1: Pair<Float, Float>, p2: Pair<Float, Float>, p: Pair<Float, Float>): Float {
    return (p2.first - p1.first) * (p.second - p1.second) - (p.first - p1.first) * (p2.second - p1.second)
}

fun inRectangle(
    lt: Pair<Float, Float>,
    lb: Pair<Float, Float>,
    rb: Pair<Float, Float>,
    rt: Pair<Float, Float>,
    p: Pair<Float, Float>,
    o: Pair<Float, Float>,
    r: Float
): Boolean {
    val ltr = thePointRotatesAroundAPoint(lt, o, r)
    val lbr = thePointRotatesAroundAPoint(lb, o, r)
    val rbr = thePointRotatesAroundAPoint(rb, o, r)
    val rtr = thePointRotatesAroundAPoint(rt, o, r)
    return (cross(ltr, lbr, p) * cross(rbr, rtr, p) >= 0 && cross(lbr, rbr, p) * cross(
        rtr,
        ltr,
        p
    ) >= 0)
}

fun thePointRotatesAroundAPoint(
    p: Pair<Float, Float>,
    o: Pair<Float, Float>,
    r: Float
): Pair<Float, Float> {
    return ((p.first - o.first) * cos(r * PI / 180) - (p.second - o.second) * sin(
        r * PI / 180
    ) + o.first).toFloat() to
            ((p.second - o.second) * cos(r * PI / 180) + (p.first - o.first) * sin(
                r * PI / 180
            ) + o.second).toFloat()
}

fun angularFit(r: Float): Float = abs(r).let {
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
    if (r < 0) -fit else fit
}

fun scaleFit(zoom: Float): Float = if (zoom > 0.995f && zoom < 1.05) { 1f } else { zoom }

fun offsetFit(of: Float): Float = if (of > -20 && of < 20) { 0f } else { of }

fun correctLocation(
    drawController: DrawController,
    offset: Offset
): Triple<Float, Float, Boolean>{
    val zoom = scaleFit(drawController.zoom)
    val x = offsetFit(drawController.offset.x)
    val y = offsetFit(drawController.offset.y)
    val ox = (drawController.size.width / 2)
    val oy = (drawController.size.height / 2)

    val ltx = ((drawController.size.width / 2) - ((drawController.size.width * zoom) / 2)) + x
    val lty = ((drawController.size.height / 2) - (drawController.size.height * zoom / 2)) + y
    val lby = lty + drawController.size.height * zoom
    val rbx = ltx + drawController.size.width * zoom

    val r = angularFit(drawController.rotation)

    if (!inRectangle(
            ltx to lty,
            ltx to lby,
            rbx to lby,
            rbx to lty,
            offset.x to offset.y,
            ox + x to oy + y,
            r
        )
    ) {
        return Triple(0f, 0f, true)
    }

    val xc =
        ((offset.x - x) / zoom) + (drawController.size.width - drawController.size.width / zoom) / 2
    val yc =
        ((offset.y - y) / zoom) + (drawController.size.height - drawController.size.height / zoom) / 2

    return Triple(
        ((xc - ox) * cos(-r * PI / 180) - (yc - oy) * sin(
        -r * PI / 180) + ox) . toFloat(),
        ((yc - oy) * cos(-r * PI / 180) + (xc - ox) * sin(
            -r * PI / 180) + oy).toFloat(),
        false)
}

@Composable
fun DrawBox(
    drawController: DrawController,
    modifier: Modifier = Modifier,
    ending: (() -> Unit)? = null,
    bitmapCallback: (ImageBitmap?, Throwable?) -> Unit,
    trackHistory: (undoCount: Int, redoCount: Int) -> Unit = { _, _ -> },
) {
    LaunchedEffect(drawController) {
        drawController.changeBgColor(drawController.bgColor)
        drawController.trackBitmaps(drawController.size, this, bitmapCallback)
        drawController.trackHistory(this, trackHistory)
    }
    BoxWithConstraints {
        Box(
            modifier = modifier
                .background(bgColor)
                .fillMaxSize()
                .wrapContentSize(unbounded = true)
        ) {

            Canvas(
                modifier = modifier
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val (x, y, flag) = correctLocation( drawController, offset)
                                if (flag) return@detectDragGestures
                                drawController.flag = true

                                drawController.insertNewPath(Offset(x, y))
                                ending?.invoke()
                            },
                        ) { change, _ ->
                            if (!drawController.flag) return@detectDragGestures
                            val (x, y, flag) = correctLocation( drawController, change.position)
                            if (flag) return@detectDragGestures

                            drawController.updateLatestPath(Offset(x, y))
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
                        translationX = offsetFit(drawController.offset.x),
                        translationY = offsetFit(drawController.offset.y),
                        scaleX = scaleFit(drawController.zoom),
                        scaleY = scaleFit(drawController.zoom),
                        rotationZ = angularFit(drawController.rotation)
                    )
                    .run {
                        if (drawController.bgImage != null) {
                            with(LocalDensity.current) {
                                this@run.size(
                                    drawController.bgImage!!.width.toDp(),
                                    drawController.bgImage!!.height.toDp()
                                )
                            }
                        } else {
                            size(
                                this@BoxWithConstraints.maxWidth,
                                this@BoxWithConstraints.maxHeight
                            )
                        }
                    }
                    .background(drawController.bgColor)
            ) {
                with(drawContext.canvas.nativeCanvas){
                    val layer = saveLayer(null, null)
                    drawController.size = if (drawController.bgImage != null) {
                        Size(
                            drawController.bgImage!!.width.toFloat(),
                            drawController.bgImage!!.height.toFloat()
                        )
                    } else {
                        this@Canvas.size
                    }
                    if (drawController.bgImage != null) {
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
                    restoreToCount(layer)
                }
            }
        }
    }

}