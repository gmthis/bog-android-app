package cn.xd.bog.util

import cn.xd.bog.controller.RequestParameter
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

private val okHttpClient = OkHttpClient()

fun api(
    apiName: String,
    parameter: RequestParameter? = null,
    isSync: Boolean = false,
    resultBlock: (response: Response?, e: IOException?) -> Unit
){
    request("/api/$apiName", parameter, isSync, resultBlock)
}

fun post(
    apiName: String,
    parameter: RequestParameter? = null,
    isSync: Boolean = false,
    resultBlock: (response: Response?, e: IOException?) -> Unit
){
    request("/post/$apiName", parameter, isSync, resultBlock)
}

fun request(
    apiName: String,
    parameter: RequestParameter? = null,
    isSync: Boolean = false,
    resultBlock: (response: Response?, e: IOException?) -> Unit
){
    val request = Request.Builder()
        .url("http://www.bog.ac$apiName")
        .post(
            FormBody.Builder().also {
                parameter?.toMap()?.forEach { (key, value) ->
                    it.add(key, value)
                }
            }.build()
        )
        .build()
    if (isSync){
        try {
            resultBlock(okHttpClient.newCall(request).execute(), null)
        } catch (e: IOException){
            resultBlock(null, e)
        }

    } else {
        okHttpClient.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                resultBlock(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                resultBlock(response, null)
            }

        })
    }

}

