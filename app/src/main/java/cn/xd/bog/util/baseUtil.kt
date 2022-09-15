package cn.xd.bog.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
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
import androidx.compose.runtime.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import cn.hutool.http.HtmlUtil
import cn.xd.bog.data.viewmodel.MainViewModel
import cn.xd.bog.entity.SingleContent
import cn.xd.bog.ui.components.formatter
import cn.xd.bog.ui.theme.Bubble
import cn.xd.bog.ui.theme.PinkMainVariant
import cn.xd.bog.ui.theme.Unselected
import cn.xd.bog.ui.theme.onBubble
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

private val regex = "<[a-zA-Z]+.*?>([\\s\\S]*?)</[a-zA-Z]*?>".toRegex()
private val htmlRegex = "<(\\S*?)[^>]*>.*?|<.*? />".toRegex()

//TODO 记得写一下对链接的处理,参考串内容680235
@Composable
fun String.MyFormat(
    isNull: Boolean,
    scope: CoroutineScope,
    viewModel: MainViewModel,
    heightLimit: MutableState<Int>?,
    isOpen:MutableState<Boolean>,
    navController: NavHostController,
) {
    var stringList = HtmlUtil.unescape(this)
        .split("<br />", "\n")
    Column(
        modifier = Modifier.animateContentSize().run {
            (if (isNull || isOpen.value) {
                Modifier.fillMaxWidth(1.0f)
            } else {
                Modifier.fillMaxWidth(0.6f)
            })
        }
    ) {
        for (string in stringList) {
            val find = regex.findAll(string).iterator()
            if (find.hasNext()) {
                var copy = string
                for (matchResult in find) {
                    when (matchResult.value[1]) {
                        's' -> {
                            Column {
                                val replace = htmlRegex.replace(copy, "")
                                var single: SingleContent? by remember {
                                    mutableStateOf(null)
                                }
                                var isNotLoading by remember {
                                    mutableStateOf(false)
                                }
                                LaunchedEffect(Unit) {
                                    scope.launch(Dispatchers.IO) {
                                        single = viewModel.pullSingleContent(id = replace.substring(5).trim().toInt())
                                        isNotLoading = true
                                    }
                                }
                                Text(
                                    text = replace,
                                    modifier = Modifier.clickable(
                                        onClick = {
                                            isOpen.value = !isOpen.value
                                            heightLimit?.run{
                                                if (isOpen.value){
                                                    value += 1
                                                }else {
                                                    value -= 1
                                                }
                                                println(this.value)
                                            }
                                        },
                                        indication = null,
                                        interactionSource = MutableInteractionSource()
                                    )
                                )
                                AnimatedVisibility(
                                    visible = isOpen.value,
                                    enter = expandVertically()
                                ) {
                                    var _isOpen = remember {
                                        mutableStateOf(false)
                                    }
                                    Card(
                                        modifier = Modifier
                                            .padding(
                                                vertical = 0.dp,
                                                horizontal = 0.dp
                                            )
                                            .clickable(
                                                onClick = {
                                                    navController.navigate("details/${single?.info?.id}")
                                                },
                                                indication = null,
                                                interactionSource = MutableInteractionSource()
                                            )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(
                                                vertical = 10.dp,
                                                horizontal = 10.dp
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = single?.info?.cookie ?: "",
                                                    color = PinkMainVariant,
                                                    fontSize = 15.sp,
                                                    textAlign = TextAlign.Start
                                                )
                                                Text(
                                                    text = "Po.${single?.info?.id}",
                                                    fontSize = 15.sp,
                                                    textAlign = TextAlign.Center
                                                )
                                                Text(
                                                    text = formatter.format(Date(single?.info?.time ?: 1)),
                                                    fontSize = 12.sp,
                                                    color = Unselected,
                                                    textAlign = TextAlign.End
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                single?.info?.content?.MyFormat(
                                                    isNull = single?.info?.images == null,
                                                    scope = scope,
                                                    viewModel = viewModel,
                                                    heightLimit,
                                                    _isOpen,
                                                    navController
                                                )
                                                if (single?.info?.images != null) {
                                                    var state by remember {
                                                        mutableStateOf(true)
                                                    }
                                                    Box(
                                                        modifier = Modifier
                                                            .fillMaxWidth(0.4f),
                                                        contentAlignment = Alignment.TopEnd
                                                    ) {
                                                        if (state) {
                                                            Image(
                                                                bitmap = ImageBitmap.imageResource(
                                                                    id = cn.xd.bog.R.drawable.loading
                                                                ),
                                                                contentDescription = stringResource(
                                                                    id = cn.xd.bog.R.string.loading
                                                                ),
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
                                                                if (single?.info?.images?.get(0)?.url == "image does not exist") {
                                                                    "nodata"
                                                                } else {
                                                                    single?.info?.images?.get(0)?.url
                                                                }
                                                            }${single?.info?.images?.get(0)?.ext ?: ".jpg"}",
                                                            contentDescription = stringResource(
                                                                id = cn.xd.bog.R.string.con_img
                                                            ),
                                                            contentScale = ContentScale.None,
                                                            onState = {
                                                                if (it is AsyncImagePainter.State.Success) {
                                                                    state = false
                                                                }
                                                            },
                                                            modifier = Modifier
                                                                .clip(MaterialTheme.shapes.medium)
                                                                .sizeIn(
                                                                    maxHeight = if (state) {
                                                                        0.dp
                                                                    } else {
                                                                        Dp.Unspecified
                                                                    },
                                                                    maxWidth = if (state) {
                                                                        0.dp
                                                                    } else {
                                                                        Dp.Unspecified
                                                                    }
                                                                )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        'b' -> {
                            string.substring(matchResult.range).run {
                                copy.replace(
                                    this,
                                    "🎲 " + this.substring(2, this.length - 4)
                                )
                            }
                            Text(
                                text = copy,
                                lineHeight = 1.5.em
                            )
                        }
                        'a' -> {

                        }
                    }
                }
            } else {
                Text(
                    text = string,
                    lineHeight = 1.5.em
                )
            }
        }
    }
}