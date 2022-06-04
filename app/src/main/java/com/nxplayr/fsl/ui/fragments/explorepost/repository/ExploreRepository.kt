package com.nxplayr.fsl.ui.fragments.explorepost.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.BannerPojo
import com.nxplayr.fsl.data.model.ExploreVideosPojo
import com.nxplayr.fsl.util.SingleLiveEvent
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ExploreRepository {

    private val TAG: String? = "TEST API"
    private val apiService = RestClient.get()
    val exploreSuccessLiveData = SingleLiveEvent<MutableList<ExploreVideosPojo>>()
    val bannerSuccessLiveData = SingleLiveEvent<MutableList<BannerPojo>>()
    val exploreFailureLiveData = SingleLiveEvent<String>()

    suspend fun getVideos(json: String) {
        try {
            var response = apiService?.showExploreVideos(json)

            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    exploreSuccessLiveData.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    exploreFailureLiveData.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            exploreFailureLiveData.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            exploreFailureLiveData.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            exploreFailureLiveData.postValue(e.message)
        }
    }

    suspend fun getBanners(json: String) {
        try {
            var response = apiService?.showbanners(json)

            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    bannerSuccessLiveData.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    exploreFailureLiveData.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            exploreFailureLiveData.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            exploreFailureLiveData.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            exploreFailureLiveData.postValue(e.message)
        }
    }
}