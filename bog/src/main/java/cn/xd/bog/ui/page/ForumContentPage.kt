package cn.xd.bog.ui.page

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cn.xd.bog.entity.ForumContentInfo
import cn.xd.bog.entity.SingleContent
import cn.xd.bog.entity.SingleContentInfo
import cn.xd.bog.ui.components.ContentCard
import cn.xd.bog.ui.theme.Black
import cn.xd.bog.ui.theme.ContentBackground
import cn.xd.bog.ui.theme.White
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@SuppressLint("UnrememberedMutableState")
@Composable
fun ForumContentPage(
    forumContentInfoList: MutableList<ForumContentInfo>,
    forum: Map<Int, String>,
    fontSize: Int,
    loading: (() -> Unit, () -> Unit) -> Unit,
    refresh: (() -> Unit) -> Unit,
    jump: ((Int) -> Unit)?,
    pullContent: (id: String,
                  container: MutableState<SingleContentInfo?>,
                  error: (Int, String) -> Unit,
                  after: (() -> Unit)?) -> Unit,
    imageDetails: (String, Int) -> Unit,
    lazyListState: LazyListState
) {
    var isLoading by remember {
        mutableStateOf(false)
    }
    var isNotEnd by mutableStateOf(true)
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = false)
    var isRefresh by remember {
        mutableStateOf(false)
    }
    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            if (!isRefresh){
                isRefresh = true
                refresh {
                    isRefresh = false
                }
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .background(if (isSystemInDarkTheme()) Black else ContentBackground)
                .fillMaxHeight(),
            state = lazyListState
        ){
            if (forumContentInfoList.isNotEmpty()){
                itemsIndexed(forumContentInfoList){ index, item ->
                    ContentCard(
                        index = index,
                        content = item,
                        forum = forum,
                        fontSize = fontSize,
                        jump = jump,
                        pullContent = pullContent,
                        details = false,
                        imageDetails = imageDetails
                    )
                    if (forumContentInfoList.size - index < 2 && isNotEnd && forumContentInfoList.size >= 20){
                        isLoading = true
                        LaunchedEffect(forumContentInfoList.size){
                            loading({
                                isLoading = false
                            }){
                                isLoading = false
                                isNotEnd = false
                            }
                        }
                        if (isLoading){
                            ContentCard(
                                index = 0,
                                content = null,
                                forum = forum,
                                fontSize = fontSize,
                                pullContent = pullContent,
                                details = false,
                                imageDetails = imageDetails
                            )
                        }
                    }
                }
            }else{
                item {
                    ContentCard(
                        index = 0,
                        content = null,
                        forum = forum,
                        fontSize = fontSize,
                        pullContent = pullContent,
                        details = false,
                        imageDetails = imageDetails
                    )
                }
            }
        }
    }
}