package com.nxplayr.fsl.ui.activity.onboarding.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.FootballAgeGroupListPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class FootballAgeGroupModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<FootballAgeGroupListPojo>>
    lateinit var mContext: Context
    var json: String ?=null

    fun getFootballAgeGroupList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<FootballAgeGroupListPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getFootballAgeGroupListApi()

        return languageresponse
    }

    private fun getFootballAgeGroupListApi(): LiveData<List<FootballAgeGroupListPojo>> {
        val data = MutableLiveData<List<FootballAgeGroupListPojo>>()

        viewModelScope.launch(Dispatchers.IO) {
            val call = RestClient.get()!!.ageGroupList(json!!)

            call.enqueue(object : RestCallback<List<FootballAgeGroupListPojo>>(mContext) {
                override fun Success(response: Response<List<FootballAgeGroupListPojo>>) {
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