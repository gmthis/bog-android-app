package cn.xd.bog.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import cn.xd.bog.R
import cn.xd.bog.data.viewmodel.MainViewModel
import cn.xd.bog.entity.ContentInfo
import cn.xd.bog.entity.Reply
import cn.xd.bog.ui.theme.PinkMainVariant
import cn.xd.bog.ui.theme.Unselected
import cn.xd.bog.util.MyFormat
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import java.util.*

@Composable
fun Details(
    viewModel: MainViewModel,
    contentId: Int?,
    navController: NavHostController,
    scope: CoroutineScope
) {
    var stateStacking = remember { 0 }
    val stateStack: MutableList<LazyListState> = remember {
        mutableStateListOf()
    }
    val contentStack: MutableList<List<Reply>> = remember {
        mutableStateListOf()
    }

    BackHandler {
        viewModel.contentPageIndex = 1
        navController.popBackStack()
    }
    val lazyListState = LazyListState()
    LaunchedEffect(Unit){
        viewModel.pullNewContent(id = contentId ?: 0, 1, 20, 0, lazyListState)
    }
    Column {
        Row {
            Image(
                imageVector = ImageVector.vectorResource(
                    id = R.drawable.arrow_back),
                contentDescription = stringResource(id = R.string.go_back)
            )
            Column {
                Text(text = "Po.${contentId}")
                Text(text = "${viewModel.plateMap[viewModel.content.value?.forum]?.name ?: ""}·${viewModel.content.value?.replyCount ?: ""}")
            }
        }
        val swipeRefreshState = rememberSwipeRefreshState(viewModel.isNewLoading.value)

        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                swipeRefreshState.isRefreshing = true
                viewModel.pullNewContent(
                    contentId ?: 0,
                    1,
                    swipeRefreshState = swipeRefreshState,
                    lazyListState = lazyListState
                )
            }
        ){
            LazyColumn(
                state = lazyListState
            ){
                itemsIndexed(viewModel.contentReplay){ index, item ->
                    Card(
                        modifier = (if (index == 0){
                            Modifier.padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 5.dp)
                        }else{
                            Modifier.padding(vertical = 5.dp, horizontal = 10.dp)
                        }).clickable(
                            onClick = {

                            },
                            indication = null,
                            interactionSource = MutableInteractionSource()
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ){
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
                            Spacer(modifier = Modifier.height(4.dp))
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                var isOpen = remember {
                                    mutableStateOf(false)
                                }
                                item.content.MyFormat(
                                    true,
                                    scope,
                                    viewModel,
                                    null,
                                    isOpen,
                                    navController
                                )
                                if (item.images != null){
                                    FlowRow {
                                        for (image in item.images){
                                            var state by remember {
                                                mutableStateOf(true)
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .fillParentMaxWidth(0.4f)
                                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                                            ) {
                                                if (state) {
                                                    Image(
                                                        bitmap = ImageBitmap.imageResource(
                                                            id = R.drawable.loading),
                                                        contentDescription = stringResource(id = R.string.loading),
                                                        modifier = Modifier
                                                            .fillMaxSize()
                                                            .placeholder(
                                                                visible = true,
                                                                color = Unselected,
                                                                shape = MaterialTheme.shapes.medium,
                                                                highlight = PlaceholderHighlight.shimmer(
                                                                    Color.White
                                                                )
                                                            )
                                                    )
                                                }
                                                AsyncImage(
                                                    model = "http://www.bog.ac/image/thumb/${if (image.url == "image does not exist"){"nodata"} else {image.url}}${image.ext ?: ".jpg"}",
                                                    contentDescription = stringResource(
                                                        id = R.string.con_img),
                                                    contentScale = ContentScale.None,
                                                    onState = {
                                                        if (it is AsyncImagePainter.State.Success){
                                                            state = false
                                                        }
                                                    },
                                                    modifier = Modifier.clip(MaterialTheme.shapes.medium)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (viewModel.contentReplay.size - index < 2){
                        LaunchedEffect(Unit){
                            viewModel.pullContent()
                        }
                        if (viewModel.isBottomLoading.value){
                            if (viewModel.isBottomLoading.value){
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
                }
            }
        }
        Row {

        }
    }
}
