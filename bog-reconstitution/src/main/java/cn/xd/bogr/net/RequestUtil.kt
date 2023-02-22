package cn.xd.bogr.net

import okhttp3.*
import java.io.IOException

/**
 * 默认的http客户端
 */
private val client = OkHttpClient()

var host = "192.168.0.125:8080"

/**
 * 所有api的路径
 */
enum class ApiPath(val path: String){
    /**获取所有板块*/
    ForumList("/api/forumlist"),
    /**获取板块内容*/
    Forum("/api/forum"),
    /**获取单一内容*/
    Thread("/api/thread"),
    /**获取主内容及回复*/
    Threads("/api/threads"),
    /**发布内容*/
    Post("/post/post"),
    /**删除内容*/
    Del("/post/del"),
    /**图片相关*/
    Upload("/post/upload"),
    /**搜索*/
    Search(""),
    /**获取饼干*/
    CookieGet("/post/cookieGet"),
    /**导入饼干*/
    CookieAdd("/api/cookieAdd"),
    /**移除饼干*/
    CookieDel("/api/cookiedel"),
    /**备注饼干*/
    Remarks("/api/remarks"),
    /**签到*/
    Sign("/api/sign"),
    /**获取饼干信息*/
    UserInfo("/api/userinfo")
}

/**
 * 进行一次同步请求
 *
 * @param api 请求的api
 * @param parameter 请求参数
 * @throws IOException
 * @return [String] 请求产生的结果
 */
fun syncRequest(
    api: ApiPath,
    parameter: Map<String, String>
): String{
    val request = Request.Builder()
        .url("http://$host${api.path}")
        .post(
            FormBody.Builder().also {
                parameter.forEach { (key, value) ->
                    it.add(key, value)
                }
            }.build()
        )
        .addHeader("Content-Type", "application/json;charset=utf-8")
        .build()
    return client.newCall(request).execute().body!!.string()
}

/**
 * 进行一次异步请求
 *
 * @param api 请求的api
 * @param parameter 请求参数
 * @param result 对请求结果进行处理的回调
 */
fun request(
    api: ApiPath,
    parameter: Map<String, String>,
    result: (response: Response?, e: IOException?) -> Unit
){
    val request = Request.Builder()
        .url("http://$host${api.path}")
        .post(
            FormBody.Builder().also {
                parameter.forEach { (key, value) ->
                    it.add(key, value)
                }
            }.build()
        )
        .build()
    client.newCall(request).enqueue(object: Callback {
        override fun onFailure(call: Call, e: IOException) {
            result(null, e)
        }

        override fun onResponse(call: Call, response: Response) {
            result(response, null)
        }
    })
}