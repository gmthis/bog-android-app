package cn.xd.bog.controller.util

import okhttp3.*
import java.io.IOException

private val okHttpClient = OkHttpClient()

fun syncRequest(
    apiName: String,
    parameter: Map<String, String>
): String{
    val request = Request.Builder()
        .url("http://www.bog.ac$apiName")
        .post(
            FormBody.Builder().also {
                parameter.forEach { (key, value) ->
                    it.add(key, value)
                }
            }.build()
        )
        .build()
    return try {
        okHttpClient.newCall(request).execute().body!!.string()
    } catch (e: IOException){
        "TimeOut"
    }
}

fun request(
    apiName: String,
    parameter: Map<String, String>,
    resultBlock: (response: Response?, e: IOException?) -> Unit
){
    val request = Request.Builder()
        .url("http://www.bog.ac$apiName")
        .post(
            FormBody.Builder().also {
                parameter.forEach { (key, value) ->
                    it.add(key, value)
                }
            }.build()
        )
        .build()
    okHttpClient.newCall(request).enqueue(object: Callback{
        override fun onFailure(call: Call, e: IOException) {
            resultBlock(null, e)
        }

        override fun onResponse(call: Call, response: Response) {
            resultBlock(response, null)
        }
    })
}

