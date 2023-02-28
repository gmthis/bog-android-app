package cn.xd.bogr.ui.view.components

import android.graphics.BitmapFactory
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cn.xd.bogr.R
import cn.xd.bogr.net.entity.emojiMap
import cn.xd.bogr.ui.page.components.Container
import cn.xd.bogr.ui.state.ContainerState
import cn.xd.bogr.ui.state.rememberContainerState
import cn.xd.bogr.util.imeStatus
import cn.xd.bogr.util.noRippleClickable
import cn.xd.bogr.util.rememberViewModel
import cn.xd.bogr.viewmodel.AppStatus
import cn.xd.bogr.util.get

@Composable
fun SendCardIcon(
    modifier: Modifier = Modifier,
    icon: Int,
    description: Int,
    viewModel: AppStatus,
    tint: Color = MaterialTheme.colorScheme.onSecondary,
    onClick: () -> Unit
){
    Icon(
        painter = painterResource(id = icon),
        contentDescription = stringResource(id = description),
        modifier = modifier
            .size(viewModel.iconSize.dp)
            .noRippleClickable(onClick = onClick),
        tint = tint
    )
}

@Composable
fun SendCardStateBar(
    viewModel: AppStatus,
    rotate: Float,
    fullRevise: () -> Unit,
    moreOpenStateRevise: () -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SendCardIcon(
                icon = R.drawable.arrow_drop_down,
                description = R.string.more,
                viewModel = viewModel,
                modifier = Modifier
                    .rotate(rotate)
            ) {
                moreOpenStateRevise()
            }
            Text(text = stringResource(id = R.string.send_new))
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SendCardIcon(
                icon = R.drawable.help,
                description = R.string.help,
                viewModel = viewModel
            ) {

            }
            SendCardIcon(
                icon = R.drawable.fullscreen,
                description = R.string.full_screen,
                viewModel = viewModel
            ) {
                fullRevise()
            }
            SendCardIcon(
                icon = R.drawable.close,
                description = R.string.close,
                viewModel = viewModel
            ) {

            }
        }
    }
}

@Composable
fun ColumnScope.MoreInputField(
    modifier: Modifier = Modifier,
    isOpen: Boolean,
    viewModel: AppStatus,
    emojiShowRevise: () -> Unit,
    focus: (FocusState, Int) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val source1 = remember {
        MutableInteractionSource()
    }
    val source2 = remember {
        MutableInteractionSource()
    }
    if (source1.collectIsPressedAsState().value || source2.collectIsPressedAsState().value){
        emojiShowRevise()
    }
    AnimatedVisibility(visible = isOpen) {
        Column {
            val modifier1 = modifier
                .fillMaxWidth()
                .height((viewModel.fontSize + 20).dp)
                .padding(horizontal = 8.dp, vertical = 2.dp)
            UnstyledTextField(
                value = viewModel.nick,
                prompt = stringResource(id = R.string.nick),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                viewModel = viewModel,
                singleLine = true,
                modifier = modifier1.onFocusChanged {
                    focus(it, 0)
                },
                interactionSource = source1
            ){
                viewModel.nick = it
            }
            Spacer(modifier = Modifier.height(1.dp))
            UnstyledTextField(
                value = viewModel.title,
                prompt = stringResource(id = R.string.title),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }),
                viewModel = viewModel,
                singleLine = true,
                modifier = modifier1.onFocusChanged {
                    focus(it, 1)
                },
                interactionSource = source2
            ){
                viewModel.title = it
            }
            Spacer(modifier = Modifier.height(1.dp))
        }
    }
}

@Composable
fun ColumnScope.InputField(
    modifier: Modifier = Modifier,
    viewModel: AppStatus,
    focusRequester: FocusRequester,
    emojiShowRevise: () -> Unit,
    focus: (FocusState, Int) -> Unit,
){
    val source = remember {
        MutableInteractionSource()
    }
    if (source.collectIsPressedAsState().value){
        emojiShowRevise()
    }
    UnstyledTextField(
        value = viewModel.sendContent,
        prompt = stringResource(id = R.string.send_content_prompt),
        viewModel = viewModel,
        singleLine = false,
        modifier = modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .focusRequester(focusRequester)
            .onFocusChanged {
                focus(it, 2)
            },
        interactionSource = source
    ){
        viewModel.sendContent = it
    }
}

