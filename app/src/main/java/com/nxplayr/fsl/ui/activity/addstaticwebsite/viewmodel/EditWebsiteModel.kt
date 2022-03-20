package com.nxplayr.fsl.ui.activity.addstaticwebsite.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.WebsitePojo
import retrofit2.Response

class EditWebsiteModel:ViewModel() {

    var getWebsiteEditResponse: LiveData<List<WebsitePojo>>?=null
    var mContext: Activity?=null
    var isShowing: Boolean = false
    var json: String = ""
    var from=""

    fun apiEditSubAlbum(context: Activity, isShowing: Boolean,
                       json: String): LiveData<List<WebsitePojo>> {
        this.mContext = context
        this.isShowing = isShowing
        this.json = json

        getWebsiteEditResponse = apiSubAlbumDeleteResponse()
        return getWebsiteEditResponse!!
    }

    private fun apiSubAlbumDeleteResponse(): LiveData<List<WebsitePojo>> {
        val data = MutableLiveData<List<WebsitePojo>>()

        var call = RestClient.get()!!.editWebsiteList(json)

        call.enqueue(object : RestCallback<List<WebsitePojo>>(mContext) {
            override fun Success(response: Response<List<WebsitePojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }
}