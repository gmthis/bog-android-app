package cn.xd.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.paging.*
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import cn.xd.test.entity.Reply
import cn.xd.test.entity.TestData
import cn.xd.test.entity.toReply
import cn.xd.test.network.syncRequest
import cn.xd.test.ui.theme.BogTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MyViewModel: ViewModel()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = MyViewModel()
        val flow = Pager(PagingConfig(pageSize = 1)) {
            SimpleSource()
        }.flow.cachedIn(viewModel.viewModelScope)
        setContent {
            BogTheme {
                val items = flow.collectAsLazyPagingItems()
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ){
                    itemsIndexed(items){ _, item ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Column {
                                Row {
                                    Text(text = item?.cookie.toString())
                                    Text(text = item?.time.toString())
                                }
                                Text(text = item?.content.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}

class SimpleSource: PagingSource<Int, Reply>(){
    override fun getRefreshKey(state: PagingState<Int, Reply>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Reply> {
        val nextPage = params.key ?:1
        val data = Json.decodeFromString<TestData>(syncRequest(
            "/api/threads",
            mapOf(
                "id" to "141412",
                "page" to nextPage.toString(),
                "page_def" to 20.toString(),
                "order" to 0.toString()
            )
        ))
        if (nextPage == 1){
            data.info.reply.add(0, data.info.toReply())
        }

        return LoadResult.Page(
            data = data.info.reply,
            prevKey = if (nextPage == 1) null else nextPage - 1,
            nextKey = if (nextPage < 100) nextPage + 1 else null
        )
    }

}