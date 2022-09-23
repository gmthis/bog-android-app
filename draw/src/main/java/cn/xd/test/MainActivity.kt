package cn.xd.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import cn.xd.test.ui.theme.BogTheme
import cn.xd.test.ui.theme.PinkText
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.ak1.drawbox.DrawBox
import io.ak1.drawbox.DrawController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val uiController = rememberSystemUiController()
            uiController.setSystemBarsColor(
                Color.Transparent, true
            )
            WindowInsetsControllerCompat(window, window.decorView).let {
                it.hide(WindowInsetsCompat.Type.systemBars())
                it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            BogTheme {
                DrawPage(tint = PinkText)
            }
        }
    }
}

@Composable
fun DrawPage(modifier: Modifier = Modifier, tint: Color) {
    var undoVisibility by rememberSaveable { mutableStateOf(false) }
    var redoVisibility by rememberSaveable { mutableStateOf(false) }
    val darkTheme = isSystemInDarkTheme()

    val bgColor by rememberSaveable {
        if (darkTheme){
            mutableStateOf(Pair(mutableStateOf(0), mutableStateOf(1)))
        }else{
            mutableStateOf(Pair(mutableStateOf(0), mutableStateOf(9)))
        }

    }
    val penColor by rememberSaveable {
        if (darkTheme){
            mutableStateOf(Pair(mutableStateOf(0), mutableStateOf(8)))
        }else{
            mutableStateOf(Pair(mutableStateOf(0), mutableStateOf(0)))
        }
    }
    var isOpen by rememberSaveable {
        mutableStateOf(0)
    }
    val scope = rememberCoroutineScope()
    var penWidth by rememberSaveable{ mutableStateOf(10f)}
    var penTransparency by rememberSaveable {
        mutableStateOf(1f)
    }
    val pen = colorArray[penColor.first.value][penColor.second.value]
    val bg = colorArray[bgColor.first.value][bgColor.second.value]

    var controllerFlag by rememberSaveable {
        mutableStateOf(true)
    }
    var infoFlag by rememberSaveable {
        mutableStateOf(false)
    }
    var infoText by rememberSaveable {
        mutableStateOf("")
    }
    val state by animateFloatAsState(
        targetValue = if (controllerFlag) 1f else 0f,
        animationSpec = tween(durationMillis = 450)
    )
    val drawController = rememberSaveable {
        DrawController()
    }
    drawController.changeColor(pen)
    drawController.changeBgColor(bg)
    drawController.changeStrokeWidth(penWidth)
    drawController.changeOpacity(penTransparency)
    val context = LocalContext.current

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        DrawBox(
            drawController = drawController,
            backgroundColor = bg,
            bitmapCallback = { imageBitmap, throwable ->
                controllerFlag = false
                val fileName = "draw${System.currentTimeMillis()}${('a'..'z').random()}.jpg"
                val uri = imageBitmap?.asAndroidBitmap()?.saveToAlbum(
                    context = context,
                    fileName = fileName,
                    relativePath = "draw",
                    quality = 100
                )
                println(uri)
                scope.launch(
                    Dispatchers.IO
                ) {
                    delay(500)
                    infoFlag = true
                    infoText = if (uri != null){
                        "成功 已保存到:\n Pictures/draw/$fileName"
                    }else{
                        "失败, 发生内部错误"
                    }
                    controllerFlag = true
                    scope.launch(
                        Dispatchers.IO
                    ) {
                        delay(2000)
                        controllerFlag = false
                        scope.launch(
                            Dispatchers.IO
                        ) {
                            delay(500)
                            infoFlag = false
                            controllerFlag = true
                        }
                    }
                }
            },
        ) { undoCount, redoCount ->
            undoVisibility = undoCount != 0
            redoVisibility = redoCount != 0
        }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(state)
                .padding(bottom = 40.dp, start = 20.dp, end = 20.dp)
                .wrapContentSize(Alignment.BottomCenter)
        ){
            Card(
                elevation = 4.dp,
                modifier = Modifier
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 10.dp)
                ){
                    AnimatedVisibility(visible = isOpen == 1 || isOpen == 2) {
                        val title = if (isOpen == 1) stringResource(id = R.string.bgColor) else stringResource(
                                id = R.string.penColor
                            )
                        Column {
                            ColorSelector(
                                title = if (isOpen == 0) "" else title,
                                selected = if (isOpen == 1)
                                    bgColor.first.value to bgColor.second.value
                                else
                                    penColor.first.value to penColor.second.value,
                                colors = colorArray,
                                tint = tint,
                                onClick = { first, last ->
                                    if (isOpen == 1){
                                        bgColor.first.value = first
                                        bgColor.second.value = last
                                    }else{
                                        penColor.first.value = first
                                        penColor.second.value = last
                                        drawController.changeColor(pen)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                    AnimatedVisibility(visible = isOpen == 3) {
                        Column(
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(text = "不透明度", color = tint)
                            Slider(
                                value = penTransparency,
                                onValueChange = {
                                    penTransparency = it
                                },
                                colors = SliderDefaults.colors(
                                    thumbColor = pen,
                                    activeTickColor = pen,
                                    activeTrackColor = pen,
                                ),
                                steps = 10,
                            )
                            Text(text =" 粗细", color = tint)
                            Slider(
                                value = penWidth,
                                onValueChange = {
                                    penWidth = it
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
                    if (infoFlag){
                        Text(
                            text = infoText,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }else{
                        DrawControlsBar(
                            undoVisibility = undoVisibility,
                            redoVisibility = redoVisibility,
                            drawController = drawController,
                            bgColor = bg,
                            penColor = pen,
                            tint = tint,
                            bgColorSelected = {
                                when(isOpen){
                                    1 -> {
                                        isOpen = 0
                                    }
                                    2,3 -> {
                                        isOpen = 0
                                        scope.launch(
                                            Dispatchers.IO
                                        ){
                                            delay(500)
                                            isOpen = 1
                                        }
                                    }
                                    0 -> {
                                        isOpen = 1
                                    }
                                }
                            },
                            penColorSelected = {
                                when(isOpen){
                                    1,3 -> {
                                        isOpen = 0
                                        scope.launch(
                                            Dispatchers.IO
                                        ){
                                            delay(500)
                                            isOpen = 2
                                        }
                                    }
                                    2 -> {
                                        isOpen = 0
                                    }
                                    0 -> {
                                        isOpen = 2
                                    }
                                }
                            },
                            penSizeSelected = {
                                when(isOpen){
                                    3 -> {
                                        isOpen = 0
                                    }
                                    1,2 -> {
                                        isOpen = 0
                                        scope.launch(
                                            Dispatchers.IO
                                        ){
                                            delay(500)
                                            isOpen = 3
                                        }
                                    }
                                    0 -> {
                                        isOpen = 3
                                    }
                                }
                            },
                            done = {},
                            ref = {
                                penWidth = 10f
                                penTransparency = 1f
                                if (darkTheme){
                                    bgColor.first.value = 0
                                    bgColor.second.value = 1
                                    penColor.first.value = 0
                                    penColor.second.value = 8
                                }else{
                                    bgColor.first.value = 0
                                    bgColor.second.value = 9
                                    penColor.first.value = 0
                                    penColor.second.value = 0
                                }

                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DrawControlsBar(
    undoVisibility: Boolean,
    redoVisibility: Boolean,
    bgColor: Color,
    penColor: Color,
    tint: Color,
    drawController: DrawController,
    bgColorSelected: () -> Unit,
    penColorSelected: () -> Unit,
    penSizeSelected: () -> Unit,
    done: () -> Unit,
    ref: () -> Unit
) {
    val width = 10
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row {
            ControlsBarItem(
                resId = R.drawable.done,
                contentDescriptor = stringResource(id = R.string.done),
                tint = tint
            ) {
                drawController.saveBitmap()
            }
            Spacer(modifier = Modifier.width(width.dp))
            ControlsBarItem(
                resId = R.drawable.ic_undo,
                contentDescriptor = stringResource(id = R.string.undo),
                tint = tint
            ) {
                if (undoVisibility) drawController.unDo()
            }
            Spacer(modifier = Modifier.width(width.dp))
            ControlsBarItem(
                resId = R.drawable.ic_redo,
                contentDescriptor = stringResource(id = R.string.redo),
                tint = tint
            ) {
                if (redoVisibility){
                    drawController.reDo()
                }
            }
            Spacer(modifier = Modifier.width(width.dp))
            ControlsBarItem(
                resId = R.drawable.ic_refresh,
                contentDescriptor = stringResource(id = R.string.refresh),
                tint = tint
            ) {
                drawController.reset()
                ref()
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
                )
            ) {
                bgColorSelected()
            }
            Spacer(modifier = Modifier.width(width.dp))
            ControlsBarItem(
                resId = R.drawable.ic_color,
                contentDescriptor = stringResource(id = R.string.penColor),
                tint = penColor,
                modifier = Modifier.border(
                    1.dp,
                    tint,
                    CircleShape
                )
            ) {
                penColorSelected()
            }
            Spacer(modifier = Modifier.width(width.dp))
            ControlsBarItem(
                resId = R.drawable.ic_size,
                contentDescriptor = stringResource(id = R.string.penSize),
                tint = tint
            ) {
                penSizeSelected()
            }
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
){
    val width = 10
    var first by remember{
        mutableStateOf(selected.first)
    }
    var last by remember {
        mutableStateOf(selected.second)
    }
    Column{
        Text(text = title, color = tint)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "变体", color = tint)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ){
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
                        if (index == last){
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
                if (index + 1 != colors[first].size){
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
                        if (index == first){
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
                if (index + 1 != colors.size){
                    Spacer(modifier = Modifier.width(width.dp))
                }
            }
        }
    }
}