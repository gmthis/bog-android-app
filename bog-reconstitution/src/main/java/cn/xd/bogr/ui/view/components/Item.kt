package cn.xd.bogr.ui.view.components

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.xd.bogr.R
import cn.xd.bogr.net.entity.*
import cn.xd.bogr.net.requestSingleContent
import cn.xd.bogr.ui.theme.Grey
import cn.xd.bogr.ui.theme.OnGrey_Grey
import cn.xd.bogr.util.*
import cn.xd.bogr.viewmodel.AppStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun Item(
    content: Content,
    listState: LazyListState,
    notIsDetails: Boolean
) {
    val viewModel = rememberViewModel<AppStatus>()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .run {
                if (notIsDetails){
                    noRippleClickable {
                        viewModel.contentMap[content.id] = content
                        viewModel.saveForumListOffset(listState)
                        viewModel.navController.navigate("details/${content.id}")
                    }
                }else this
            }

    ) {
        Box{
            when(content){
                is Strand -> StrandItem(content, listState, notIsDetails, viewModel)
                is Reply -> ReplyItem(content, listState, notIsDetails, viewModel)
                is Single -> SingleItem(content, listState, notIsDetails, viewModel)
            }
        }
        Divider(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp)
        )
    }
}

@Composable
fun DetailsItem(
    content: Content,
    po: Content,
    listState: LazyListState,
    notIsDetails: Boolean = true
){
    val viewModel = rememberViewModel<AppStatus>()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .run {
                if (notIsDetails && content.res == 0){
                    noRippleClickable {
                        viewModel.contentMap[content.id] = content
                        val poId = if (po is Reply) po.res else po.id
                        viewModel.listOffsetMap[poId] = listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
                        viewModel.navController.navigate("details/${content.id}")
                    }
                }else this
            }
    ) {
        Divider(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp)
        )
        Box{
            when(content){
                is Strand -> StrandItem(content, listState, notIsDetails, viewModel, po)
                is Reply -> ReplyItem(content, listState, notIsDetails, viewModel, po)
                is Single -> SingleItem(content, listState, notIsDetails, viewModel, po)
            }
        }
        Divider(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 8.dp)
        )
    }
}

@Composable
fun ItemContainer(
    content: Content,
    listState: LazyListState,
    isDetails: Boolean,
    viewModel: AppStatus,
    po: Content?,
    composable: (@Composable ColumnScope.() -> Unit)? = null
){
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
        ItemInfo(content = content, viewModel)
        if (composable != null){
            composable()
        }
        ItemContent(content = content, isDetails, listState, viewModel, po)
    }
}

@Composable
fun StrandItem(content: Strand,
               listState: LazyListState, isDetails: Boolean, viewModel: AppStatus, po: Content? = null){
    ItemContainer(content = content,listState, isDetails, viewModel, composable = if (isDetails){{
        ItemMoreInfo(content = content, viewModel = viewModel){
            Row(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(vertical = 2.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.chat_bubble),
                    contentDescription = stringResource(id = R.string.rep_num),
                    modifier = Modifier.size(viewModel.s4FontSize.dp),
                    tint = MaterialTheme.colorScheme.onSecondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = content.replyCount.toString(),
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = viewModel.sssFontSize.sp
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = viewModel.forumMap[content.forum]?.name.toString(),
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(vertical = 2.dp, horizontal = 8.dp),
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = viewModel.sssFontSize.sp
            )
        }
    }}else null, po = po)
}

@Composable
fun ReplyItem(content: Reply,listState: LazyListState, isDetails: Boolean, viewModel: AppStatus, po: Content? = null){
    ItemContainer(content = content,listState, isDetails, viewModel, composable = if(content.name != ""){{
        ItemInfo(content = content, viewModel = viewModel)
    }}else null, po = po)
}

@Composable
fun SingleItem(content: Single,listState: LazyListState, isDetails: Boolean, viewModel: AppStatus,po: Content? = null){
    ItemContainer(content = content,listState, isDetails, viewModel, composable = if(content.title != "" || content.name != ""){{
        ItemInfo(content = content, viewModel = viewModel)
    }}else null, po = po)
}

