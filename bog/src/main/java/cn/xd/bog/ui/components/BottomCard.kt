package cn.xd.bog.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cn.xd.bog.R
import cn.xd.bog.ui.theme.Black
import cn.xd.bog.ui.theme.White

@Composable
fun BottomCard(
    content: @Composable ColumnScope.() -> Unit
){
    Column(
        modifier = Modifier
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

@Composable
fun SendString(){
    var text by remember {
        mutableStateOf("")
    }
    var dialogFlag by remember {
        mutableStateOf(false)
    }
    var form by remember {
        mutableStateOf("时间线")
    }
    Box{
        BottomCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.arrow_drop_down),
                        contentDescription = stringResource(id = R.string.more)
                    )
                    Text(text = stringResource(id = R.string.newString))
                }
                Row {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.help),
                        contentDescription = stringResource(id = R.string.help)
                    )
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.fullscreen),
                        contentDescription = stringResource(id = R.string.full)
                    )
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.close),
                        contentDescription = stringResource(id = R.string.close)
                    )
                }
            }
            TextField(
                value = text,
                onValueChange = {
                    text = it
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Unspecified,
                    unfocusedIndicatorColor = Color.Unspecified,
                    disabledIndicatorColor = Color.Unspecified
                )
            )
            Row {
                Text(
                    text = form,
                    modifier = Modifier.clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick ={
                            dialogFlag = true
                        }
                    )
                )
            }
        }
        if (dialogFlag){
            Dialog(onDismissRequest = { dialogFlag = false }) {
                Column {
                    Text(text = "haha", modifier = Modifier.clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick ={
                            form = "haha"
                            dialogFlag = false
                        }
                    ))
                    Text(text = "heihei", modifier = Modifier.clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick ={
                            form = "heihei"
                            dialogFlag = false
                        }
                    ))
                    Text(text = "hoho", modifier = Modifier.clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick ={
                            form = "hoho"
                            dialogFlag = false
                        }
                    ))
                }
            }
        }
    }

}