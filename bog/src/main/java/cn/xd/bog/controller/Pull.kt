package cn.xd.bog.controller

import androidx.compose.runtime.MutableState
import cn.xd.bog.entity.*

interface Pull {
    fun pullForum(
        forum: MutableState<Forum?>,
        after: (() -> Unit)? = null
    )

    fun pullForumContent(
        forumContent: MutableState<ForumContent?>,
        id: Int,
        after: (() -> Unit)? = null
    )

    fun pullForumContent(
        forumContentInfoList: MutableList<ForumContentInfo>,
        id: Int,
        page: Int,
        error: ((Int, String) -> Unit),
        after: (() -> Unit)
    )

    fun pullStringContent(
        stringContentMap: MutableMap<String, StringContent>,
        stringId: Int,
        page: Int,
        itemNum: Int,
        order: Int,
        after: (() -> Unit)?
    )

    fun pullStringContentNextPage(
        stringId: Int,
        page: Int,
        itemNum: Int,
        order: Int,
        block: (StringContent) -> Unit,
        error: (Int, String) -> Unit,
        after: (() -> Unit)?
    )

    fun pullSingleContent(
        id: String,
        block: (SingleContent) -> Unit,
        error: (Int, String) -> Unit,
        after: (() -> Unit)?
    )
}