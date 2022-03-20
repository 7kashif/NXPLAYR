package com.nxplayr.fsl.ui.fragments.collection.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.EditAlbumDeletePojo
import retrofit2.Response

class EditSubAlbumModel: ViewModel() {

    var getEditSubAlbumResponse: LiveData<List<EditAlbumDeletePojo>>?=null
    var mContext: Activity?=null
    var isShowing: Boolean = false
    var json: String = ""
    var from=""

    fun apiSubEditAlbumDelete(context: Activity, isShowing: Boolean,
                           json: String): LiveData<List<EditAlbumDeletePojo>> {
        this.mContext = context
        this.isShowing = isShowing
        this.json = json

        getEditSubAlbumResponse = apiEditSubAlbumResponse()
        return getEditSubAlbumResponse!!
    }

    private fun apiEditSubAlbumResponse(): LiveData<List<EditAlbumDeletePojo>> {
        val data = MutableLiveData<List<EditAlbumDeletePojo>>()

        var call = RestClient.get()!!.editSubAlbum(json)

        call.enqueue(object : RestCallback<List<EditAlbumDeletePojo>>(mContext) {
            override fun Success(response: Response<List<EditAlbumDeletePojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }
}