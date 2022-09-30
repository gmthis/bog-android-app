package cn.xd.bog.ui.components

import android.graphics.BitmapFactory
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import cn.xd.bog.R
import cn.xd.bog.entity.Cookie
import cn.xd.bog.entity.Forum
import cn.xd.bog.ui.page.DrawPage
import cn.xd.bog.ui.theme.*
import cn.xd.bog.viewmodel.AppStatus
import cn.xd.bog.viewmodel.Data
import com.mxalbert.zoomable.Zoomable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun BottomCard(
    modifier: Modifier = Modifier,
    isFull: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .imePadding()
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    topStart = 10.dp,
                    topEnd = 10.dp
                )
            )
            .background(if (isSystemInDarkTheme()) Black else White)
            .padding(top = 10.dp, bottom = 20.dp, start = 5.dp, end = 5.dp)
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = {

                }
            ).run {
                if (isFull) fillMaxHeight() else this
            }
    ) {
        content()
        Surface(modifier = Modifier.navigationBarsPadding().fillMaxWidth()){}
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun SendString(
    forums: Forum?,
    forumsSelectedItem: Int,
    fontSize: Int,
    appStatus: AppStatus,
    data: Data,
    close: () -> Unit
) {

    var dialogFlag by rememberSaveable {
        mutableStateOf(0)
    }

    val iconSize: Int = 24
    val iconColor: Color = PinkText
    var moreIsOpen by rememberSaveable {
        mutableStateOf(false)
    }
    val animateState by animateFloatAsState(if (moreIsOpen) 180f else 0f)
    var isFull by rememberSaveable {
        mutableStateOf(false)
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val value = interactionSource.collectIsPressedAsState().value
    var isEmoji by rememberSaveable {
        mutableStateOf(false)
    }
    if (value){
        isEmoji = false
    }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var focus by rememberSaveable {
        mutableStateOf(2)
    }
    val context = LocalContext.current as ComponentActivity
    val result =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { imgUri ->
            if (imgUri !== null) {
                val fileDescriptor = context.contentResolver.openFileDescriptor(imgUri, "r")
                val descriptor = fileDescriptor?.fileDescriptor
                val bitmap = BitmapFactory.decodeFileDescriptor(descriptor)
                fileDescriptor?.close()
                data.images.add(bitmap.asImageBitmap())
            }
        }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier.clickable(
            interactionSource = MutableInteractionSource(),
            indication = null,
            onClick = {}
        )
    ) {
        BottomCard(
            isFull = isFull
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.arrow_drop_down),
                        contentDescription = stringResource(id = R.string.more),
                        modifier = Modifier
                            .size(
                                iconSize.dp
                            )
                            .clickable(
                                indication = null,
                                interactionSource = MutableInteractionSource(),
                                onClick = {
                                    moreIsOpen = !moreIsOpen
                                }
                            )
                            .rotate(animateState),
                        tint = iconColor,
                    )
                    Text(text = stringResource(id = R.string.newString))
                }
                Row {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.help),
                        contentDescription = stringResource(id = R.string.help),
                        modifier = Modifier
                            .size(
                                iconSize.dp
                            )
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = {
                                    appStatus.viewModelScope.launch(
                                        Dispatchers.IO
                                    ) {
                                        appStatus.snackbarHostState.showSnackbar(
                                            data.forum?.info?.get(appStatus.selected)?.info
                                                ?: "error"
                                        )
                                    }
                                }
                            ),
                        tint = iconColor
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.fullscreen),
                        contentDescription = stringResource(id = R.string.full),
                        modifier = Modifier
                            .size(
                                iconSize.dp
                            )
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = {
                                    isFull = !isFull
                                }
                            ),
                        tint = iconColor
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.close),
                        contentDescription = stringResource(id = R.string.close),
                        modifier = Modifier
                            .size(
                                iconSize.dp
                            )
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = close
                            ),
                        tint = iconColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            MaterialTheme(
                colors = if (!isSystemInDarkTheme()) {
                    Field
                } else {
                    FieldDark
                },
                typography = Typography,
                shapes = Shapes
            ) {
                AnimatedVisibility(visible = moreIsOpen) {
                    Column {
                        BasicTextField(
                            interactionSource = interactionSource,
                            singleLine = true,
                            value = data.nick,
                            onValueChange = {
                                data.nick = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(34.dp)
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .onFocusChanged {
                                    if (it.isFocused) {
                                        focus = 0
                                    }
                                },
                            textStyle = TextStyle(
                                fontSize = (fontSize).sp,
                                color = MaterialTheme.colors.onSurface
                            ),
                            cursorBrush = SolidColor(PinkText),
                            decorationBox = {
                                Box(
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(MaterialTheme.colors.surface)
                                        .padding(5.dp)
                                ) {
                                    it()
                                    if (data.nick.text.isEmpty()) {
                                        Text(
                                            text = stringResource(id = R.string.nick),
                                            color = Unselected
                                        )
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Down)
                                }
                            )
                        )
                        Spacer(modifier = Modifier.height(0.5.dp))
                        BasicTextField(
                            interactionSource = interactionSource,
                            singleLine = true,
                            value = data.title,
                            onValueChange = {
                                data.title = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(34.dp)
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .onFocusChanged {
                                    if (it.isFocused) {
                                        focus = 1
                                    }
                                },
                            textStyle = TextStyle(
                                fontSize = (fontSize).sp,
                                color = MaterialTheme.colors.onSurface
                            ),
                            cursorBrush = SolidColor(PinkText),
                            decorationBox = {
                                Box(
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(MaterialTheme.colors.surface)
                                        .padding(5.dp)
                                ) {
                                    it()
                                    if (data.title.text.isEmpty()) {
                                        Text(
                                            text = stringResource(id = R.string.title),
                                            color = Unselected
                                        )
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = {
                                    focusManager.moveFocus(FocusDirection.Down)
                                }
                            )
                        )
                        Spacer(modifier = Modifier.height(0.5.dp))
                    }
                }
                BasicTextField(
                    interactionSource = interactionSource,
                    value = data.content,
                    onValueChange = {
                        data.content = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp, max = 260.dp)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .run {
                            if (isFull) weight(1f) else this
                        }
                        .onFocusChanged {
                            if (it.isFocused) {
                                focus = 2
                            }
                        },
                    textStyle = TextStyle(
                        fontSize = (fontSize).sp,
                        color = MaterialTheme.colors.onSurface
                    ),
                    cursorBrush = SolidColor(PinkText),
                    decorationBox = {
                        Box(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colors.surface)
                                .padding(5.dp)
                        ) {
                            it()
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .weight(0.5f)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = {
                                dialogFlag = 1
                            }
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        Text(
                            text = "板块:",
                            color = PinkText
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            text = appStatus.form,
                            color = PinkText
                        )
                    }
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.arrow_drop_down),
                        contentDescription = stringResource(id = R.string.selected),
                        modifier = Modifier.size(
                            iconSize.dp
                        ),
                        tint = iconColor
                    )
                }
                Spacer(modifier = Modifier.width(50.dp))
                Row(
                    modifier = Modifier
                        .weight(0.5f)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = {
                                dialogFlag = 2
                            }
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        Text(
                            text = "饼干:",
                            color = PinkText
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            text = data.cookies.takeIf { it.isNotEmpty() }?.get(appStatus.cookieSelected)?.cookie
                                ?: "无饼干",
                            color = PinkText
                        )
                    }
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.arrow_drop_down),
                        contentDescription = stringResource(id = R.string.selected),
                        modifier = Modifier.size(
                            iconSize.dp
                        ),
                        tint = iconColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyVerticalGrid(
                modifier = Modifier,
                columns = GridCells.Adaptive(50.dp),
                contentPadding = PaddingValues(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(data.images) { index, image ->
                    Image(
                        image,
                        contentDescription = "图片",
                        modifier = Modifier
                            .border(1.dp, PinkText)
                            .combinedClickable(
                                indication = null,
                                interactionSource = MutableInteractionSource(),
                                onClick = {

                                },
                                onLongClick = {
                                    data.images.removeAt(index)
                                }
                            )
                            .size(50.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.expand_more),
                    contentDescription = stringResource(id = R.string.put_away),
                    modifier = Modifier
                        .size(
                            30.dp
                        )
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = {
                                keyboardController?.hide()
                            }
                        ),
                    tint = iconColor
                )
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.draw),
                    contentDescription = stringResource(id = R.string.draw),
                    modifier = Modifier
                        .size(
                            iconSize.dp
                        )
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = {
                                dialogFlag = 3
                                keyboardController?.hide()
                            }
                        ),
                    tint = iconColor
                )
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.sentiment_satisfied),
                    contentDescription = stringResource(id = R.string.sentiment_satisfied),
                    modifier = Modifier
                        .size(
                            iconSize.dp
                        )
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = {
                                isEmoji = !isEmoji
                                keyboardController?.hide()
                            }
                        ),
                    tint = iconColor
                )
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.imagesmode),
                    contentDescription = stringResource(id = R.string.imagesmode),
                    modifier = Modifier
                        .size(
                            iconSize.dp
                        )
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = {
                                result.launch("image/*")
                            }
                        ),
                    tint = iconColor
                )
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.dice),
                    contentDescription = stringResource(id = R.string.dice_icon),
                    modifier = Modifier
                        .size(
                            iconSize.dp
                        )
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = {
                                if (data.content.selection.length == 0) {
                                    val first =
                                        data.content.text.substring(
                                            0,
                                            data.content.selection.start
                                        )
                                    val last = data.content.text.substring(
                                        data.content.selection.start,
                                        data.content.text.length
                                    )
                                    data.content = data.content.copy(
                                        "$first[0-9]$last",
                                        TextRange(data.content.selection.start + 5)
                                    )
                                } else {
                                    val first =
                                        data.content.text.substring(
                                            0,
                                            data.content.selection.start
                                        )
                                    val last = data.content.text.substring(
                                        data.content.selection.end,
                                        data.content.text.length
                                    )
                                    data.content = data.content.copy(
                                        "$first[0-9]$last",
                                        TextRange(
                                            data.content.selection.start,
                                            data.content.selection.start + 5
                                        )
                                    )
                                }
                            }
                        ),
                    tint = iconColor
                )
                Box(
                    modifier = Modifier
                        .clip(send)
                        .background(PinkText)
                        .width(50.dp)
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ){
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.send),
                        contentDescription = stringResource(id = R.string.send),
                        modifier = Modifier
                            .size(
                                30.dp
                            )
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = {

                                }
                            ),
                        tint = White
                    )
                }
            }
            if (isEmoji) {
                Emoji(textFieldValue = when (focus) {
                    0 -> data.nick
                    1 -> data.title
                    else -> data.content
                }, onClick = {
                    when (focus) {
                        0 -> data.nick = it
                        1 -> data.title = it
                        else -> data.content = it
                    }
                })
            }
        }
        when (dialogFlag) {
            1 -> {
                Dialog(onDismissRequest = { dialogFlag = 0 }) {
                    ForumSelected(forums = forums!!, selected = {
                        appStatus.selected = it
                        dialogFlag = 0
                    })
                }
            }
            2 -> {
                Dialog(onDismissRequest = { dialogFlag = 0 }) {
                    CookieSelected(cookies = data.cookies, selected = {
                        appStatus.cookieSelected = it
                    })
                }
            }
        }
        AnimatedVisibility(visible = dialogFlag == 3) {
            DrawPage(drawPageInfo = appStatus.drawPageInfo, tint = PinkText, done = {
                data.images.add(it)
                dialogFlag = 0
            })
            BackHandler {
                dialogFlag = 0
            }
            if (dialogFlag == 0) {
                WindowInsetsControllerCompat(context.window, context.window.decorView).let {
                    it.show(WindowInsetsCompat.Type.systemBars())
                    it.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
        }
    }
}

