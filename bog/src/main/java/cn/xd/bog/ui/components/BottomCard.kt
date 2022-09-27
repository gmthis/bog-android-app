package cn.xd.bog.ui.components

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import cn.xd.bog.R
import cn.xd.bog.entity.Forum
import cn.xd.bog.ui.page.DrawPage
import cn.xd.bog.ui.theme.*
import cn.xd.bog.viewmodel.AppStatus
import com.mxalbert.zoomable.Zoomable

@Composable
fun BottomCard(
    modifier: Modifier = Modifier,
    isFull: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
){
    Column(
        modifier = modifier
            .navigationBarsPadding()
            .imePadding()
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    topStart = 10.dp,
                    topEnd = 10.dp
                )
            )
            .background(if (isSystemInDarkTheme()) Black else White)
            .padding(vertical = 10.dp, horizontal = 5.dp)
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
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SendString(
    forums: Forum?,
    forumsSelectedItem: Int,
    fontSize: Int,
    appStatus: AppStatus,
    close: () -> Unit
){
    var content by remember {
        mutableStateOf(TextFieldValue())
    }
    var nick by remember {
        mutableStateOf(TextFieldValue())
    }
    var title by remember {
        mutableStateOf(TextFieldValue())
    }
    var dialogFlag by remember {
        mutableStateOf(0)
    }
    var form by remember {
        mutableStateOf(
            if (forumsSelectedItem == 0)
                "综合版"
            else
                forums!!.info[forumsSelectedItem].name
        )
    }
    val iconSize: Int by remember {
        mutableStateOf(24)
    }
    val iconColor: Color by remember {
        mutableStateOf(PinkText)
    }
    var moreIsOpen by remember {
        mutableStateOf(false)
    }
    val animateState by animateFloatAsState(if (moreIsOpen) 180f else 0f)
    var isFull by remember {
        mutableStateOf(false)
    }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        contentAlignment = Alignment.BottomCenter
    ){
        BottomCard(
            isFull = isFull
        ){
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
                        modifier = Modifier.size(
                            iconSize.dp
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
            AnimatedVisibility(visible = moreIsOpen) {
                Column {
                    BasicTextField(
                        singleLine = true,
                        value = nick,
                        onValueChange = {
                            nick = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        textStyle = TextStyle(
                            fontSize = (fontSize).sp,
                            color = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Unspecified,
                                unfocusedIndicatorColor = Color.Unspecified,
                                disabledIndicatorColor = Color.Unspecified,
                                cursorColor = PinkText
                            ).textColor(enabled = true).value
                        ),
                        cursorBrush = SolidColor(PinkText),
                        decorationBox = {
                            Box(
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(WhiteBackgroundVariants)
                                    .padding(5.dp)
                            ) {
                                it()
                                if (nick.text.isEmpty()){
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
                        singleLine = true,
                        value = title,
                        onValueChange = {
                            title = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        textStyle = TextStyle(
                            fontSize = (fontSize).sp,
                            color = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Unspecified,
                                unfocusedIndicatorColor = Color.Unspecified,
                                disabledIndicatorColor = Color.Unspecified,
                                cursorColor = PinkText
                            ).textColor(enabled = true).value
                        ),
                        cursorBrush = SolidColor(PinkText),
                        decorationBox = {
                            Box(
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(WhiteBackgroundVariants)
                                    .padding(5.dp)
                            ) {
                                it()
                                if (title.text.isEmpty()){
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
                value = content,
                onValueChange = {
                    content = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp, max = 260.dp)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .run {
                         if (isFull) weight(1f) else this
                    },
                textStyle = TextStyle(
                    fontSize = (fontSize).sp,
                    color = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Unspecified,
                        unfocusedIndicatorColor = Color.Unspecified,
                        disabledIndicatorColor = Color.Unspecified,
                        cursorColor = PinkText
                    ).textColor(enabled = true).value
                ),
                cursorBrush = SolidColor(PinkText),
                decorationBox = {
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .background(WhiteBackgroundVariants)
                            .padding(5.dp)
                    ) {
                        it()
                    }
                }
            )
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
                            text = form,
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
                            text = form,
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
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.expand_more),
                    contentDescription = stringResource(id = R.string.put_away),
                    modifier = Modifier
                        .size(
                            iconSize.dp
                        )
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = {
                                keyboardController
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

                            }
                        ),
                    tint = iconColor
                )
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.send),
                    contentDescription = stringResource(id = R.string.send),
                    modifier = Modifier
                        .size(
                            iconSize.dp
                        )
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = {

                            }
                        ),
                    tint = iconColor
                )
            }
        }
        val context = LocalContext.current as ComponentActivity
        when(dialogFlag){
            1 -> {
                Dialog(onDismissRequest = { dialogFlag = 0 }) {
                    ForumSelected(forums = forums!!, selected = {
                        form = forums.info[it].name
                        dialogFlag = 0
                    })
                }
            }
        }
        AnimatedVisibility(visible = dialogFlag == 3) {
            DrawPage(drawPageInfo = appStatus.drawPageInfo, tint = PinkText)
            BackHandler {
                dialogFlag = 0
            }
            if (dialogFlag == 0){
                WindowInsetsControllerCompat(context.window, context.window.decorView).let {
                    it.show(WindowInsetsCompat.Type.systemBars())
                    it.systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }
        }
    }
}

@Composable
fun ForumSelected(
    forums: Forum,
    selected: (Int) -> Unit,
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
        for (forum in forums.info.indices) {
            if (forum != 0){
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