package cn.xd.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.xd.test.entity.TestData
import cn.xd.test.network.syncRequest
import cn.xd.test.ui.theme.BogTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BogTheme {

            }
        }
    }
}

//class SimpleSource: PagingSource<Int, TestData>(){
//    override fun getRefreshKey(state: PagingState<Int, TestData>): Int? {
//
//    }
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TestData> {
//        val nextPage = params.key ?:1
//        val data = Json.decodeFromString<TestData>(syncRequest(
//            "/api/threads",
//            mapOf(
//                "id" to "141412",
//                "page" to nextPage.toString(),
//                "page_def" to 20.toString(),
//                "order" to 0.toString()
//            )
//        ))
//        data.info.reply = data.info.reply.toMutableStateList()
//
//        return LoadResult.Page(
//            data = data
//        )
//    }
//
//}