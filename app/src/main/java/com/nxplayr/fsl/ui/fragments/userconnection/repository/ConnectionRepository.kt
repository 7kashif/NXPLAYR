package com.nxplayr.fsl.ui.fragments.userconnection.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.ChatList
import com.nxplayr.fsl.data.model.ConnectionTypePojo
import com.nxplayr.fsl.data.model.SuggestedFreindListPojo
import com.nxplayr.fsl.util.SingleLiveEvent
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ConnectionRepository {

    private val TAG: String? = "TEST API"
    private val apiService = RestClient.get()
    val chatList = SingleLiveEvent<MutableList<ChatList>>()
    val mConnectionTypePojo = SingleLiveEvent<MutableList<ConnectionTypePojo>>()
    val userContactList = SingleLiveEvent<MutableList<SuggestedFreindListPojo>>()
    val failureLiveData = SingleLiveEvent<String>()

    suspend fun userContactList(json: String) {
        try {
            val response = apiService?.userContactList(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    userContactList.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failureLiveData.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failureLiveData.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failureLiveData.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failureLiveData.postValue(e.message)
        }
    }

    suspend fun connectionList(json: String) {
        try {
            val response = apiService?.connectionList(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    mConnectionTypePojo.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failureLiveData.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failureLiveData.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failureLiveData.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failureLiveData.postValue(e.message)
        }
    }

    suspend fun chatList(json: String) {
        try {
            val response = apiService?.chatlist(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    chatList.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failureLiveData.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failureLiveData.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failureLiveData.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failureLiveData.postValue(e.message)
        }
    }
}