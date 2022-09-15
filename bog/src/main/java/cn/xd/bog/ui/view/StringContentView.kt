package cn.xd.bog.ui.view

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cn.xd.bog.entity.SingleContentInfo
import cn.xd.bog.entity.StringContent
import cn.xd.bog.ui.components.ContentCard
import cn.xd.bog.ui.components.StringContentBottomBar
import cn.xd.bog.ui.components.StringContentTopBar
import cn.xd.bog.ui.theme.Black
import cn.xd.bog.ui.theme.ContentBackground
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun StringContentView(
    stringId: String,
    stringContents: Map<String, StringContent>,
    scaffoldState: ScaffoldState,
    fontSize: Int,
    forum: Map<Int, String>,
    back: () -> Unit,
    refresh: (Int, Int, () -> Unit) -> Unit,
    next: (Int, () -> Unit, (Int, String) -> Unit) -> Unit,
    pullContent: (id: String,
                  container: MutableState<SingleContentInfo?>,
                  error: (Int, String) -> Unit,
                  after: (() -> Unit)?) -> Unit,
    imageDetails: (String, Int) -> Unit,
    jump: (Int) -> Unit,
    sort: (Int) -> Unit,
    lazyListState: LazyListState
) {
    BackHandler(
        onBack = back
    )
    val stringContent = stringContents[stringId]
    Scaffold(
        scaffoldState =  scaffoldState,
        topBar = {
            StringContentTopBar(
                stringId = stringId,
                forum = forum[stringContent?.info?.forum],
                repliesCount = stringContent?.info?.replyCount ?: 0,
                stringContent = stringContent,
                back = back,
                sort = sort
            )
        },
        bottomBar = {
            StringContentBottomBar(fontSize)
        }
    ) {
       Surface(
           modifier = Modifier.padding(it)
       ) {
           if (stringContent?.info?.reply !== null){
               StringContentList(
                   refresh = refresh,
                   next = next,
                   pullContent = pullContent,
                   stringContent = stringContent,
                   fontSize = fontSize,
                   forum = forum,
                   imageDetails = imageDetails,
                   jump = jump,
                   lazyListState = lazyListState
               )
           }else{
               Column(
                   modifier = Modifier
                       .background(if (isSystemInDarkTheme()) Black else ContentBackground)
                       .fillMaxHeight()
               ) {
                   ContentCard(
                       index = 0,
                       content = null,
                       forum = forum,
                       fontSize = fontSize,
                       pullContent = pullContent,
                       details = true,
                       imageDetails = imageDetails,
                       jump = jump
                   )
               }
           }
       }
    }
}

@Composable
fun StringContentList(
    refresh: (Int, Int, () -> Unit) -> Unit,
    next: (Int, () -> Unit, (Int, String) -> Unit) -> Unit,
    pullContent: (id: String,
                  container: MutableState<SingleContentInfo?>,
                  error: (Int, String) -> Unit,
                  after: (() -> Unit)?) -> Unit,
    stringContent: StringContent,
    fontSize: Int,
    forum: Map<Int, String>,
    imageDetails: (String, Int) -> Unit,
    jump: (Int) -> Unit,
    lazyListState: LazyListState
) {
    val state = rememberSwipeRefreshState(isRefreshing = false)
    var isLoading by remember {
        mutableStateOf(false)
    }
    var isRefresh by remember {
        mutableStateOf(false)
    }
    SwipeRefresh(state = state, onRefresh = {
        if (!isRefresh){
            isRefresh = true
            refresh(stringContent.info!!.id, 0){
                isLoading = false
            }
        }
    }) {
        LazyColumn(
            modifier = Modifier
                .background(if (isSystemInDarkTheme()) Black else ContentBackground)
                .fillMaxHeight(),
            state = lazyListState
        ){
            item {
                ContentCard(
                    index = 0,
                    content = stringContent.info,
                    forum = forum,
                    fontSize = fontSize,
                    pullContent = pullContent,
                    details = true,
                    imageDetails = imageDetails,
                    jump = jump,
                    po = stringContent.info
                )
            }
            itemsIndexed(stringContent.info!!.reply){ index, item ->
                if (stringContent.filter){
                    if (item.cookie == stringContent.info.cookie){
                        ContentCard(
                            index = index + 1,
                            content = item,
                            forum = forum,
                            fontSize = fontSize,
                            jump = null,
                            pullContent = pullContent,
                            details = true,
                            imageDetails = imageDetails,
                            po = stringContent.info
                        )
                    }
                }else{
                    ContentCard(
                        index = index + 1,
                        content = item,
                        forum = forum,
                        fontSize = fontSize,
                        jump = null,
                        pullContent = pullContent,
                        details = true,
                        imageDetails = imageDetails,
                        po = stringContent.info
                    )
                }
                if (stringContent.info.reply.size - index < 2 && stringContent.isNotEnd && stringContent.info.reply.size >= 20){
                    isLoading = true
                    LaunchedEffect(stringContent.info.reply.size){
                        next(stringContent.info.id, {
                            isLoading = false
                        }){ int, string ->
                            isLoading = false
                            stringContent.isNotEnd = false
                        }
                    }
                    if (isLoading){
                        ContentCard(
                            index = 0,
                            content = null,
                            forum = forum,
                            fontSize = fontSize,
                            pullContent = pullContent,
                            details = true,
                            imageDetails = imageDetails
                        )
                    }
                }
            }
        }
    }
}