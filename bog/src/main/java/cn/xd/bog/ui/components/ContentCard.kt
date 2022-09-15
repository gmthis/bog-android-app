package cn.xd.bog.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.hutool.http.HtmlUtil
import cn.xd.bog.R
import cn.xd.bog.entity.*
import cn.xd.bog.ui.theme.*
import cn.xd.bog.util.getTimeDifference
import coil.compose.AsyncImage
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun ContentCard(
    index: Int,
    content: Info?,
    forum: Map<Int, String>,
    fontSize: Int,
    jump: ((Int) -> Unit)? = null,
    pullContent: (
        id: String,
        container: MutableState<SingleContentInfo?>,
        error: (Int, String) -> Unit,
        after: (() -> Unit)?
    ) -> Unit,
    details: Boolean,
    imageDetails: (String, Int) -> Unit,
    po: Info? = null,
    isInternal: Boolean = false
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .run {
                if (isInternal) {
                    padding(
                        start = 0.dp,
                        end = 0.dp,
                        top = 4.dp,
                        bottom = 8.dp
                    )
                } else {
                    padding(
                        start = 10.dp,
                        end = 10.dp,
                        top = if (index == 0) 8.dp else 4.dp,
                        bottom = 4.dp
                    )
                }
            }
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = {
                    if (content !== null && !details) {
                        jump?.invoke(content.id)
                    }
                }
            ),
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = if (isInternal) 0.dp else 10.dp, vertical = 10.dp)
        ) {
//            EvenlyDividedRow {
//                Cookie(content?.cookie, fontSize)
//                ContentId(content?.id, fontSize)
//                Time(content?.time, fontSize)
//            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.weight(0.33f)
                ) {
                    Cookie(content?.cookie, fontSize)
                    if (details && content?.cookie == po?.cookie && content != null){
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Po",
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.medium)
                                .background(PinkBackground),
                            color = White
                        )
                    }
                }
                Box(
                    modifier = Modifier.weight(0.33f)
                ) {
                    ContentId(content?.id, fontSize)
                }
                Box(
                    modifier = Modifier.weight(0.33f)
                ) {
                    Time(content?.time, fontSize)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NickName(content?.name, details, fontSize)
                if (content is ForumContentInfo && !details) {
                    Row {
                        ReplyQuantity(content.replyCount, fontSize)
                        Spacer(modifier = Modifier.width(10.dp))
                        ForumName(forum[content.forum], fontSize)
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            if (content is ForumContentInfo || content is SingleContentInfo) {
                Title(content.title, fontSize)
            }
            Content(
                content,
                fontSize = fontSize,
                pullContent = pullContent,
                details = details,
                imageDetails = imageDetails,
                jump = jump,
                po = po
            )
        }
    }
}

@Composable
fun Cookie(
    text: String?,
    fontSize: Int,
    modifier: Modifier = Modifier,
) {
    LoadingText(
        text = text,
        modifier = modifier,
        color = PinkText,
        fontSize = (fontSize - 2).sp
    )
}

@Composable
fun ContentId(
    text: Int?,
    fontSize: Int,
    modifier: Modifier = Modifier,
) {
    LoadingText(
        text = if (text != null) "Po.$text" else null,
        modifier = modifier,
        fontSize = (fontSize - 2).sp
    )
}

@Composable
fun Time(
    text: Long?,
    fontSize: Int,
    modifier: Modifier = Modifier,
) {
    LoadingText(
        text = text?.getTimeDifference(),
        color = Unselected,
        modifier = modifier,
        fontSize = (fontSize - 4).sp
    )
}

@Composable
fun NickName(
    text: String?,
    details: Boolean,
    fontSize: Int,
    modifier: Modifier = Modifier,
) {
    if (text != "" || !details){
        LoadingText(
            text = text,
            modifier = modifier,
            color = PinkText,
            fontSize = (fontSize - 2).sp
        )
    }
}

@Composable
fun ReplyQuantity(
    text: Int?,
    fontSize: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(PinkBackgroundVariants)
            .padding(vertical = 2.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.chat_bubble),
            contentDescription = stringResource(id = R.string.rep_num),
            modifier = Modifier
                .size((fontSize - 4).dp),
            tint = PinkTextVariants
        )
        Spacer(modifier = Modifier.width(4.dp))
        LoadingText(
            text = text?.toString(),
            modifier = modifier,
            color = PinkTextVariants,
            fontSize = (fontSize - 2).sp
        )
    }
}

@Composable
fun ForumName(
    text: String?,
    fontSize: Int,
    modifier: Modifier = Modifier,
) {
    LoadingText(
        text = text,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(PinkBackgroundVariants)
            .padding(vertical = 2.dp, horizontal = 8.dp),
        color = PinkTextVariants,
        fontSize = (fontSize - 2).sp
    )
}

