package com.nxplayr.fsl.ui.activity.onboarding.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.FootballTypeListPojo
import retrofit2.Response

class FootballTypeListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<FootballTypeListPojo>>
    lateinit var mContext: Context
    var json: String ?= null


    fun getFootballTypeList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<FootballTypeListPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getFootballTypeListApi()

        return languageresponse
    }

    private fun getFootballTypeListApi(): LiveData<List<FootballTypeListPojo>> {
        val data = MutableLiveData<List<FootballTypeListPojo>>()

        var call = RestClient.get()!!.footballTypeList(json!!)
        call!!.enqueue(object : RestCallback<List<FootballTypeListPojo>>(mContext) {
            override fun Success(response: Response<List<FootballTypeListPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }
}