@Composable
fun ItemInfo(content: Content, viewModel: AppStatus) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = content.cookie,
            color = MaterialTheme.colorScheme.onSecondary,
            fontSize = viewModel.fontSize.sp,
            modifier = Modifier.weight(0.33f)
        )
        Text(
            text = "Po.${content.id}",
            fontSize = viewModel.fontSize.sp,
            modifier = Modifier.weight(0.33f),
            textAlign = TextAlign.Center
        )
        Text(
            text = content.time.getTimeDifference(),
            color = MaterialTheme.colorScheme.scrim,
            fontSize = viewModel.fontSize.sp,
            modifier = Modifier.weight(0.33f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun ItemMoreInfo(
    content: Content,
    viewModel: AppStatus,
    composable: (@Composable RowScope.() -> Unit)? = null
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = content.name,
            fontSize = viewModel.ssFontSize.sp,
            modifier = Modifier.weight(0.30f),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSecondary
        )
        Text(
            text = content.title ?: "",
            fontSize = viewModel.ssFontSize.sp,
            modifier = Modifier.weight(0.30f),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSecondary
        )
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.weight(0.40f)
        ){
            composable?.invoke(this)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ItemContent(
    content: Content,
    isDetails: Boolean,
    listState: LazyListState,
    viewModel: AppStatus,
    po: Content?,
){
    Column(
        modifier = Modifier.fillMaxWidth()
    ){
        RichText(content = content, listState, viewModel)
        if (content.images != null){
            Spacer(modifier = Modifier.height(8.dp))
            if (isDetails){
                Box(modifier = Modifier.fillMaxSize(0.5f).height(120.dp)){
                    LoadingThumbImage(image = content.images!![0]) {
                        viewModel.strandImageMap[content.id] = LinkedHashSet<Image>().also {
                            it.addAll(content.images!!)
                        }
                        if (po != null){
                            val poId = if (po is Reply) po.res else po.id
                            viewModel.listOffsetMap[poId] = listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
                        }else{
                            viewModel.saveForumListOffset(listState)
                        }
                        viewModel.navController.navigate(
                            "imageDetails/${content.id}/0/false"
                        )
                    }
                }
            }else{
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    maxItemsInEachRow = 2,
                ) {
                    for (image in content.images!!) {
                        Box(
                            modifier = Modifier.fillMaxSize(0.5f).height(120.dp)
                        ){
                            LoadingThumbImage(image = image) {
                                val id = if (content is Strand) content.id else content.res
                                viewModel.listOffsetMap[id] = listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
                                viewModel.navController.navigate(
                                    "imageDetails/${id}/${
                                        viewModel.strandImageMap[id]!!.indexOf(
                                            image
                                        )
                                    }/true"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RichText(
    content: Content,
    listState: LazyListState,
    viewModel: AppStatus
){
    val segmentation = htmlSegmentation(content.content)
    val paragraphs = mutableListOf<Paragraph>()

    for (s in segmentation) {
        paragraphs += htmlLabelExtract(s)
    }

    for (paragraph in paragraphs) {

        if (paragraph.htmlLabel == null){
            Text(text = htmlUnescape(paragraph.content), fontSize = viewModel.fontSize.sp)
            continue
        }

        var last = ""
        for (index in paragraph.htmlLabel!!.indices) {
            val htmlLabel = paragraph.htmlLabel!![index]
            val split = if (last == ""){
                paragraph.content.split(htmlLabel.content)
            }else{
                last.split(htmlLabel.content)
            }
            last = split[1]
            val first = split[0]
            if (first != ""){
                Text(
                    text = htmlUnescape(first),
                    fontSize = viewModel.fontSize.sp
                )
            }
            when(htmlLabel.labelName){
                "b" -> BText(htmlLabel.content[3, htmlLabel.content.indexOf('(')], viewModel)
                "span" -> SpanText(
                    content = content,
                    showId = htmlUnescape(htmlLabel.content[20, htmlLabel.content.length - 7]).substring(
                        5
                    ),
                    listState,
                    viewModel
                )
                "a" -> AText(htmlLabel = htmlLabel, viewModel)
                else -> Text(text = htmlLabel.content, color = MaterialTheme.colorScheme.error)
            }
            if (last != "" && paragraph.htmlLabel!!.size - (index + 1) == 0){
                Text(
                    text = htmlUnescape(last),
                    fontSize = viewModel.fontSize.sp
                )
            }
        }
    }
}

@Composable
fun BText(content: String, viewModel: AppStatus) {
    Text(text = "\uD83C\uDFB2 $content", fontSize = viewModel.fontSize.sp)
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun SpanText(
    content: Content,
    showId: String,
    listState: LazyListState,
    viewModel: AppStatus
) {
    if (content.citeIsOpen[showId] == null)
        content.citeIsOpen[showId] = mutableStateOf(false)
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "引用 Po.$showId 单击展开",
            color = OnGrey_Grey,
            modifier = Modifier
                .background(Grey)
                .noRippleClickable {
                    content.citeIsOpen[showId]!!.value =
                        !content.citeIsOpen[showId]!!.value
                },
            fontSize = viewModel.fontSize.sp
        )
    }
    AnimatedVisibility(
        visible =content.citeIsOpen[showId]?.value == true,
        enter = expandIn { IntSize(it.width, 0) } + fadeIn(),
        exit = shrinkOut { IntSize(it.width, 0) } + fadeOut()
    ) {
        var single by content.cite[showId] ?: mutableStateOf<Content?>(null).also {
            content.cite[showId] = it
        }
        var isError by remember {
            mutableStateOf(false)
        }
        if (single == null){
            LaunchedEffect(Unit){
                launch(Dispatchers.IO){
                    val singleContent = requestSingleContent(showId)
                    if (singleContent.code != 6001) isError = true
                    else single = singleContent.info
                }
            }
        }
        single?.let {
            DetailsItem(content = it, content, listState,true)
        }
    }
}

@Composable
fun AText(
    htmlLabel: HTMLLabel,
    viewModel: AppStatus
) {
    val uriHandler = LocalUriHandler.current
    val link = htmlLabel.attr?.get("title") ?: "错误"
    Text(
        text = link,
        modifier = Modifier.clickable(
            indication = null,
            interactionSource = MutableInteractionSource(),
            onClick = {
                uriHandler.openUri(link)
            }
        ),
        color = MaterialTheme.colorScheme.onSecondary,
        fontSize = viewModel.fontSize.sp
    )
}