package com.nxplayr.fsl.ui.fragments.usereducation.repository

import android.util.Log
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.EducationPojo
import com.nxplayr.fsl.util.SingleLiveEvent
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class EducationRepo {

    private var response: Response<MutableList<EducationPojo>>? = null
    private val TAG: String = "TEST API"
    private val apiService = RestClient.get()
    val successEducation = SingleLiveEvent<MutableList<EducationPojo>>()
    val failure = SingleLiveEvent<String>()

    suspend fun educationApi(json: String, from: String) {
        try {
            when (from) {
                "Add" -> {
                    response = apiService?.userAddEducationProfile(json)
                }
                "Edit" -> {
                    response = apiService?.userEditEducationProfile(json)
                }
                "Delete" -> {
                    response = apiService?.userDeleteEducationProfile(json)
                }
                "List" -> {
                    response = apiService?.userListEducationProfile(json)
                }
            }
            Log.d(TAG, "$response")
            if (response != null) {
                if (response!!.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response!!.body()}")
                    successEducation.postValue(response!!.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response!!.body()}")
                    failure.postValue(response!!.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }
}