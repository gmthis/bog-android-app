package cn.xd.test

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cn.xd.test.ui.theme.BogTheme
import cn.xd.test.ui.theme.Unselected
import io.ak1.drawbox.DrawBox
import io.ak1.drawbox.DrawController
import io.ak1.drawbox.rememberDrawController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BogTheme {
                DrawPage()
            }
        }
    }
}

@Composable
fun DrawPage(modifier: Modifier = Modifier) {
    var undoVisibility by remember { mutableStateOf(false) }
    var redoVisibility by remember { mutableStateOf(false) }

    val bgColor by remember {
        mutableStateOf(Pair(mutableStateOf(0), mutableStateOf(9)))
    }
    val penColor by remember {
        mutableStateOf(Pair(mutableStateOf(0), mutableStateOf(0)))
    }
    var isOpen by remember {
        mutableStateOf(0)
    }
    val scope = rememberCoroutineScope()
    var penWidth by remember{ mutableStateOf(10f)}
    var penTransparency by remember {
        mutableStateOf(1f)
    }

    val drawController = rememberDrawController()
    drawController.changeColor(colorArray[penColor.first.value][penColor.second.value])
    drawController.changeBgColor(colorArray[bgColor.first.value][bgColor.second.value])
    drawController.changeStrokeWidth(penWidth)
    drawController.changeOpacity(penTransparency)
    val context = LocalContext.current

    Box(
        modifier = modifier
    ) {
        DrawBox(
            drawController = drawController,
            bitmapCallback = { imageBitmap, throwable ->
                imageBitmap?.asAndroidBitmap()?.save(context)
            }
        ) { undoCount, redoCount ->
            undoVisibility = undoCount != 0
            redoVisibility = redoCount != 0
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 40.dp, start = 20.dp, end = 20.dp)
                .wrapContentSize(Alignment.BottomCenter)
        ){
            Card(
                elevation = 4.dp,
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 10.dp)
                ){
                    AnimatedVisibility(visible = isOpen == 1 || isOpen == 2) {
                        Column {
                            ColorSelector(
                                selected = if (isOpen == 1)
                                    bgColor.first.value to bgColor.second.value
                                else
                                    penColor.first.value to penColor.second.value,
                                colors = colorArray,
                                onClick = { first, last ->
                                    if (isOpen == 1){
                                        bgColor.first.value = first
                                        bgColor.second.value = last
                                    }else{
                                        penColor.first.value = first
                                        penColor.second.value = last
                                        drawController.changeColor(colorArray[penColor.first.value][penColor.second.value])
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                    AnimatedVisibility(visible = isOpen == 3) {
                        Column {
                            Text(text = "不透明度")
                            Slider(
                                value = penTransparency,
                                onValueChange = {
                                    penTransparency = it
                                }
                            )
                            Text(text =" 粗细")
                            Slider(
                                value = penWidth,
                                onValueChange = {
                                    penWidth = it
                                },
                                valueRange = 5f..50f
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                    DrawControlsBar(
                        undoVisibility = undoVisibility,
                        redoVisibility = redoVisibility,
                        drawController = drawController,
                        bgColor = colorArray[bgColor.first.value][bgColor.second.value],
                        penColor = colorArray[penColor.first.value][penColor.second.value],
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
                        done = {}
                    )
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
    drawController: DrawController,
    bgColorSelected: () -> Unit,
    penColorSelected: () -> Unit,
    penSizeSelected: () -> Unit,
    done: () -> Unit
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
                tint = if (undoVisibility) MaterialTheme.colors.primary else MaterialTheme.colors.primaryVariant
            ) {
                done()
                drawController.saveBitmap()
            }
            Spacer(modifier = Modifier.width(width.dp))
            ControlsBarItem(
                resId = R.drawable.ic_undo,
                contentDescriptor = stringResource(id = R.string.undo),
                tint = if (redoVisibility) MaterialTheme.colors.primary else MaterialTheme.colors.primaryVariant
            ) {
                if (undoVisibility) drawController.unDo()
            }
            Spacer(modifier = Modifier.width(width.dp))
            ControlsBarItem(
                resId = R.drawable.ic_redo,
                contentDescriptor = stringResource(id = R.string.redo),
                tint = if (redoVisibility) MaterialTheme.colors.primary else MaterialTheme.colors.primaryVariant
            ) {
                if (redoVisibility){
                    drawController.reDo()
                }
            }
            Spacer(modifier = Modifier.width(width.dp))
            ControlsBarItem(
                resId = R.drawable.ic_refresh,
                contentDescriptor = stringResource(id = R.string.refresh),
                tint = if (undoVisibility || redoVisibility) MaterialTheme.colors.primary else MaterialTheme.colors.primaryVariant
            ) {
                drawController.reset()
            }
        }
        Row {
            ControlsBarItem(
                resId = R.drawable.ic_color,
                contentDescriptor = stringResource(id = R.string.bgColor),
                tint = bgColor,
                modifier = Modifier.border(
                    1.dp,
                    Unselected,
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
                    Unselected,
                    CircleShape
                )
            ) {
                penColorSelected()
            }
            Spacer(modifier = Modifier.width(width.dp))
            ControlsBarItem(
                resId = R.drawable.ic_size,
                contentDescriptor = stringResource(id = R.string.penSize),
                tint = if (undoVisibility) MaterialTheme.colors.primary else MaterialTheme.colors.primaryVariant
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
    selected: Pair<Int, Int>,
    colors: List<List<Color>>,
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
        Column {
            Text(text = "变体")
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
                                    Unselected,
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
        }
        Column {
            Text(text = "主要颜色")
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
                                    Unselected,
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
}

//TODO 图片保存功能 https://github.com/hushenghao/MediaStoreDemo/blob/main/app/src/main/java/com/dede/mediastoredemo/ImageExt.kt 该库有完善的保存方案,可供参考
fun Bitmap.save(content: Context): Boolean{

    val path = Environment.getExternalStorageDirectory().toString() + "/DCIM/draw/"
    val fileName = "cn.xd.draw" + System.currentTimeMillis() + ('A'..'Z').random()
    content.assets.open(fileName).use {

    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val values = ContentValues()
        var out: OutputStream? = null
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
        values.put(MediaStore.Images.Media.IS_PENDING, 1)
        content.contentResolver.also { resolver ->
            resolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
            )
            out = uri?.let {
                resolver.openOutputStream(it)
            }
        }
        out?.use {
            compress(
                Bitmap.CompressFormat.JPEG,
                100,
                it
            )
        }
        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        content.contentResolver.update(
            uri, values, null, null
        )
    }else{
        val file = File(path, fileName)
        val outStream = FileOutputStream(file)
        compress(
            Bitmap.CompressFormat.JPEG,
            100,
            outStream
        )
        outStream.close()
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val uri = Uri.fromFile(file)
        intent.data = uri
        content.sendBroadcast(intent)
    }
    return true
}