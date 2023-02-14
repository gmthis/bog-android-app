package cn.xd.test

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.xd.test.ui.theme.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.ak1.drawbox.DrawBox
import io.ak1.drawbox.DrawController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("----------------------------------\nonCreate")
        setContent {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val uiController = rememberSystemUiController()
            uiController.setSystemBarsColor(
                Color.Transparent, true
            )
            BackHandler {
                moveTaskToBack(true)
            }
            BogTheme {
                DrawPage(drawPageInfo = viewModel<MyViewModel>().drawPageInfo, tint = PinkText)
            }
        }
    }
}

class MyViewModel : ViewModel() {
    val drawPageInfo = DrawPageInfo(viewModelScope)
}

class DrawPageInfo(val scope: CoroutineScope) {
    val drawController = DrawController()
    var undoVisibility by mutableStateOf(false)
    var redoVisibility by mutableStateOf(false)
    var isOpen by mutableStateOf(0)
    var bgColorFirst by mutableStateOf(0)
    var bgColorLast by mutableStateOf(9)
    var penColorFirst by mutableStateOf(0)
    var penColorLast by mutableStateOf(0)
    var controllerFlag by mutableStateOf(true)
    var infoFlag by mutableStateOf(false)
    var infoText by mutableStateOf("")
    var penWidth by mutableStateOf(10f)
    var penTransparency by mutableStateOf(1f)
    var isEraser by mutableStateOf(false)
    var isDark: Boolean? = null
    var bgImage: ImageBitmap? by mutableStateOf(null)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrawPage(
    drawPageInfo: DrawPageInfo,
    modifier: Modifier = Modifier,
    tint: Color,
    done: ((ImageBitmap) -> Unit)? = null
) {
    val context = LocalContext.current as ComponentActivity
    WindowInsetsControllerCompat(context.window, context.window.decorView).let {
        it.hide(WindowInsetsCompat.Type.systemBars())
        it.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
    val darkTheme = isSystemInDarkTheme()
    if (drawPageInfo.isDark == null) {
        drawPageInfo.isDark = darkTheme
    }
    if (darkTheme != drawPageInfo.isDark) {
        drawPageInfo.isDark = darkTheme
        if (darkTheme) {
            if (drawPageInfo.bgColorFirst == 0 && drawPageInfo.bgColorLast == 9) {
                drawPageInfo.bgColorLast = 1
            }
            if (drawPageInfo.penColorFirst == 0 && drawPageInfo.penColorLast == 0) {
                drawPageInfo.penColorLast = 8
            }
        } else {
            if (drawPageInfo.bgColorFirst == 0 && drawPageInfo.bgColorLast == 1) {
                drawPageInfo.bgColorLast = 9
            }
            if (drawPageInfo.penColorFirst == 0 && drawPageInfo.penColorLast == 8) {
                drawPageInfo.penColorLast = 0
            }
        }
    }
    val pen = colorArray[drawPageInfo.penColorFirst][drawPageInfo.penColorLast]
    val bg = colorArray[drawPageInfo.bgColorFirst][drawPageInfo.bgColorLast]

    val state by animateFloatAsState(
        targetValue = if (drawPageInfo.controllerFlag) 1f else 0f,
        animationSpec = tween(durationMillis = 450)
    )
    var isDone = false

    drawPageInfo.drawController.changeColor(pen)
    drawPageInfo.drawController.changeBgColor(bg)
    drawPageInfo.drawController.changeStrokeWidth(drawPageInfo.penWidth)
    drawPageInfo.drawController.changeOpacity(drawPageInfo.penTransparency)
    drawPageInfo.drawController.changeBgImage(drawPageInfo.bgImage)
    val result =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { imgUri ->
            if (imgUri !== null) {
                val fileDescriptor = context.contentResolver.openFileDescriptor(imgUri, "r")
                val descriptor = fileDescriptor?.fileDescriptor
                val bitmap = BitmapFactory.decodeFileDescriptor(descriptor)
                fileDescriptor?.close()
                drawPageInfo.bgImage = bitmap.asImageBitmap()
            }
        }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        DrawBox(
            modifier = Modifier
                .combinedClickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    onDoubleClick = {
                        if (drawPageInfo.isOpen != 0) {
                            drawPageInfo.isOpen = 0
                            drawPageInfo.scope.launch(
                                Dispatchers.IO
                            ) {
                                delay(500)
                                drawPageInfo.controllerFlag = false
                            }
                        } else {
                            drawPageInfo.controllerFlag = !drawPageInfo.controllerFlag
                        }
                    },
                    onClick = {
                        if (drawPageInfo.isOpen != 0) {
                            drawPageInfo.isOpen = 0
                        }
                    },
                ),
            drawController = drawPageInfo.drawController,
            bitmapCallback = { imageBitmap, throwable ->
                val fileName = "draw${System.currentTimeMillis()}${('a'..'z').random()}.jpg"
                if (isDone && imageBitmap != null) {
                    done?.invoke(imageBitmap)
                } else {
                    val uri: Uri? = imageBitmap?.asAndroidBitmap()?.saveToAlbum(
                        context = context,
                        fileName = fileName,
                        relativePath = "draw",
                        quality = 100
                    )
                    if (drawPageInfo.isOpen != 0) {

                        drawPageInfo.isOpen = 0
                        drawPageInfo.scope.launch(
                            Dispatchers.IO
                        ) {
                            delay(500)
                            drawPageInfo.controllerFlag = false
                            drawPageInfo.scope.launch(
                                Dispatchers.IO
                            ) {
                                delay(500)
                                drawPageInfo.infoFlag = true
                                drawPageInfo.infoText = if (uri != null) {
                                    "成功 已保存到:\n Pictures/draw/$fileName"
                                } else {
                                    "失败, 发生内部错误"
                                }
                                drawPageInfo.controllerFlag = true
                                drawPageInfo.scope.launch(
                                    Dispatchers.IO
                                ) {
                                    delay(2000)
                                    drawPageInfo.controllerFlag = false
                                    drawPageInfo.scope.launch(
                                        Dispatchers.IO
                                    ) {
                                        delay(500)
                                        drawPageInfo.infoFlag = false
                                        drawPageInfo.controllerFlag = true
                                    }
                                }
                            }
                        }
                    } else {
                        drawPageInfo.controllerFlag = false
                        drawPageInfo.scope.launch(
                            Dispatchers.IO
                        ) {
                            delay(500)
                            drawPageInfo.infoFlag = true
                            drawPageInfo.infoText = if (uri != null) {
                                "成功 已保存到:\n Pictures/draw/$fileName"
                            } else {
                                "失败, 发生内部错误"
                            }
                            drawPageInfo.controllerFlag = true
                            drawPageInfo.scope.launch(
                                Dispatchers.IO
                            ) {
                                delay(2000)
                                drawPageInfo.controllerFlag = false
                                drawPageInfo.scope.launch(
                                    Dispatchers.IO
                                ) {
                                    delay(500)
                                    drawPageInfo.infoFlag = false
                                    drawPageInfo.controllerFlag = true
                                }
                            }
                        }
                    }
                }
            },
            ending = {
                if (drawPageInfo.isOpen != 0) {
                    drawPageInfo.isOpen = 0
                }
            }
        ) { undoCount, redoCount ->
            drawPageInfo.undoVisibility = undoCount != 0
            drawPageInfo.redoVisibility = redoCount != 0
        }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(state)
                .padding(bottom = 40.dp, start = 20.dp, end = 20.dp)
                .wrapContentSize(Alignment.BottomCenter)
        ) {
            Card(
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 10.dp)
                ) {
                    AnimatedVisibility(visible = drawPageInfo.isOpen == 1) {
                        Column {
                            ColorSelector(
                                title = stringResource(id = R.string.bgColor),
                                selected = if (drawPageInfo.isOpen == 1)
                                    drawPageInfo.bgColorFirst to drawPageInfo.bgColorLast
                                else
                                    drawPageInfo.penColorFirst to drawPageInfo.penColorLast,
                                colors = colorArray,
                                tint = tint,
                                onClick = { first, last ->
                                    if (drawPageInfo.isOpen == 1) {
                                        drawPageInfo.bgColorFirst = first
                                        drawPageInfo.bgColorLast = last
                                    } else {
                                        drawPageInfo.penColorFirst = first
                                        drawPageInfo.penColorLast = last
                                        drawPageInfo.drawController.changeColor(pen)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                    AnimatedVisibility(visible = drawPageInfo.isOpen == 2) {
                        Column {
                            ColorSelector(
                                title = stringResource(id = R.string.penColor),
                                selected = if (drawPageInfo.isOpen == 1)
                                    drawPageInfo.bgColorFirst to drawPageInfo.bgColorLast
                                else
                                    drawPageInfo.penColorFirst to drawPageInfo.penColorLast,
                                colors = colorArray,
                                tint = tint,
                                onClick = { first, last ->
                                    if (drawPageInfo.isOpen == 1) {
                                        drawPageInfo.bgColorFirst = first
                                        drawPageInfo.bgColorLast = last
                                    } else {
                                        drawPageInfo.penColorFirst = first
                                        drawPageInfo.penColorLast = last
                                        drawPageInfo.drawController.changeColor(pen)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "不透明度", color = tint)
                            Slider(
                                value = drawPageInfo.penTransparency,
                                onValueChange = {
                                    drawPageInfo.penTransparency = it
                                },
                                colors = SliderDefaults.colors(
                                    thumbColor = pen,
                                    activeTickColor = pen,
                                    activeTrackColor = pen,
                                ),
                                steps = 10,
                            )
                            Text(text = " 粗细", color = tint)
                            Slider(
                                value = drawPageInfo.penWidth,
                                onValueChange = {
                                    drawPageInfo.penWidth = it
                                },
                                valueRange = 5f..50f,
                                colors = SliderDefaults.colors(
                                    thumbColor = pen,
                                    activeTickColor = pen,
                                    activeTrackColor = pen,
                                ),
                                steps = 20,
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                    AnimatedVisibility(visible = drawPageInfo.isOpen == 3) {
                        Column(
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .weight(0.25f)
                                        .clickable(
                                            interactionSource = MutableInteractionSource(),
                                            indication = null,
                                            onClick = {
                                                result.launch("image/*")
                                                drawPageInfo.isOpen = 0
                                            }
                                        ),
                                ) {
                                    Icon(
                                        painterResource(id = R.drawable.imagesmode_24px),
                                        contentDescription = "插入背景图片",
                                        tint = tint
                                    )
                                }
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .weight(0.25f)
                                        .clickable(
                                            interactionSource = MutableInteractionSource(),
                                            indication = null,
                                            onClick = {
                                                drawPageInfo.bgImage = null
                                                drawPageInfo.isOpen = 0
                                            }
                                        ),
                                ) {
                                    Icon(
                                        painterResource(id = R.drawable.clean_imagesmode),
                                        contentDescription = "清理图片",
                                        tint = tint
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                    if (drawPageInfo.infoFlag) {
                        Text(
                            text = drawPageInfo.infoText,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        DrawControlsBar(
                            drawPageInfo = drawPageInfo,
                            bgColor = bg,
                            penColor = pen,
                            tint = tint,
                            bgColorSelected = {
                                when (drawPageInfo.isOpen) {
                                    1 -> {
                                        drawPageInfo.isOpen = 0
                                    }
                                    2, 3 -> {
                                        drawPageInfo.isOpen = 0
                                        drawPageInfo.scope.launch(
                                            Dispatchers.IO
                                        ) {
                                            delay(500)
                                            drawPageInfo.isOpen = 1
                                        }
                                    }
                                    0 -> {
                                        drawPageInfo.isOpen = 1
                                    }
                                }
                            },
                            penColorSelected = {
                                when (drawPageInfo.isOpen) {
                                    1, 3 -> {
                                        drawPageInfo.isOpen = 0
                                        drawPageInfo.scope.launch(
                                            Dispatchers.IO
                                        ) {
                                            delay(500)
                                            drawPageInfo.isOpen = 2
                                        }
                                    }
                                    2 -> {
                                        drawPageInfo.isOpen = 0
                                    }
                                    0 -> {
                                        drawPageInfo.isOpen = 2
                                    }
                                }
                            },
                            penSizeSelected = {
                                when (drawPageInfo.isOpen) {
                                    3 -> {
                                        drawPageInfo.isOpen = 0
                                    }
                                    1, 2 -> {
                                        drawPageInfo.isOpen = 0
                                        drawPageInfo.scope.launch(
                                            Dispatchers.IO
                                        ) {
                                            delay(500)
                                            drawPageInfo.isOpen = 3
                                        }
                                    }
                                    0 -> {
                                        drawPageInfo.isOpen = 3
                                    }
                                }
                            },
                            done = if (done == null) {
                                null
                            } else {
                                {
                                    isDone = true
                                    drawPageInfo.drawController.saveBitmap()
                                }
                            },
                            ref = {
                                drawPageInfo.penWidth = 10f
                                drawPageInfo.penTransparency = 1f
                                if (darkTheme) {
                                    drawPageInfo.bgColorFirst = 0
                                    drawPageInfo.bgColorLast = 1
                                    drawPageInfo.penColorFirst = 0
                                    drawPageInfo.penColorLast = 8
                                } else {
                                    drawPageInfo.bgColorFirst = 0
                                    drawPageInfo.bgColorLast = 9
                                    drawPageInfo.penColorFirst = 0
                                    drawPageInfo.penColorLast = 0
                                }

                            },
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DrawControlsBar(
    drawPageInfo: DrawPageInfo,
    bgColor: Color,
    penColor: Color,
    tint: Color,
    bgColorSelected: () -> Unit,
    penColorSelected: () -> Unit,
    penSizeSelected: () -> Unit,
    done: (() -> Unit)? = null,
    ref: () -> Unit
) {
    val width = 10
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            ControlsBarItem(
                resId = R.drawable.ic_download,
                contentDescriptor = stringResource(id = R.string.download),
                tint = tint
            ) {
                drawPageInfo.drawController.saveBitmap()
            }
            Spacer(modifier = Modifier.width(width.dp))
            ControlsBarItem(
                resId = R.drawable.ic_undo,
                contentDescriptor = stringResource(id = R.string.undo),
                tint = tint
            ) {
                if (drawPageInfo.undoVisibility) drawPageInfo.drawController.unDo()
            }
            Spacer(modifier = Modifier.width(width.dp))
            ControlsBarItem(
                resId = R.drawable.ic_redo,
                contentDescriptor = stringResource(id = R.string.redo),
                tint = tint
            ) {
                if (drawPageInfo.redoVisibility) {
                    drawPageInfo.drawController.reDo()
                }
            }
            Spacer(modifier = Modifier.width(width.dp))
            Icon(
                painterResource(id = R.drawable.ic_refresh),
                contentDescription = stringResource(id = R.string.refresh),
                tint = tint,
                modifier = Modifier.combinedClickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    onClick = {
                        drawPageInfo.drawController.zoom = 1f
                        drawPageInfo.drawController.offset = Offset.Zero
                        drawPageInfo.drawController.rotation = 0f
                    },
                    onLongClick = {
                        drawPageInfo.drawController.reset()
                        ref()
                    }
                )
            )
        }
        if (done != null) {
            Row {
                ControlsBarItem(
                    modifier = Modifier.scale(1.4f),
                    resId = R.drawable.done,
                    contentDescriptor = stringResource(id = R.string.done),
                    tint = tint,
                    onClick = done
                )
            }
        }
        Row {
            ControlsBarItem(
                resId = R.drawable.ic_color,
                contentDescriptor = stringResource(id = R.string.bgColor),
                tint = bgColor,
                modifier = Modifier.border(
                    1.dp,
                    tint,
                    CircleShape
                ),
                onClick = bgColorSelected
            )
            Spacer(modifier = Modifier.width(width.dp))
            ControlsBarItem(
                resId = R.drawable.ic_color,
                contentDescriptor = stringResource(id = R.string.penColor),
                tint = penColor,
                modifier = Modifier.border(
                    1.dp,
                    tint,
                    CircleShape
                ),
                onClick = penColorSelected
            )
            Spacer(modifier = Modifier.width(width.dp))
            ControlsBarItem(
                resId = if (drawPageInfo.isEraser) {
                    R.drawable.auto_fix_normal
                } else {
                    R.drawable.auto_fix_off
                },
                contentDescriptor = stringResource(id = R.string.eraser),
                tint = tint
            ) {
                drawPageInfo.isEraser = if (!drawPageInfo.isEraser) {
                    drawPageInfo.drawController.enableClear()
                    true
                } else {
                    drawPageInfo.drawController.disableClear()
                    false
                }
            }
            Spacer(modifier = Modifier.width(width.dp))
            ControlsBarItem(
                resId = R.drawable.ic_size,
                contentDescriptor = stringResource(id = R.string.penSize),
                tint = tint,
                onClick = penSizeSelected
            )
        }
    }
}

@Composable
fun ControlsBarItem(
    modifier: Modifier = Modifier,
    resId: Int,
    contentDescriptor: String,
    tint: Color,
    onClick: () -> Unit
) {
    Icon(
        painterResource(id = resId),
        contentDescription = contentDescriptor,
        tint = tint,
        modifier = modifier.clickable(
            interactionSource = MutableInteractionSource(),
            indication = null,
            onClick = onClick
        )
    )
}

@Composable
fun ColorSelector(
    title: String,
    selected: Pair<Int, Int>,
    colors: List<List<Color>>,
    tint: Color,
    onClick: (Int, Int) -> Unit
) {
    val width = 10
    var first by remember {
        mutableStateOf(selected.first)
    }
    var last by remember {
        mutableStateOf(selected.second)
    }
    Column {
        Text(text = title, color = tint)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "变体", color = tint)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            for (index in colors[first].indices) {
                val col = colors[first][index]
                Icon(
                    painterResource(id = R.drawable.ic_color),
                    contentDescription =
                    "${stringResource(id = R.string.color)}: R${col.red}G${col.green}B${col.blue}",
                    tint = col,
                    modifier = Modifier.clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = {
                            last = index
                            onClick(first, last)
                        }
                    ).run {
                        if (index == last) {
                            border(
                                1.dp,
                                tint,
                                CircleShape
                            )
                        } else {
                            this
                        }
                    }
                )
                if (index + 1 != colors[first].size) {
                    Spacer(modifier = Modifier.width(width.dp))
                }
            }
        }
        Text(text = "主要颜色", color = tint)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            for (index in colors.indices) {
                val col = colors[index][0]
                Icon(
                    painterResource(id = R.drawable.ic_color),
                    contentDescription =
                    "${stringResource(id = R.string.color)}: R${col.red}G${col.green}B${col.blue}",
                    tint = col,
                    modifier = Modifier.clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = {
                            first = index
                            last = 0
                            onClick(first, last)
                        }
                    ).run {
                        if (index == first) {
                            border(
                                1.dp,
                                tint,
                                CircleShape
                            )
                        } else {
                            this
                        }
                    }
                )
                if (index + 1 != colors.size) {
                    Spacer(modifier = Modifier.width(width.dp))
                }
            }
        }
    }
}