package cn.xd.bogr.net.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.xd.bogr.net.entity.Image
import cn.xd.bogr.net.entity.Reply
import cn.xd.bogr.net.requestStrand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StrandPaging(
    private val id: Int,
    private val images: LinkedHashSet<Image>
): PagingSource<Int, Reply>() {
    override fun getRefreshKey(state: PagingState<Int, Reply>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Reply> = withContext(Dispatchers.IO) {
        try {
            val nextPage = params.key ?: 1
            val strandRst = requestStrand(id, nextPage)
            for (reply in strandRst.info.reply) {
                reply.images?.let { images.addAll(it) }
            }
            LoadResult.Page(
                data = strandRst.info.reply,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (strandRst.info.reply.isEmpty()) null else nextPage + 1
            )
        }catch (e: Exception){
            LoadResult.Error(e)
        }
    }
}