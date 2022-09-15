package cn.xd.bog.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.xd.bog.R
import cn.xd.bog.entity.StringContent
import cn.xd.bog.ui.theme.Black
import cn.xd.bog.ui.theme.BogTheme
import cn.xd.bog.ui.theme.PinkText

@Composable
fun StringContentTopBar(
    stringId: String,
    forum: String?,
    repliesCount: Int,
    stringContent: StringContent?,
    back: () -> Unit,
    sort: (Int) -> Unit,
) {
    val sortAnimateFlag by animateFloatAsState(if (stringContent?.sort == 0) 0f else 180f)
    BackHandler(
        onBack = back
    )
    Column(
        modifier = Modifier.padding(horizontal = 10.dp)
    ) {
        Spacer(modifier = Modifier.statusBarsPadding())
        TopAppBar(
            elevation = 0.dp
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.back),
                contentDescription = stringResource(id = R.string.back),
                modifier = Modifier
                    .size(24.dp)
                    .clickable(
                        indication = null,
                        interactionSource = MutableInteractionSource(),
                        onClick = {
                            back()
                        }
                    )
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 40.dp)
            ) {
                Text(
                    text = "Po.$stringId",
                    fontSize = 18.sp
                )
                if (forum != null){
                    Text(
                        text = "$forum · $repliesCount",
                        fontSize = 10.sp,
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.notifications_off),
                    contentDescription = stringResource(id = R.string.remind),
                    modifier = Modifier
                        .size(22.dp)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = {

                            }
                        )
                )
                Spacer(modifier = Modifier.padding(start = 10.dp))
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.sort),
                    contentDescription = stringResource(id = R.string.sort),
                    modifier = Modifier
                        .size(22.dp)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = {
                                if (stringContent != null) {
                                    if (stringContent.sort == 0) {
                                        stringContent.sort = 1
                                    } else {
                                        stringContent.sort = 0
                                    }
                                    sort(stringContent.sort)
                                }
                            }
                        )
                        .rotate(sortAnimateFlag),
                    tint = if (stringContent?.sort == 0){
                        LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                    }else{
                        PinkText
                    }
                )
                Spacer(modifier = Modifier.padding(start = 10.dp))
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.filter_list),
                    contentDescription = stringResource(id = R.string.filter),
                    modifier = Modifier
                        .size(22.dp)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = {
                                if (stringContent != null){
                                    stringContent.filter = !stringContent.filter
                                }
                            }
                        ),
                    tint = if (stringContent?.filter == true){
                        PinkText
                    }else{
                        LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
                    }
                )
            }
        }
    }
}