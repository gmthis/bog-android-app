package cn.xd.bog.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cn.xd.bog.R
import cn.xd.bog.entity.Forum
import cn.xd.bog.ui.theme.*

@Composable
fun BottomCard(
    modifier: Modifier = Modifier,
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
            )
    ) {
        content()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SendString(
    forums: Forum?,
    forumsSelectedItem: Int,
    fontSize: Int
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
        mutableStateOf(forums!!.info[forumsSelectedItem].name)
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
    Box{
        BottomCard{
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
                            ),
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
                        modifier = Modifier.size(
                            iconSize.dp
                        ),
                        tint = iconColor
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.close),
                        contentDescription = stringResource(id = R.string.close),
                        modifier = Modifier.size(
                            iconSize.dp
                        ),
                        tint = iconColor
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            val focusManager = LocalFocusManager.current
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
                    .animateContentSize(),
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
                    modifier = Modifier.weight(0.5f),
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
                            modifier = Modifier.clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick ={
                                    dialogFlag = 1
                                }
                            ),
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
                    modifier = Modifier.weight(0.5f),
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
                            modifier = Modifier.clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick ={
                                    dialogFlag = 2
                                }
                            ),
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
                    modifier = Modifier.size(
                        iconSize.dp
                    ),
                    tint = iconColor
                )
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.draw),
                    contentDescription = stringResource(id = R.string.draw),
                    modifier = Modifier.size(
                        iconSize.dp
                    ),
                    tint = iconColor
                )
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.sentiment_satisfied),
                    contentDescription = stringResource(id = R.string.sentiment_satisfied),
                    modifier = Modifier.size(
                        iconSize.dp
                    ),
                    tint = iconColor
                )
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.imagesmode),
                    contentDescription = stringResource(id = R.string.imagesmode),
                    modifier = Modifier.size(
                        iconSize.dp
                    ),
                    tint = iconColor
                )
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.dice),
                    contentDescription = stringResource(id = R.string.dice_icon),
                    modifier = Modifier.size(
                        iconSize.dp
                    ),
                    tint = iconColor
                )
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.send),
                    contentDescription = stringResource(id = R.string.send),
                    modifier = Modifier.size(
                        iconSize.dp
                    ),
                    tint = iconColor
                )
            }
        }
        if (dialogFlag != 0){
            Dialog(onDismissRequest = { dialogFlag = 0 }) {
                when(dialogFlag){
                    1 -> {
                        ForumSelected(forums = forums!!, selected = {
                            form = forums.info[it].name
                        })
                    }
                    2 -> {

                    }
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
                10.dp
            )
    ) {
        for (forum in forums.info.indices) {
            Text(
                text = forums.info[forum].name,
                modifier = Modifier.clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null,
                    onClick = {
                        selected(forum)
                    }
                )
            )
        }
    }
}