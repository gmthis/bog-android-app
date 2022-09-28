package io.ak1.drawbox

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import kotlin.math.roundToInt

/**
 * Created by akshay on 24/12/21
 * https://ak1.io
 */


private fun View.getRect(x: Int, y: Int): android.graphics.Rect {
    val viewWidth = this.width
    val viewHeight = this.height
    return android.graphics.Rect(x, y, viewWidth + x, viewHeight + y)
}


//Model
data class PathWrapper(
    var points: SnapshotStateList<Offset>,
    val strokeWidth: Float = 5f,
    val strokeColor: Color,
    val alpha: Float = 1f,
    val blendMode: BlendMode = BlendMode.SrcOver
)

data class DrawBoxPayLoad(val bgColor: Color, val path: List<PathWrapper>)

fun createPath(points: List<Offset>) = Path().apply {
    if (points.size > 1) {
        var oldPoint: Offset? = null
        this.moveTo(points[0].x, points[0].y)
        for (i in 1 until points.size) {
            val point: Offset = points[i]
            oldPoint?.let {
                val midPoint = calculateMidpoint(it, point)
                if (i == 1) {
                    this.lineTo(midPoint.x, midPoint.y)
                } else {
                    this.quadraticBezierTo(it.x, it.y, midPoint.x, midPoint.y)
                }
            }
            oldPoint = point
        }
        oldPoint?.let { this.lineTo(it.x, oldPoint.y) }
    }
}

private fun calculateMidpoint(start: Offset, end: Offset) =
    Offset((start.x + end.x) / 2, (start.y + end.y) / 2)


internal suspend fun drawBitmapFromView(
    size: Size,
    config: Bitmap.Config,
    drawController: DrawController
): Bitmap{
    val bgBitmap = Bitmap.createBitmap(size.width.roundToInt(), size.height.roundToInt(), config)
    val bgImageBitmap = bgBitmap.asImageBitmap()
    val contentBitmap = Bitmap.createBitmap(size.width.roundToInt(), size.height.roundToInt(), config)
    val contentImageBitmap = contentBitmap.asImageBitmap()
    val bg = Canvas(bgImageBitmap)
    val content = Canvas(contentImageBitmap)
    val paint = Paint()
    paint.color = drawController.bgColor
    bg.drawRect(0f, 0f, size.width, size.height, paint = paint)
    if (drawController.scaleBgImage != null){
        bg.drawImage(drawController.scaleBgImage!!, drawController.bgOffset!!, paint)
    }
    for (pathWrapper in drawController.pathList) {
        paint.color = pathWrapper.strokeColor
        paint.alpha = pathWrapper.alpha
        paint.strokeWidth = pathWrapper.strokeWidth
        paint.strokeCap = StrokeCap.Round
        paint.strokeJoin = StrokeJoin.Round
        paint.style = PaintingStyle.Stroke
        paint.blendMode = pathWrapper.blendMode
        content.drawPath(
            createPath(pathWrapper.points),
            paint
        )
    }
    paint.blendMode = BlendMode.SrcOver
    bg.drawImage(contentImageBitmap, paint = paint, topLeftOffset = Offset(0f,0f))

    return bgBitmap
}
//    suspendCoroutine { continuation ->
//        doOnLayout { view ->
//            if (Build.VERSION_CODES.O > Build.VERSION.SDK_INT) {
//                continuation.resume(view.drawToBitmap(config))
//                return@doOnLayout
//            }
//
//            val window =
//                (context as? Activity)?.window ?: error("Can't get window from the Context")
//
//            Bitmap.createBitmap(width, height, config).apply {
//                val (x, y) = IntArray(2).apply { view.getLocationInWindow(this) }
//                PixelCopy.request(
//                    window,
//                    getRect(x, y),
//                    this,
//                    { copyResult ->
//                        if (copyResult == PixelCopy.SUCCESS) continuation.resume(this) else continuation.resumeWithException(
//                            RuntimeException("Bitmap generation failed")
//                        )
//                    },
//                    Handler(Looper.getMainLooper())
//                )
//            }
//        }
//    }