val title = listOf(
    "[つд⊂]", "(`Д´)", "\uD83D\uDC34", "特殊"
)

val emojiMap = listOf(
    listOf(
        "|∀ﾟ",
        "[´ﾟДﾟ`]",
        "[;´Д`]",
        "[｀･ω･]",
        "[=ﾟωﾟ]=",
        "| ω・´]",
        "|-` ]",
        "|д` ]",
        "|ー` ]",
        "|∀` ]",
        "[つд⊂]",
        "[ﾟДﾟ≡ﾟДﾟ]",
        "[＾o＾]ﾉ",
        "[|||ﾟДﾟ]",
        "[ ﾟ∀ﾟ]",
        "[ ´∀`]",
        "[*´∀`]",
        "[*ﾟ∇ﾟ]",
        "[*ﾟーﾟ]",
        "[　ﾟ 3ﾟ]",
        "[ ´ー`]",
        "[ ・_ゝ・]",
        "[ ´_ゝ`]",
        "[*´д`]",
        "[・ー・]",
        "[・∀・]",
        "[ゝ∀･]",
        "[〃∀〃]",
        "[*ﾟ∀ﾟ*]",
        "[ ﾟ∀。]",
        "[ `д´]",
        "[`ε´ ]",
        "[`ヮ´ ]",
        "σ`∀´]",
        " ﾟ∀ﾟ]σ",
        " ﾟ ∀ﾟ]ノ ",
        "[╬ﾟдﾟ]",
        "[|||ﾟдﾟ]",
        "[ ﾟдﾟ]",
        "Σ[ ﾟдﾟ]",
        "[ ;ﾟдﾟ]",
        "[ ;´д`]",
        "[　д ] ﾟ ﾟ",
        "[ ☉д⊙]",
        "[[[　ﾟдﾟ]]]",
        "[ ` ・´]",
        "[ ´д`]",
        "[ -д-]",
        "･ﾟ[ ﾉд`ﾟ]",
        "[>д<]",
        "[ TдT]",
        "[￣∇￣]",
        "[￣3￣]",
        "[￣ｰ￣]",
        "[￣ . ￣]",
        "[￣皿￣]",
        "[￣艸￣]",
        "[￣︿￣]",
        "[￣︶￣]",
        "ヾ[´ωﾟ｀]",
        "[*´ω`*]",
        "[・ω・]",
        "[ ´・ω]",
        "[｀・ω]",
        "[`・ω・´]",
        "[ `_っ´]",
        "[ `ー´]",
        "[ ´_っ`]",
        "[ ´ρ`]",
        "[ ﾟωﾟ]",
        "[oﾟωﾟo]",
        "[　^ω^]",
        "[｡◕∀◕｡]",
        "/[ ◕‿‿◕ ]\\",
        "ヾ[´ε`ヾ]",
        "[ノﾟ∀ﾟ]ノ",
        "[σﾟдﾟ]σ",
        "[σﾟ∀ﾟ]σ",
        "|дﾟ ]",
        "┃電柱┃",
        "ﾟ[つд`ﾟ]",
        "ﾟÅﾟ ]",
        "⊂彡☆]]д`]",
        "⊂彡☆]]д´]",
        "⊂彡☆]]∀`]",
        "[´∀[[☆ミつ",
        "･ﾟ[ ﾉヮ´ ]",
        "[ﾉ]`ω´[ヾ]",
        "ᕕ[ ᐛ ]ᕗ",
        "[　ˇωˇ]",
        "[ ｣ﾟДﾟ]｣＜",
        "[ ›´ω`‹ ]",
        "[;´ヮ`]7",
        "[`ゥ´ ]",
        "[`ᝫ´ ]",
        "[ ᑭ`д´]ᓀ]]д´]ᑫ",
        "σ[ ᑒ ]",
        "[`ヮ´ ]σ`∀´] ﾟ∀ﾟ]σ"
    ),
    listOf(
        "(⌒▽⌒)",
        "（￣▽￣）",
        "(=・ω・=)",
        "(｀・ω・´)",
        "(〜￣△￣)〜",
        "(･∀･)",
        "((°∀°)ﾉ",
        "(￣3￣)",
        "╮(￣▽￣)╭",
        "( ´_ゝ｀)",
        "←_←",
        "→_→",
        "(<_<)",
        "(>_>)",
        "((;¬_¬)",
        "((\"▔□▔)/",
        "(ﾟДﾟ≡ﾟдﾟ)!?",
        "Σ(ﾟдﾟ;)",
        "Σ( ￣□￣||)",
        "(´；ω；`)",
        "（/TДT)/",
        "((^・ω・^ )",
        "(｡･ω･｡)",
        "(●￣(ｴ)￣●)",
        "ε=ε=(ノ≧∇≦)ノ",
        "(´･_･`)",
        "((-_-#)",
        "（￣へ￣）",
        "(￣ε(#￣) Σ",
        "ヽ(`Д´)ﾉ",
        "(╯°口°)╯(┴—┴",
        "（#-_-)┯━┯",
        "__( =>3」∠)_",
        "(笑)",
        "(汗)",
        "(泣)",
        "(苦笑)"
    ),
    listOf(
        "\ud83d\udc34",
        "\ud83e\udd21",
        "\ud83d\udc4f",
        "\ud83d\ude2e",
        "\ud83d\udc4d",
        "\ud83d\ude00",
        "\ud83d\ude18",
        "\ud83d\ude0d",
        "\ud83d\ude06",
        "\ud83d\ude1c",
        "\ud83d\ude05",
        "\ud83d\ude02",
        "\ud83d\ude0b",
        "\ud83d\ude1e",
        "\ud83e\udd14",
        "\ud83d\ude29",
        "\ud83d\ude2d",
        "\ud83d\ude21",
        "\ud83d\udc80",
        "\u267f\ufe0f",
        "\ud83d\udc74",
        "\u2640\ufe0f",
        "\u2642\ufe0f",
        "\u2764\ufe0f",
        "\u2705",
        "\u274c",
        "\u2b55\ufe0f",
        "\ud83d\ude4f"
    ),
    listOf(">>Po.")
)

