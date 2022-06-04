package com.nxplayr.fsl.ui.fragments.userwebsite.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.WebsitePojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class WebsiteListModel:ViewModel() {

    lateinit var websiteResponse: LiveData<List<WebsitePojo>>
    lateinit var mContext: Context
    var json: String? = null
    var from: String = ""

    fun getWebsiteList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<WebsitePojo>> {

        this.json = json

        this.mContext = context
        this.from = from

        websiteResponse = getWebsiteListApi()

        return websiteResponse
    }

    private fun getWebsiteListApi(): LiveData<List<WebsitePojo>> {

        val data = MutableLiveData<List<WebsitePojo>>()
        viewModelScope.launch(Dispatchers.IO) {
        val call = RestClient.get()!!.websiteList(json!!)

        call.enqueue(object : RestCallback<List<WebsitePojo>>(mContext){
            override fun Success(response: Response<List<WebsitePojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })}

        return data
    }
}