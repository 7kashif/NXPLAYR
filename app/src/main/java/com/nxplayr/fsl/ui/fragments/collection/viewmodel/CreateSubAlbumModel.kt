package com.nxplayr.fsl.ui.fragments.collection.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CreateAlbumPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class CreateSubAlbumModel: ViewModel() {

    var getAblimResponse: LiveData<List<CreateAlbumPojo>>?=null
    var mContext: Activity?=null
    var isShowing: Boolean = false
    var json: String = ""
    var from=""

    fun apiSubCreateAlbum(context: Activity, isShowing: Boolean,
                       json: String): LiveData<List<CreateAlbumPojo>> {
        this.mContext = context
        this.isShowing = isShowing
        this.json = json

        getAblimResponse = apiSubAlbumResponse()
        return getAblimResponse!!
    }

    private fun apiSubAlbumResponse(): LiveData<List<CreateAlbumPojo>> {
        val data = MutableLiveData<List<CreateAlbumPojo>>()
        viewModelScope.launch(Dispatchers.IO) {
            var call = RestClient.get()!!.createSubAlbum(json)

            call.enqueue(object : RestCallback<List<CreateAlbumPojo>>(mContext) {
                override fun Success(response: Response<List<CreateAlbumPojo>>) {
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