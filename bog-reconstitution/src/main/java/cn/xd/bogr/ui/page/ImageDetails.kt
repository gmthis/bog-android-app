package cn.xd.bogr.ui.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cn.xd.bogr.net.entity.Image
import cn.xd.bogr.ui.view.components.LoadingLargePicture
import cn.xd.bogr.util.rememberViewModel
import cn.xd.bogr.viewmodel.AppStatus
import com.mxalbert.zoomable.Zoomable
import com.mxalbert.zoomable.rememberZoomableState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageDetails(
    images: LinkedHashSet<Image>,
    selected: Int,
    back: () -> Unit,
) {
    val imageList = remember {
        images.toList()
    }
    val state = rememberPagerState(selected)
    Box(modifier = Modifier.fillMaxSize()){
        HorizontalPager(
            pageCount = images.size,
            state = state
        ) {
            val zoomableState = rememberZoomableState(maxScale = 10f)
            Zoomable(
                state = zoomableState,
                modifier = Modifier.fillMaxSize(),
                onTap = {
                    back()
                },
                dismissGestureEnabled = true,
                onDismiss = {
                    back()
                    true
                }
            ) {
                Box {
                    LoadingLargePicture(image = imageList[it])
                }
            }
        }
        Text(
            text = "${state.currentPage + 1}/${images.size}",
            modifier = Modifier.align(Alignment.TopCenter)
                .statusBarsPadding()
        )
    }
}