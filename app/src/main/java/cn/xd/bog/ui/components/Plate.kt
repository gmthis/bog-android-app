package cn.xd.bog.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import cn.xd.bog.R
import cn.xd.bog.data.viewmodel.MainViewModel
import cn.xd.bog.ui.theme.*
import cn.xd.bog.util.MyFormat
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

val formatter =
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

@Composable
fun Plate(
    viewModel: MainViewModel,
    navController: NavHostController,
    lazyListState: LazyListState,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    var swipeRefreshState = rememberSwipeRefreshState(viewModel.isNewLoading.value)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            swipeRefreshState.isRefreshing = true
            viewModel.pullNewPlateContent(
                viewModel.plateID.value,
                1,
                swipeRefreshState = swipeRefreshState
            )
        }
    ) {
        LazyColumn(
            state = lazyListState
        ) {
            itemsIndexed(viewModel.plateContentList) { index, item ->
                Card(
                    modifier = (if (index == 0) {
                        Modifier.padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 5.dp)
                    } else {
                        Modifier.padding(vertical = 5.dp, horizontal = 10.dp)
                    }).clickable(
                        onClick = {
                            navController.navigate("details/${item.id}")
                        },
                        indication = null,
                        interactionSource = MutableInteractionSource()
                    ).animateContentSize()
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.cookie,
                                color = PinkMainVariant,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Start
                            )
                            Text(
                                text = "Po.${item.id}",
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = formatter.format(Date(item.time)),
                                fontSize = 12.sp,
                                color = Unselected,
                                textAlign = TextAlign.End
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Bubble)
                                    .padding(vertical = 2.dp, horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(
                                        id = R.drawable.chat_bubble
                                    ),
                                    contentDescription = stringResource(
                                        id = R.string.rep_num
                                    ),
                                    tint = onBubble,
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = item.replyCount.toString(),
                                    textAlign = TextAlign.End,
                                    fontSize = 12.sp,
                                    color = onBubble,
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = viewModel.plateMap[item.forum]?.name ?: "null",
                                textAlign = TextAlign.End,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Bubble)
                                    .padding(vertical = 2.dp, horizontal = 6.dp),
                                color = onBubble
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        val heightLimit = remember {
                            mutableStateOf(1)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = heightLimit.value * 210.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val isOpen = remember {
                                mutableStateOf(false)
                            }
                            item.content.MyFormat(
                                item.images == null,
                                scope,
                                viewModel,
                                heightLimit,
                                isOpen,
                                navController
                            )
                            if (item.images != null) {
                                var state by remember {
                                    mutableStateOf(true)
                                }
                                Box(
                                    modifier = Modifier
                                        .animateContentSize()
                                        .fillParentMaxWidth(if (isOpen.value){
                                            0f
                                        } else {
                                            0.4f
                                        }),
                                    contentAlignment = Alignment.TopEnd
                                ) {
                                    if (state) {
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
                                            if (item.images[0].url == "image does not exist") {
                                                "nodata"
                                            } else {
                                                item.images[0].url
                                            }
                                        }${item.images[0].ext ?: ".jpg"}",
                                        contentDescription = stringResource(
                                            id = R.string.con_img
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
                if (viewModel.plateContentList.size - index < 2) {
                    LaunchedEffect(Unit) {
                        viewModel.pullPlateContent()
                    }
                    if (viewModel.isBottomLoading.value) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .wrapContentHeight(Alignment.CenterVertically)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                                .size(24.dp),
                            color = PinkMainVariant,
                            strokeWidth = 3.dp,
                        )
                    }
                }
            }
            if (viewModel.noMore.value) {
                item {
                    val noMore = stringResource(
                        id = R.string.no_more
                    )
                    Card(
                        modifier = Modifier
                            .padding(start = 10.dp, top = 5.dp, end = 10.dp, bottom = 10.dp)
                            .clickable(
                                onClick = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            noMore,
                                            null,
                                            SnackbarDuration.Short
                                        )
                                    }
                                },
                                indication = null,
                                interactionSource = MutableInteractionSource()
                            )
                            .fillParentMaxWidth()
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string.no_more_text),
                            lineHeight = 1.5.em,
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .padding(vertical = 10.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}