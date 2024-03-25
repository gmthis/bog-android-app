package cn.xd.bogr.ui.state

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.xd.bogr.ExampleApplication
import cn.xd.bogr.R
import cn.xd.bogr.ui.theme.colorArray
import cn.xd.bogr.ui.view.components.Grid
import cn.xd.bogr.util.noRippleClickable
import cn.xd.bogr.util.saveToAlbum
import io.ak1.drawbox.DrawController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DrawState(
    var fontSize: TextUnit = 14.sp,
    var iconSize: Dp = 24.dp,
    var iconTint: Color,
    val apply: (suspend (ImageBitmap) -> Unit)? = null,
    val isDark: Boolean,
    val context: Context
) {
    val drawController = DrawController()
    private var _controlBarStatus by mutableStateOf(true)
    val controlBarStatus
        get() = _controlBarStatus
    private var _controlBarMoreInfoStatus = ControlState()
    val controlBarMoreInfoStatus
        get() = _controlBarMoreInfoStatus.currentStatus


    var bgColorIndex by mutableStateOf(if (isDark) Pair(0, 1) else Pair(0, 9))
    var penColorIndex by mutableStateOf(if (isDark) Pair(0, 8) else Pair(0, 0))
    var bgColor: Color by mutableStateOf(colorArray[bgColorIndex.first][bgColorIndex.second]).also {
        drawController.changeBgColor(it.value)
    }
    var penColor: Color by mutableStateOf(colorArray[penColorIndex.first][penColorIndex.second]).also{
        drawController.changeColor(it.value)
    }
    var eraser by mutableStateOf(false)
    private var token = "cb"

    var penTransparency by mutableStateOf(1.0f)
    var penWeight by mutableStateOf(10f)

    var bitmapCallback: suspend (ImageBitmap) -> Unit = apply ?: ::saveToLocal

    fun closeControlBarMoreInfo(){
        if (_controlBarMoreInfoStatus.currentStatus){
            _controlBarMoreInfoStatus.close()
        }
    }

    fun openControlBar(token: String = "cb", config: (Config.() -> Unit)? = null){
        if (!_controlBarStatus){
            this.token = token
            if (config == null){
                _controlBarContent = defaultControlBarContent
            }else {
                Config().run {
                    config()
                    _controlBarContent = controlBarContent ?: defaultControlBarContent
                }
            }
            _controlBarStatus = true
        }
    }

    suspend fun closeControlBar(){
        _controlBarStatus = if (_controlBarMoreInfoStatus.currentStatus){
            _controlBarMoreInfoStatus.close()
            delay(500)
            false
        }else{
            false
        }
    }

    suspend fun controlBarStatusHandOff(token: String = "cb", config: (Config.() -> Unit)? = null){
        if (_controlBarStatus){
            if (this.token != token){
                closeControlBar()
                delay(500)
                openControlBar(token, config)
            }else{
                closeControlBar()
            }
        }else{
            openControlBar(token, config)
        }
    }

    var undoStatus by mutableStateOf(false)
    var redoStatus by mutableStateOf(false)

    private val defaultControlBarContent: @Composable ColumnScope.() -> Unit = {
        DrawControlContent()
    }

    private var _controlBarContent: @Composable ColumnScope.() -> Unit = defaultControlBarContent
    val controlBarContent
        get() = _controlBarContent

    suspend fun showText(token: String, composable: @Composable ColumnScope.() -> Unit){
        closeControlBar()
        delay(500)
        openControlBar(token){
            controlBarContent = composable
        }
        delay(2000)
        controlBarStatusHandOff()
    }


    suspend fun save(imageBitmap: ImageBitmap?, throwable: Throwable?){
        if (imageBitmap == null) return
        bitmapCallback(imageBitmap)
    }

    private suspend fun saveToLocal(imageBitmap: ImageBitmap){
        val fileName = "draw${System.currentTimeMillis()}${('a'..'z').random()}.jpg"
        val uri = imageBitmap.asAndroidBitmap().saveToAlbum(
            context = context,
            fileName = fileName,
            relativePath = "draw",
            quality = 100
        )
        showText("save"){
            Text(
                text = if (uri != null){
                    "${stringResource(id = R.string.save_succeed)}Pictures/draw/$fileName"
                }else{
                     stringResource(id = R.string.save_fail)
                },
                fontSize = fontSize,
                color = iconTint,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    class Config {
        var controlBarContent: (@Composable ColumnScope.() -> Unit)? = null
    }

    private class ControlState {

        private var _moreComposable: (@Composable ColumnScope.() -> Unit)? = null
        private var _moreIsOpen by mutableStateOf(false)
        private var token = "null"

        val currentStatus
            get() = _moreIsOpen

        val composable
            get() = _moreComposable

        fun open(token: String, config: (Config.() -> Unit)?){
            if (!_moreIsOpen){
                this.token = token
                Config().run{
                    config?.let {
                        it()
                        _moreComposable = composable
                    }
                }
                _moreIsOpen = true
            }
        }

        fun close(){
            if (_moreIsOpen){
                _moreIsOpen = false
                _moreComposable = null
                token = "null"
            }
        }

        suspend fun handoff(token: String, config: (Config.() -> Unit)?){
            if (this.token != token && _moreIsOpen){
                close()
                delay(550)
                open(token, config)
                return
            }else{
                if (_moreIsOpen){
                    close()
                }else{
                    open(token, config)
                }
            }
        }

        class Config{
            var composable: (@Composable ColumnScope.() -> Unit)? = null
        }
    }

    @Composable
    fun ColumnScope.DrawControlContent(){
        AnimatedVisibility(visible = _controlBarMoreInfoStatus.currentStatus) {
            val function = remember {
                _controlBarMoreInfoStatus.composable
            }
            Column {
                function?.invoke(this)
            }
        }
        DrawControlBar(_controlBarMoreInfoStatus)
    }

    @Composable
    private fun ColorSelector(
        title: Int,
        selected: Pair<Int, Int>,
        colors: List<List<Color>> = colorArray,
        tint: Color,
        onClick: (Int, Int) -> Unit
    ) {
        Column {
            Text(text = stringResource(id = title), fontSize = fontSize, color = iconTint)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = stringResource(id = R.string.variant))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                colors[selected.first].forEachIndexed{index, item ->
                    Icon(
                        painter = painterResource(id = R.drawable.ic_color),
                        contentDescription = "${stringResource(id = R.string.color)}: R${item.red}G${item.green}B${item.blue}",
                        tint = item,
                        modifier = Modifier.noRippleClickable {
                            onClick(selected.first, index)
                        }.run {
                            if (index == selected.second)
                                border(1.dp, tint, CircleShape)
                            else this
                        }
                    )
                }
            }
            Text(text = stringResource(id = R.string.main_color))
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState())
            ) {
                colors.forEachIndexed{index, item ->
                    Icon(
                        painter = painterResource(id = R.drawable.ic_color),
                        contentDescription = "${stringResource(id = R.string.color)}: R${item[0].red}G${item[0].green}B${item[0].blue}",
                        tint = item[0],
                        modifier = Modifier.noRippleClickable {
                            onClick(index, 0)
                        }.run {
                            if (index == selected.first)
                                border(1.dp, tint, CircleShape)
                            else this
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun ColumnScope.BackgroundColorSelector(){
        ColorSelector(
            title = R.string.backgroud_color,
            selected = bgColorIndex,
            tint = iconTint
        ){ first, second ->
            bgColorIndex = first to second
            println(bgColorIndex)
            bgColor = colorArray[bgColorIndex.first][bgColorIndex.second]
            drawController.changeBgColor(bgColor)
        }
        Spacer(modifier = Modifier.height(20.dp))
    }

    @Composable
    private fun ColumnScope.PenColorSelector(){
        ColorSelector(
            title = R.string.pen_color,
            selected = penColorIndex,
            tint = iconTint
        ){ first, second ->
            penColorIndex = first to second
            penColor = colorArray[penColorIndex.first][penColorIndex.second]
            drawController.changeColor(penColor)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(id = R.string.transparency),
            color = iconTint,
            fontSize = fontSize
        )
        Slider(
            value = penTransparency,
            onValueChange = {
                penTransparency = it
                drawController.changeOpacity(it)
            },
            colors = SliderDefaults.colors(
                thumbColor = penColor,
                activeTickColor = penColor,
                activeTrackColor = penColor
            ),
            steps = 20
        )
        Text(
            text = stringResource(id = R.string.pen_weight),
            color = iconTint,
            fontSize = fontSize
        )
        Slider(
            value = penWeight,
            onValueChange = {
                penWeight = it
                drawController.changeStrokeWidth(it)
            },
            colors = SliderDefaults.colors(
                thumbColor = penColor,
                activeTickColor = penColor,
                activeTrackColor = penColor
            ),
            steps = 20,
            valueRange = 5f..50f
        )
        Spacer(modifier = Modifier.height(20.dp))
    }

    @Composable
    fun ColumnScope.MoreSetting() {
        val context = LocalContext.current
        val result =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { imgUri ->
                if (imgUri !== null) {
                    val fileDescriptor = context.contentResolver.openFileDescriptor(imgUri, "r")
                    val descriptor = fileDescriptor?.fileDescriptor
                    val bitmap = BitmapFactory.decodeFileDescriptor(descriptor)
                    fileDescriptor?.close()
                    drawController.changeBgImage(bitmap.asImageBitmap())
                }
            }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            reverseLayout = true,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                MoreItem(icon = R.drawable.imagesmode_24px, text = R.string.insert_bg_img) {
                    result.launch("image/*")
                }
            }
            item {
                MoreItem(icon = R.drawable.clean_imagesmode, text = R.string.delete_bg_img) {
                    drawController.changeBgImage(null)
                }
            }
            if (apply != null){
                item {
                    MoreItem(icon = R.drawable.ic_download, text = R.string.save) {
                        bitmapCallback = ::saveToLocal
                        drawController.saveBitmap()
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }


    @Composable
    private fun MoreItem(
        icon: Int,
        text: Int,
        onClick: () -> Unit
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    onClick = onClick
                ),
        ) {
            Icon(
                painterResource(id = icon),
                contentDescription = stringResource(id = text),
                tint = iconTint
            )
            Text(
                text = stringResource(id = text),
                fontSize = fontSize,
                color = iconTint
            )
        }
    }

    @Composable
    private fun DrawControlBar(state: ControlState) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CanvasState()
            PaintState(state)
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun CanvasState() {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ){
            DrawControlContentItem(
                icon = if (apply == null) R.drawable.ic_download else R.drawable.done,
                descriptor = R.string.save,
                tint = iconTint,
            ){
                bitmapCallback = apply ?: ::saveToLocal
                drawController.saveBitmap()
            }
            DrawControlContentItem(
                icon = R.drawable.ic_undo,
                descriptor = R.string.undo,
                tint = iconTint
            ) {
                if (undoStatus) drawController.unDo()
            }
            DrawControlContentItem(
                icon = R.drawable.ic_redo,
                descriptor = R.string.redo,
                tint = iconTint
            ) {
                if (redoStatus) drawController.reDo()
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_refresh),
                contentDescription = stringResource(id = R.string.refresh),
                tint = iconTint,
                modifier = Modifier
                    .size(iconSize)
                    .combinedClickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onLongClick = {
                            drawController.reset()
                        },
                        onClick = {
                            drawController.resetStatus()
                        }
                    )
            )
        }
    }

    @Composable
    private fun PaintState(state: ControlState) {
        val scope = rememberCoroutineScope()
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ){
            DrawControlContentItem(
                icon = R.drawable.ic_color,
                descriptor = R.string.backgroud_color,
                tint = bgColor,
                modifier = Modifier.border(
                    1.dp, iconTint, CircleShape
                )
            ){
                scope.launch(
                    Dispatchers.IO
                ){
                    state.handoff("bg"){
                        composable = {
                            BackgroundColorSelector()
                        }
                    }
                }
            }
            DrawControlContentItem(
                icon = R.drawable.ic_color,
                descriptor = R.string.paint_color,
                tint = penColor,
                modifier = Modifier.border(
                    1.dp, iconTint, CircleShape
                )
            ) {
                scope.launch(
                    Dispatchers.IO
                ) {
                    state.handoff("pen"){
                        composable = {
                            PenColorSelector()
                        }
                    }
                }
            }
            DrawControlContentItem(
                icon = if (eraser){
                    R.drawable.auto_fix_normal
                } else {
                    R.drawable.auto_fix_off
                },
                descriptor = R.string.eraser,
                tint = iconTint
            ) {
                eraser = if (!eraser){
                    drawController.enableClear()
                    true
                }else{
                    drawController.disableClear()
                    false
                }
            }
            DrawControlContentItem(
                icon = R.drawable.ic_size,
                descriptor = R.string.more,
                tint = iconTint
            ) {
                scope.launch(
                    Dispatchers.IO
                ) {
                    state.handoff("more"){
                        composable = {
                            MoreSetting()
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun DrawControlContentItem(
        modifier: Modifier = Modifier,
        icon: Int,
        descriptor: Int,
        tint: Color,
        size: Dp = iconSize,
        onClick: () -> Unit
    ){
        Icon(
            painter = painterResource(id = icon),
            contentDescription = stringResource(id = descriptor),
            tint = tint,
            modifier = modifier
                .noRippleClickable(onClick = onClick)
                .size(size)
        )
    }
}

@Composable
fun rememberDrawState(
    fontSize: TextUnit = 14.sp,
    iconSize: Dp = 24.dp,
    iconTint: Color = MaterialTheme.colorScheme.onSecondary,
    isDark: Boolean = isSystemInDarkTheme(),
    context: Context = LocalContext.current,
    apply: (suspend (ImageBitmap) -> Unit)? = null,
) = remember {

    DrawState(
        fontSize = fontSize,
        iconSize = iconSize,
        iconTint = iconTint,
        apply = apply,
        isDark = isDark,
        context = context
    )
}