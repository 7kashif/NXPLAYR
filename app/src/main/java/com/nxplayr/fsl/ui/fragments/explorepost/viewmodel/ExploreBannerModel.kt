package com.nxplayr.fsl.ui.fragments.explorepost.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.BannerPojo
import retrofit2.Response

class ExploreBannerModel : ViewModel() {

    lateinit var bannerresponse: LiveData<List<BannerPojo>>
    lateinit var mContext: Context
    var json: String? = null
    var from: String = ""

    fun getBannerList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<BannerPojo>> {
        this.json = json

        this.mContext = context
        this.from = from

        bannerresponse = getBannerListApi()

        return bannerresponse
    }

    private fun getBannerListApi(): LiveData<List<BannerPojo>> {

        val data = MutableLiveData<List<BannerPojo>>()
        val call = RestClient.get()!!.showbanners(json!!)

        call.enqueue(object :RestCallback<List<BannerPojo>>(mContext){
            override fun Success(response: Response<List<BannerPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }

}