@Composable
fun Title(
    text: String?,
    fontSize: Int,
    modifier: Modifier = Modifier,
) {
    if (text != "") {
        LoadingText(
            text = text,
            modifier = modifier,
            color = PinkTextVariants,
            fontSize = (fontSize + 2).sp
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
fun Content(
    content: Info?,
    fontSize: Int,
    modifier: Modifier = Modifier,
    pullContent: (
        id: String,
        container: MutableState<SingleContentInfo?>,
        error: (Int, String) -> Unit,
        after: (() -> Unit)?
    ) -> Unit,
    details: Boolean,
    imageDetails: (String, Int) -> Unit,
    jump: ((Int) -> Unit)?,
    po: Info?
) {
    Column(
        modifier = Modifier.animateContentSize()
    ) {
        Column(
            modifier = modifier.fillMaxWidth(1f)
        ) {
            ComplexText(
                fontSize = fontSize,
                content = content,
                pullContent = pullContent,
                details = details,
                imageDetails = imageDetails,
                jump = jump,
                po = po
            )
        }
//        LoadingText(
//            text = content?.content,
//            modifier = modifier.run {
//                if (content?.images != null){
//                    fillMaxWidth(0.6f)
//                }else {
//                    fillMaxWidth(1f)
//                }
//            },
//            fontSize = fontSize.sp
//        )
        if (content?.images != null) {
            if (!details) {
                Spacer(modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp))
                LoadingImage(
                    image = content.images!![0],
                    imageDetails = imageDetails,
                    modifier = Modifier
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = {
                                imageDetails(Json.encodeToString(content.images), 0)
                            }
                        )
                )
            } else {
                FlowRow(
                    modifier = modifier.fillMaxWidth()
                ) {
                    for (image in content.images!!.indices) {
                        LoadingImage(
                            image = content.images!![image],
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 10.dp)
                                .clickable(
                                    interactionSource = MutableInteractionSource(),
                                    indication = null,
                                    onClick = {
                                        imageDetails(Json.encodeToString(content.images), image)
                                    }
                                ),
                            imageDetails = imageDetails
                        )
                    }
                }
            }
        }
    }
}

private val regex = "<[abspn]+.*?>.*?</[abspn]*?>".toRegex()
private val htmlRegex = "<(\\S*?)[^>]*>.*?|<.*? />".toRegex()
private val httpRegex =
    "https?://[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\\.?.*?\"".toRegex()

