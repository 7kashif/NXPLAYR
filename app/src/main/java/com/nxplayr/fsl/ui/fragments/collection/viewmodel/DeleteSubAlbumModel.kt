package com.nxplayr.fsl.ui.fragments.collection.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CreateAlbumDeletePojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class DeleteSubAlbumModel:ViewModel() {

    var getSubAblumDeleteResponse: LiveData<List<CreateAlbumDeletePojo>>?=null
    var mContext: Activity?=null
    var isShowing: Boolean = false
    var json: String = ""
    var from=""

    fun apiDeleteSubAlbum(context: Activity, isShowing: Boolean,
                             json: String): LiveData<List<CreateAlbumDeletePojo>> {
        this.mContext = context
        this.isShowing = isShowing
        this.json = json

        getSubAblumDeleteResponse = apiSubAlbumDeleteResponse()
        return getSubAblumDeleteResponse!!
    }

    private fun apiSubAlbumDeleteResponse(): LiveData<List<CreateAlbumDeletePojo>> {
        val data = MutableLiveData<List<CreateAlbumDeletePojo>>()
        viewModelScope.launch(Dispatchers.IO) {
            var call = RestClient.get()!!.deleteSubAlbum(json)

            call.enqueue(object : RestCallback<List<CreateAlbumDeletePojo>>(mContext) {
                override fun Success(response: Response<List<CreateAlbumDeletePojo>>) {
                    data.value = response.body()
                }

                override fun failure() {
                    data.value = null
                }

            })
        }

        return data
    }
}