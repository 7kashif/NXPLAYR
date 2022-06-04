package com.nxplayr.fsl.ui.fragments.feed.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.PostCreatePojo
import com.nxplayr.fsl.util.SingleLiveEvent
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class FeedRepository {

    private val TAG: String? = "TEST API"
    private val apiService = RestClient.get()
    val postSuccessLiveData = SingleLiveEvent<MutableList<PostCreatePojo>>()
    val postFailureLiveData = SingleLiveEvent<String>()

    suspend fun getPostList(json: String, from: String) {
        try {
            var response: Response<MutableList<PostCreatePojo>>? = null
            when (from) {
                "getPostList" -> {
                    response = apiService?.getPostList(json)
                }
                "editPost" -> {
                    response = apiService?.editPost(json)
                }
                "postShare" -> {
                    response = apiService?.sharePost(json)
                }
                else -> {
                    response = apiService?.createPost(json)
                }
            }

            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    postSuccessLiveData.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    postFailureLiveData.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            postFailureLiveData.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            postFailureLiveData.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            postFailureLiveData.postValue(e.message)
        }
    }
}