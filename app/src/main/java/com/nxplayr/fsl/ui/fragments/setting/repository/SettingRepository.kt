package com.nxplayr.fsl.ui.fragments.setting.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.LanguageLabelPojo
import com.nxplayr.fsl.util.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class SettingRepository {

    private val TAG: String? = "TEST API"
    private val apiService = RestClient.get()
    val usersSuccessLiveData = SingleLiveEvent<MutableList<LanguageLabelPojo>>()
    val usersFailureLiveData = SingleLiveEvent<String>()

    suspend fun getLabels(json: String) {
        withContext(Dispatchers.IO) {
            try {
                val response = apiService?.languageLabel(json)
                Log.d(TAG, "$response")
                if (response != null) {
                    if (response.isSuccessful) {
                        Log.d(TAG, "SUCCESS")
                        Log.d(TAG, "${response.body()}")
                        usersSuccessLiveData.postValue(response.body())
                    } else {
                        Log.d(TAG, "FAILURE")
                        Log.d(TAG, "${response.body()}")
                        usersFailureLiveData.postValue(response.message())
                    }
                }
            } catch (e: UnknownHostException) {
                e.message?.let { Log.e(TAG, it) }
                usersFailureLiveData.postValue(e.message)
            } catch (e: SocketTimeoutException) {
                e.message?.let { Log.e(TAG, it) }
                usersFailureLiveData.postValue(e.message)
            } catch (e: Exception) {
                e.message?.let { Log.e(TAG, it) }
                usersFailureLiveData.postValue(e.message)
            }
        }
    }
}