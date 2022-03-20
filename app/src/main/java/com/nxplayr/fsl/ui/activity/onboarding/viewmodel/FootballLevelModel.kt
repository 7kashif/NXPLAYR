package com.nxplayr.fsl.ui.activity.onboarding.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.FootballLevelListPojo
import retrofit2.Response

class FootballLevelModel :ViewModel() {

    lateinit var languageresponse: LiveData<List<FootballLevelListPojo>>
    lateinit var mContext: Context
    var json: String ?=null

    fun getFootballLevelList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<FootballLevelListPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getFootballLevelListApi()

        return languageresponse
    }

    private fun getFootballLevelListApi(): LiveData<List<FootballLevelListPojo>> {
        val data = MutableLiveData<List<FootballLevelListPojo>>()

        var call = RestClient.get()!!.footballLevelList(json!!)
        call!!.enqueue(object : RestCallback<List<FootballLevelListPojo>>(mContext) {
            override fun Success(response: Response<List<FootballLevelListPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }

}