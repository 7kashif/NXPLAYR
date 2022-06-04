package com.nxplayr.fsl.ui.fragments.collection.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.*
import com.nxplayr.fsl.util.SingleLiveEvent
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class CollectionRepository {

    private val TAG: String? = "TEST API"
    private val apiService = RestClient.get()
    val createAlbumList = SingleLiveEvent<MutableList<CreateAlbumListPojo>>()
    val assignPostAlbumCreated = SingleLiveEvent<MutableList<AssignPostAlbumPojo>>()
    val deleteAlbum = SingleLiveEvent<MutableList<CreateAlbumDeletePojo>>()
    val createAlbum = SingleLiveEvent<MutableList<CreateAlbumPojo>>()
    val editAlbum = SingleLiveEvent<MutableList<EditAlbumDeletePojo>>()
    val failureLiveData = SingleLiveEvent<String>()

    suspend fun createAlbumList(json: String) {
        try {
            val response = apiService?.createAlbumList(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    createAlbumList.postValue(response.body())
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

    suspend fun assignPostAlbum(json: String) {
        try {
            val response = apiService?.assignPostAlbum(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    assignPostAlbumCreated.postValue(response.body())
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

    suspend fun deleteAlbum(json: String) {
        try {
            val response = apiService?.createAlbumDelete(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    deleteAlbum.postValue(response.body())
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

    suspend fun createAlbum(json: String) {
        try {
            val response = apiService?.createAlbum(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    createAlbum.postValue(response.body())
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

    suspend fun editAlbum(json: String) {
        try {
            val response = apiService?.editAlbum(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    editAlbum.postValue(response.body())
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