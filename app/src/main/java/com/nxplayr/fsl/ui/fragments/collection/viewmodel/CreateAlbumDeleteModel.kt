package com.nxplayr.fsl.ui.fragments.collection.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CreateAlbumDeletePojo
import retrofit2.Response

class CreateAlbumDeleteModel: ViewModel() {

    var getAblumDeleteResponse: LiveData<List<CreateAlbumDeletePojo>>?=null
    var mContext: Activity?=null
    var isShowing: Boolean = false
    var json: String = ""
    var from=""

    fun apiCreateAlbumDelete(context: Activity, isShowing: Boolean,
                       json: String): LiveData<List<CreateAlbumDeletePojo>> {
        this.mContext = context
        this.isShowing = isShowing
        this.json = json

        getAblumDeleteResponse = apiAlbumDeleteResponse()
        return getAblumDeleteResponse!!
    }

    private fun apiAlbumDeleteResponse(): LiveData<List<CreateAlbumDeletePojo>> {
        val data = MutableLiveData<List<CreateAlbumDeletePojo>>()

        var call = RestClient.get()!!.createAlbumDelete(json)

        call.enqueue(object : RestCallback<List<CreateAlbumDeletePojo>>(mContext) {
            override fun Success(response: Response<List<CreateAlbumDeletePojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }
}