package com.nxplayr.fsl.ui.fragments.userlanguage.repository

import android.util.Log
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.ProfileLanguagePojo
import com.nxplayr.fsl.util.SingleLiveEvent
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ProfileRepository {

    private var response: Response<MutableList<ProfileLanguagePojo>>? = null
    private val TAG: String = "TEST API"
    private val apiService = RestClient.get()
    val successProfile = SingleLiveEvent<MutableList<ProfileLanguagePojo>>()
    val failure = SingleLiveEvent<String>()

    suspend fun profileApi(json: String, from: String) {
        try {
            when (from) {
                "Add" -> {
                    response = apiService?.userAddLanguageProfile(json)
                }
                "Edit" -> {
                    response = apiService?.userEditLanguageProfile(json)
                }
                "Delete" -> {
                    response = apiService?.userDeleteLanguageProfile(json)
                }
                "List" -> {
                    response = apiService?.userListLanguageProfile(json)
                }
            }
            Log.d(TAG, "$response")
            if (response != null) {
                if (response!!.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response!!.body()}")
                    successProfile.postValue(response!!.body())
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