@Composable
fun RowScope.SendCardSelect(
    name: Int,
    value: String,
    icon: Int,
    description: Int,
    viewModel: AppStatus,
    onClick: () -> Unit
){
    Row(
        modifier = Modifier
            .weight(0.5f)
            .noRippleClickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ){
            Text(
                text = stringResource(id = name) + ":",
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = viewModel.fontSize.sp
            )
            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = viewModel.fontSize.sp
            )
        }
        Icon(
            painter = painterResource(id = icon),
            contentDescription = stringResource(id = description),
            modifier = Modifier.size(
                viewModel.iconSize.dp
            ),
            tint = MaterialTheme.colorScheme.onSecondary
        )
    }
}

@Composable
fun <V> SelectDialog(
    list: List<V>,
    viewModel: AppStatus,
    selected: Int,
    getShowString: V.() -> String,
    select: (V) -> Unit,
    close: () -> Unit
){
    Dialog(onDismissRequest = close) {
        Column(
            modifier = Modifier
                .fillMaxSize(0.95f)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Spacer(modifier = Modifier.height(5.dp))
            list.forEachIndexed { index, item ->
                Box(
                    modifier = Modifier.padding(horizontal = 15.dp)
                ) {
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = item.getShowString(),
                                fontSize = viewModel.fontSize.sp
                            )
                        },
                        onClick = {
                            select(item)
                        },
                        selected = selected == index,
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondary,
                            unselectedContainerColor = MaterialTheme.colorScheme.background,
                            selectedTextColor = MaterialTheme.colorScheme.onSecondary
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

@Composable
fun SendCardSelects(
    dialogContainerState: ContainerState,
    viewModel: AppStatus
){
    Row(
        horizontalArrangement = Arrangement.spacedBy(50.dp),
        modifier = Modifier
            .padding(vertical = 2.dp, horizontal = 10.dp)
    ) {
        SendCardSelect(
            name = R.string.forum,
            value = viewModel.forumMap[viewModel.sendForum]?.name ?: "",
            icon = R.drawable.arrow_drop_down,
            description = R.string.forum_select,
            viewModel = viewModel
        ) {
            dialogContainerState.open { 
                contentAlignment = Alignment.Center
                composable = {
                    SelectDialog(
                        list = viewModel.allowNewStrandForum,
                        selected = viewModel.sendForum,
                        viewModel = viewModel,
                        getShowString = {name},
                        select = {
                            viewModel.sendForum = viewModel.allowNewStrandForum.indexOf(it)
                            dialogContainerState.close()
                        },
                        close = dialogContainerState::close
                    )
                }
            }
        }
        SendCardSelect(
            name = R.string.cookie,
            value = viewModel.cookies.getOrNull(viewModel.cookieSelected)?.cookie ?: stringResource(
                id = R.string.cookie_not_exist
            ),
            icon = R.drawable.arrow_drop_down,
            description = R.string.cookie_select,
            viewModel = viewModel
        ) {
            dialogContainerState.open {
                contentAlignment = Alignment.Center
                composable = {
                    SelectDialog(
                        list = viewModel.cookies,
                        selected = viewModel.cookieSelected,
                        viewModel =viewModel ,
                        getShowString = {cookie},
                        select = {
                            viewModel.sendForum = viewModel.cookies.indexOf(it)
                        },
                        close = dialogContainerState::close
                    )
                }
            }
        }
    }
}

@Composable
fun SendCardBottomBarItem(
    modifier: Modifier = Modifier,
    icon: Int,
    description: Int,
    tint: Color,
    viewModel: AppStatus,
    onClick: () -> Unit
) {
    Icon(
        painter = painterResource(id = icon),
        contentDescription = stringResource(id = description),
        modifier = modifier
            .size(viewModel.iconSize.dp)
            .noRippleClickable(onClick = onClick),
        tint = tint
    )
}

@Composable
fun Emoji(
    focus: Int,
    tabIndex: Int,
    viewModel: AppStatus,
    tabIndexRevise: (Int) -> Unit
) {
    val textFieldValue = when (focus) {
        0 -> viewModel.nick
        1 -> viewModel.title
        else -> viewModel.sendContent
    }
    val tabRowTitles = stringArrayResource(id = R.array.emoji_tab_title)
    TabRow(selectedTabIndex = tabIndex) {
        tabRowTitles.forEachIndexed { index, item ->
            Text(
                text = item,
                modifier = Modifier
                    .selectable(
                        selected = tabIndex == index,
                        indication = null,
                        interactionSource = MutableInteractionSource(),
                        role = Role.Tab
                    ) {
                        tabIndexRevise(index)
                    }
                    .padding(vertical = 10.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        }
    }
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.height(120.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ){
        items(emojiMap[tabIndex]){
            Text(
                text = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .noRippleClickable {
                        val first =
                            textFieldValue.text[0, textFieldValue.selection.start]
                        val last =
                            textFieldValue.text[textFieldValue.selection.start, textFieldValue.text.length]
                        val new = textFieldValue.copy(
                            first + it + last,
                            if (textFieldValue.selection.length == 0) {
                                TextRange(textFieldValue.selection.start + it.length)
                            } else {
                                TextRange(
                                    textFieldValue.selection.start,
                                    textFieldValue.selection.start + it.length
                                )
                            }
                        )
                        when (focus) {
                            0 -> viewModel.nick = new
                            1 -> viewModel.title = new
                            else -> viewModel.sendContent = new
                        }
                    },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSecondary
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SendCardBottomBar(
    viewModel: AppStatus,
    focusRequester: FocusRequester,
    isEmoji: MutableState<Boolean>,
    focus: Int,
    containerState: ContainerState
){
    val imeStatus = imeStatus()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    var isEmojiShow by isEmoji
    var tabIndex by remember {
        mutableStateOf(0)
    }

    val context = LocalContext.current as ComponentActivity

    val result =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            if (it !== null) {
                context.contentResolver.openFileDescriptor(it, "r").use { pfd ->
                    pfd?.fileDescriptor?.let { fd ->
                        viewModel.sendImages.add(
                            BitmapFactory.decodeFileDescriptor(fd).asImageBitmap()
                        )
                    }
                }
            }
        }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SendCardBottomBarItem(
            icon = R.drawable.expand_more,
            description = R.string.collapse_keyboard,
            tint = MaterialTheme.colorScheme.onSecondary,
            viewModel = viewModel,
            modifier = Modifier.rotate(animateFloatAsState(targetValue = if (imeStatus) 0f else 180f).value)
        ) {
            isEmojiShow = false
            if (imeStatus) {
                focusManager.clearFocus()
            }else {
                focusRequester.requestFocus()
                keyboardController?.show()
            }
        }
        SendCardBottomBarItem(
            icon = R.drawable.draw,
            description = R.string.draw,
            tint = MaterialTheme.colorScheme.onSecondary,
            viewModel = viewModel
        ) {
            containerState.open {
                composable = {
                    DrawView()
                }
            }
        }
        SendCardBottomBarItem(
            icon = R.drawable.sentiment_satisfied,
            description = R.string.emoji,
            tint = MaterialTheme.colorScheme.onSecondary,
            viewModel = viewModel
        ) {
            keyboardController?.hide()
            isEmojiShow = !isEmojiShow
        }
        SendCardBottomBarItem(
            icon = R.drawable.imagesmode,
            description = R.string.image_mode,
            tint = MaterialTheme.colorScheme.onSecondary,
            viewModel = viewModel
        ) {
            result.launch("image/*")
        }
        SendCardBottomBarItem(
            icon = R.drawable.dice,
            description = R.string.random,
            tint = MaterialTheme.colorScheme.onSecondary,
            viewModel = viewModel
        ) {
            val first =
                viewModel.sendContent.text.substring(
                    0,
                    viewModel.sendContent.selection.start
                )
            val last = viewModel.sendContent.text.substring(
                viewModel.sendContent.selection.start,
                viewModel.sendContent.text.length
            )
            viewModel.sendContent = viewModel.sendContent.copy(
                "$first[0-9]$last",
                if (viewModel.sendContent.selection.length == 0){
                    TextRange(viewModel.sendContent.selection.start + 5)
                }else{
                    TextRange(
                        viewModel.sendContent.selection.start,
                        viewModel.sendContent.selection.start + 5
                    )
                }

            )
        }
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.onSecondary)
                .width(50.dp)
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            SendCardBottomBarItem(
                icon = R.drawable.send,
                description = R.string.send,
                tint = MaterialTheme.colorScheme.background,
                viewModel = viewModel
            ) {

            } 
        }
    }
    AnimatedVisibility(visible = isEmojiShow) {
        Column {
            Emoji(
                focus = focus,
                tabIndex = tabIndex,
                viewModel = viewModel
            ){
                tabIndex = it
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColumnScope.SendCardImages(
    viewModel: AppStatus
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ){
        itemsIndexed(viewModel.sendImages){index, item ->
            Image(item,
                contentDescription = stringResource(id = R.string.image_mode),
                modifier = Modifier
                    .border(1.dp, MaterialTheme.colorScheme.onSecondary)
                    .combinedClickable(
                        indication = null,
                        interactionSource = remember {
                            MutableInteractionSource()
                        },
                        onClick = {},
                        onLongClick = {
                            viewModel.sendImages.removeAt(index)
                        }
                    )
                    .size(50.dp)
            )
        }
    }
}

@Composable
fun BoxScope.SendCardContent(
    containerState: ContainerState,
    viewModel: AppStatus
) {
    var moreIsOpen by remember {
        mutableStateOf(false)
    }
    var focus by remember {
        mutableStateOf(2)
    }
    var isFull by remember {
        mutableStateOf(false)
    }
    val focusManager = LocalFocusManager.current
    val focusRequester = remember {
        FocusRequester()
    }
    val fullState by animateFloatAsState(targetValue =
        if (isFull)
            1f
        else
            0.6f
    )
    val isEmoji = remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .systemBarsPadding()
            .imePadding()
            .fillMaxHeight(fullState)
            .align(Alignment.BottomCenter)
    ) {
        Card(
            shape = MaterialTheme.shapes.medium.copy(bottomStart = CornerSize(0.dp), bottomEnd = CornerSize(0.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            SendCardStateBar(
                viewModel = viewModel,
                rotate = animateFloatAsState(targetValue = if (!moreIsOpen) 180f else 0f).value,
                fullRevise = {isFull = !isFull}
            ){
                if (focus == 0){
                    isEmoji.value = false
                    focusManager.moveFocus(FocusDirection.Down)
                    focusManager.moveFocus(FocusDirection.Down)
                    focus = 2
                }else if (focus == 1){
                    isEmoji.value = false
                    focusManager.moveFocus(FocusDirection.Down)
                    focus = 2
                }
                moreIsOpen = !moreIsOpen
            }
            MoreInputField(
                isOpen = moreIsOpen,
                viewModel = viewModel,
                emojiShowRevise = {
                    isEmoji.value = false
                }
            ){ state, focusToken ->
                if (state.isFocused){
                    focus = focusToken
                }
            }
            InputField(
                focusRequester = focusRequester,
                viewModel = viewModel,
                emojiShowRevise = {
                    isEmoji.value = false
                }
            ){state, focusToken ->
                if (state.isFocused){
                    focus = focusToken
                }
            }
            SendCardImages(viewModel = viewModel)
            SendCardSelects(dialogContainerState = containerState,viewModel = viewModel)
            SendCardBottomBar(
                focusRequester = focusRequester,
                viewModel = viewModel,
                focus = focus,
                isEmoji = isEmoji,
                containerState = containerState
            )
        }
    }
}

@Composable
fun SendCard(

) {
    val containerState = rememberContainerState()
    val viewModel = rememberViewModel<AppStatus>()
    Container(
        state = containerState,
        background = Color.Transparent
    ) {
        SendCardContent(containerState, viewModel)
    }

}