@SuppressLint("UnrememberedMutableState")
@Composable
fun ComplexText(
    fontSize: Int,
    content: Info?,
    pullContent: (
        id: String,
        container: MutableState<SingleContentInfo?>,
        error: (Int, String) -> Unit,
        after: (() -> Unit)?
    ) -> Unit,
    details: Boolean,
    imageDetails: (String, Int) -> Unit,
    jump: ((Int) -> Unit)?,
    po: Info?
) {
    if (content == null) {
        LoadingText(
            text = null,
            fontSize = fontSize.sp
        )
        return
    }

    val list = HtmlUtil.unescape(content.content).split("<br />", "<br/>", "\n")
    var count = 0
    for (string in list) {
        if (!details && count > 5) {
            break
        } else {
            count++
        }
        val results = regex.findAll(string).iterator()
        if (!results.hasNext()) {
            Text(
                text = string,
                fontSize = fontSize.sp
            )
        }
        for (result in results) {
            when (result.value[1]) {
                'b' -> {
                    Text(
                        text = string.substring(result.range).run {
                            replace(
                                this,
                                "🎲 " + this.substring(3, this.length - 4)
                            )
                        },
                        fontSize = fontSize.sp
                    )
                }
                's' -> {
                    val id = string.substring(result.range).run {
                        htmlRegex.replace(this, "")
                    }
                    if (content.quoteIsOpen == null) {
                        content.quoteIsOpen = mutableStateMapOf()
                        content.quoteIsOpen!![id] = mutableStateOf(false)
                    }
                    if (content.quoteIsOpen!![id] == null) {
                        content.quoteIsOpen!![id] = mutableStateOf(false)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null,
                                onClick = {
                                    content.quoteIsOpen!![id]!!.value =
                                        !content.quoteIsOpen!![id]!!.value
                                }
                            )
                            .padding(vertical = 5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = id,
                            color = Grey,
                            modifier = Modifier
                                .background(GreyBackground)
                                .padding(vertical = 1.dp, horizontal = 3.dp)
                        )
                        if (content.res == 0) {
                            AnimatedVisibility(
                                visible = content.quoteIsOpen?.get(id)?.value == true,
                                enter = slideIn{
                                    IntOffset(it.width, 0)
                                }
                            ) {
                                Text(
                                    text = "单击卡片或此处前往原串",
                                    color = Grey,
                                    modifier = Modifier
                                        .background(GreyBackground)
                                        .padding(vertical = 1.dp, horizontal = 3.dp)
                                        .clickable(
                                            interactionSource = MutableInteractionSource(),
                                            indication = null,
                                            onClick = {
                                                jump?.invoke(content.quote?.get(id)?.value?.id ?: 0)
                                            }
                                        )
                                )
                            }
                        }

                    }
                    AnimatedVisibility(visible = content.quoteIsOpen!![id]!!.value) {
                        if (content.quote != null) {
                            val singleContentInfo = content.quote?.get(id)
                            if (singleContentInfo?.value != null) {
                                ContentCard(
                                    index = 0,
                                    content = singleContentInfo.value,
                                    forum = emptyMap(),
                                    fontSize = fontSize,
                                    pullContent = pullContent,
                                    details = details,
                                    imageDetails = imageDetails,
                                    jump = jump,
                                    po = po,
                                    isInternal = true
                                )
                            } else {
                                val info: MutableState<SingleContentInfo?> =
                                    if (singleContentInfo?.value == null) {
                                        remember {
                                            mutableStateOf(null)
                                        }
                                    } else {
                                        singleContentInfo
                                    }
                                if (singleContentInfo?.value == null){
                                    content.quote?.set(id, info)
                                }
                                if (po is StringContentInfo){
                                    val idNum = id.substring(5).toInt()
                                    for (reply in po.reply) {
                                        if (idNum == reply.id){
                                            info.value = reply.toSingleContentInfo()
                                            break
                                        }
                                    }
                                }
                                if (info.value == null){
                                    LaunchedEffect(Unit) {
                                        launch(Dispatchers.IO) {
                                            pullContent(
                                                id.substring(5),
                                                info,
                                                { code, text ->
                                                    println(code)
                                                    println(text)
                                                },
                                                {

                                                }
                                            )
                                        }
                                    }
                                }
                                ContentCard(
                                    index = 0,
                                    content = info.value,
                                    forum = emptyMap(),
                                    fontSize = fontSize,
                                    pullContent = pullContent,
                                    details = details,
                                    imageDetails = imageDetails,
                                    jump = jump,
                                    po = po,
                                    isInternal = true
                                )
                            }
                        } else {
                            content.quote = mutableStateMapOf()
                            val info: MutableState<SingleContentInfo?> = remember {
                                mutableStateOf(null)
                            }
                            content.quote?.set(id, info)
                            LaunchedEffect(Unit) {
                                launch(Dispatchers.IO) {
                                    pullContent(
                                        id.substring(5),
                                        content.quote!![id]!!,
                                        { code, text ->
                                            println(code)
                                            println(text)
                                        },
                                        {

                                        }
                                    )
                                }
                            }
                            ContentCard(
                                index = 0,
                                content = info.value,
                                forum = emptyMap(),
                                fontSize = fontSize,
                                pullContent = pullContent,
                                details = details,
                                imageDetails = imageDetails,
                                jump = jump,
                                po = po,
                                isInternal = true
                            )
                        }
                    }
                }
                'a' -> {
                    val start = string.substring(0, result.range.first).trim()
                    val link =
                        httpRegex.find(result.value.substring(0, result.value.length))!!.value.run {
                            substring(0, this.length - 1)
                        }
                    val end = string.substring(result.range.last + 1, string.length).trim()

                    Text(text = start)
                    val uriHandler = LocalUriHandler.current
                    Text(
                        text = link,
                        modifier = Modifier.clickable(
                            indication = null,
                            interactionSource = MutableInteractionSource(),
                            onClick = {
                                uriHandler.openUri(link)
                            }
                        ),
                        color = PinkTextVariants
                    )
                    Text(text = end)
                }
            }
        }
    }
    if (count > 5 && count < list.size) {
        Text(
            text = "......"
        )
    }
}

@Composable
fun LoadingImage(
    image: Image,
    modifier: Modifier = Modifier,
    imageDetails: (String, Int) -> Unit
) {
    Box(
        modifier = modifier
    ) {
        var isLoading by remember {
            mutableStateOf(true)
        }
        if (isLoading) {
            Image(
                bitmap = ImageBitmap.imageResource(
                    id = R.drawable.loading
                ),
                contentDescription = stringResource(id = R.string.loading),
                modifier = Modifier
                    .placeholder(
                        visible = true,
                        color = Unselected,
                        shape = MaterialTheme.shapes.medium,
                        highlight = PlaceholderHighlight.shimmer(
                            Color.White
                        )
                    ),
                contentScale = ContentScale.None
            )
        }
        AsyncImage(
            model = "http://www.bog.ac/image/thumb/${
                if (image.url == "image does not exist") {
                    "nodata.jpg"
                } else {
                    image.url + image.ext
                }
            }",
            contentDescription = stringResource(id = R.string.con_img),
            onSuccess = {
                isLoading = false
            },
            modifier = Modifier
                .wrapContentWidth(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun EvenlyDividedRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        var size = 0
        val spacing = constraints.maxWidth / 3

        val placeables = measurables.map { measurable ->
            measurable.maxIntrinsicWidth(constraints.maxHeight)
            measurable.measure(constraints)
        }

        layout(constraints.minWidth, placeables[0].height) {
            val id = placeables[0]
            val stringId = placeables[1]
            val time = placeables[2]
            id.placeRelative(y = 0, x = size)
            size += spacing
            stringId.placeRelative(y = 0, x = size)
            size += spacing + (spacing - placeables[2].width)
            time.placeRelative(y = 0, x = size)
        }
    }
}