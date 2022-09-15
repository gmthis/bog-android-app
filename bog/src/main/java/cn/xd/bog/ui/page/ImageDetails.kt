package cn.xd.bog.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import cn.xd.bog.R
import cn.xd.bog.entity.Image
import cn.xd.bog.ui.theme.Black
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.ImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.mxalbert.zoomable.Zoomable
import com.mxalbert.zoomable.rememberZoomableState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun ImageDetails(
    images: List<Image>,
    selected: Int,
    back: () -> Unit
) {
    val pagerState = rememberPagerState()
    HorizontalPager(
        count = images.size,
        state = pagerState,
        modifier = Modifier.pointerInput(Unit){
            detectVerticalDragGestures (
                onDragEnd = {
                    back()
                },
                onVerticalDrag = { _, _ ->

                }
            )
        }
    ) { index ->
        LaunchedEffect(Unit){
            launch {
                pagerState.scrollToPage(selected)
            }
        }
        val state = rememberZoomableState(
            maxScale = 5f
        )
        Zoomable(
            state = state,
            modifier = Modifier
                .fillMaxSize(),
            onTap = {
                back()
            }
        ) {
            var isLoading by remember {
                mutableStateOf(true)
            }
            if (isLoading) {
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data("http://www.bog.ac/image/thumb/${
                            if (images[index].url == "image does not exist") {
                                "nodata.jpg"
                            } else {
                                images[index].url + images[index].ext
                            }
                        }")
                        .size(Size.ORIGINAL)
                        .build()
                )
                if (painter.state is AsyncImagePainter.State.Success){
                    Image(
                        painter = painter,
                        contentDescription = stringResource(id = R.string.con_img),
                        modifier = Modifier
                            .aspectRatio(painter.intrinsicSize.width / painter.intrinsicSize.height)
                            .fillMaxSize()
                    )
                }

            }
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("http://www.bog.ac/image/large/${
                        if (images[index].url == "image does not exist") {
                            "nodata.jpg"
                        } else {
                            images[index].url + images[index].ext
                        }
                    }")
                    .size(Size.ORIGINAL)
                    .build(),
                onSuccess = {
                    isLoading = false
                }
            )
            if (painter.state is AsyncImagePainter.State.Success){
                Image(
                    painter = painter,
                    contentDescription = stringResource(id = R.string.con_img),
                    modifier = Modifier
                        .aspectRatio(painter.intrinsicSize.width / painter.intrinsicSize.height)
                        .fillMaxSize()
                )
            }
        }
    }

}