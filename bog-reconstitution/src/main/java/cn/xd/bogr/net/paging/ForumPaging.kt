package cn.xd.bogr.net.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.xd.bogr.net.entity.Strand
import cn.xd.bogr.net.requestForum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ForumPaging(
    private val id: Int
): PagingSource<Int, Strand>() {
    override fun getRefreshKey(state: PagingState<Int, Strand>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Strand> = withContext(Dispatchers.IO) {
        return@withContext try {
            val nextPage = params.key ?: 1
            val forumRst = requestForum(id, nextPage)
            LoadResult.Page(
                data = forumRst.info,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (forumRst.info.isEmpty()) null else nextPage + 1
            )
        }catch (e: Exception){
            LoadResult.Error(e)
        }
    }
}