@Composable
fun Emoji(textFieldValue: TextFieldValue, onClick: (TextFieldValue) -> Unit) {

    var state by remember {
        mutableStateOf(0)
    }

    MaterialTheme(
        colors = if (!isSystemInDarkTheme()) {
            tableColor
        } else {
            tableColorDark
        },
        typography = Typography,
        shapes = Shapes,
    ) {
        TabRow(selectedTabIndex = state) {
            for (index in title.indices) {
                Text(
                    text = title[index],
                    modifier = Modifier
                        .selectable(
                            selected = state == index,
                            indication = null,
                            interactionSource = MutableInteractionSource(),
                            onClick = {
                                state = index
                            },
                            role = Role.Tab
                        )
                        .padding(vertical = 10.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
        }
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.height(120.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        itemsIndexed(emojiMap[state]) { index, item ->
            Text(
                text = item,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = {
                            if (textFieldValue.selection.length == 0) {
                                val first =
                                    textFieldValue.text.substring(
                                        0,
                                        textFieldValue.selection.start
                                    )
                                val last = textFieldValue.text.substring(
                                    textFieldValue.selection.start,
                                    textFieldValue.text.length
                                )
                                onClick(
                                    textFieldValue.copy(
                                        first + item + last,
                                        TextRange(textFieldValue.selection.start + item.length)
                                    )
                                )
                            } else {
                                val first =
                                    textFieldValue.text.substring(
                                        0,
                                        textFieldValue.selection.start
                                    )
                                val last = textFieldValue.text.substring(
                                    textFieldValue.selection.end,
                                    textFieldValue.text.length
                                )

                                onClick(
                                    textFieldValue.copy(
                                        first + item + last,
                                        TextRange(
                                            textFieldValue.selection.start,
                                            textFieldValue.selection.start + item.length
                                        )
                                    )
                                )
                            }
                        }
                    ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = PinkText
            )
        }
    }
}

@Composable
fun CookieSelected(
    cookies: List<Cookie>,
    selected: (Int) -> Unit
){
    Column(
        modifier = Modifier
            .clip(
                MaterialTheme.shapes.medium
            )
            .background(
                MaterialTheme.colors.primary,
            )
            .fillMaxWidth(
                0.9f
            )
            .fillMaxHeight(
                0.9f
            )
            .padding(
                vertical = 20.dp,
                horizontal = 15.dp
            )
            .verticalScroll(rememberScrollState()),
    ) {
        for (index in cookies.indices) {
            if (index != 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = {
                                selected(index)
                            }
                        ),
                    elevation = 2.dp
                ) {
                    Text(
                        text = cookies[index].cookie,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}

@Composable
fun ForumSelected(
    forums: Forum,
    selected: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .clip(
                MaterialTheme.shapes.medium
            )
            .background(
                MaterialTheme.colors.primary,
            )
            .fillMaxWidth(
                0.9f
            )
            .fillMaxHeight(
                0.9f
            )
            .padding(
                vertical = 20.dp,
                horizontal = 15.dp
            )
            .verticalScroll(rememberScrollState()),
    ) {
        for (forum in forums.info.indices) {
            if (forum != 0) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = {
                                selected(forum)
                            }
                        ),
                    elevation = 2.dp
                ) {
                    Text(
                        text = forums.info[forum].name,
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize()
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}