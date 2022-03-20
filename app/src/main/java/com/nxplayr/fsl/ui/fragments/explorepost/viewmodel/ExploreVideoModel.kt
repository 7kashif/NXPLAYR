package com.nxplayr.fsl.ui.fragments.explorepost.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.ExploreVideosPojo
import retrofit2.Response

class ExploreVideoModel : ViewModel() {

    lateinit var exploreVideoResponse: LiveData<List<ExploreVideosPojo>>
    lateinit var mContext: Context
    var json: String? = null
    var from: String = ""

    fun getExploreVList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<ExploreVideosPojo>>{

        this.json = json

        this.mContext = context
        this.from = from

        exploreVideoResponse = getExploreVideoApi()

        return exploreVideoResponse
    }

    private fun getExploreVideoApi(): LiveData<List<ExploreVideosPojo>> {

        val data = MutableLiveData<List<ExploreVideosPojo>>()
        val call = RestClient.get()!!.showExploreVideos(json!!)

        call.enqueue(object : RestCallback<List<ExploreVideosPojo>>(mContext){
            override fun Success(response: Response<List<ExploreVideosPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }
}