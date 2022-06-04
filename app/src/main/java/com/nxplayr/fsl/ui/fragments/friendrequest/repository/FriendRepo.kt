package com.nxplayr.fsl.ui.fragments.friendrequest.repository

import android.util.Log
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.FriendListPojo
import com.nxplayr.fsl.util.SingleLiveEvent
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class FriendRepo {

    private var response: Response<MutableList<FriendListPojo>>? = null
    private val TAG: String = "TEST API"
    private val apiService = RestClient.get()
    val successFriend = SingleLiveEvent<MutableList<FriendListPojo>>()
    val failure = SingleLiveEvent<String>()

    suspend fun friendApi(json: String, from: String) {
        try {
            when (from) {
                "friend_list" -> {
                    response = apiService?.friendList(json)
                }
                "change_friendList" -> {
                    response = apiService?.changeConnection(json)
                }
                "chatTofriend" -> {
                    response = apiService?.chatTofriend(json)
                }
            }
            Log.d(TAG, "$response")
            if (response != null) {
                if (response!!.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response!!.body()}")
                    successFriend.postValue(response!!.body())
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