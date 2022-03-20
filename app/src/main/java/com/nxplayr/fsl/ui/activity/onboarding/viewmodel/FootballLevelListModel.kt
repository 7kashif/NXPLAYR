package com.nxplayr.fsl.ui.activity.onboarding.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.FootballLevelPojo
import retrofit2.Response

class FootballLevelListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<FootballLevelPojo>>
    lateinit var mContext: Context
    var json: String? = null

    fun getFootballLevelList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<FootballLevelPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getFootballLevelListApi()

        return languageresponse
    }

    private fun getFootballLevelListApi(): LiveData<List<FootballLevelPojo>> {
        val data = MutableLiveData<List<FootballLevelPojo>>()

        var call = RestClient.get()!!.getFootballLevelList(json!!)
        call!!.enqueue(object : RestCallback<List<FootballLevelPojo>>(mContext) {
            override fun Success(response: Response<List<FootballLevelPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }

}
