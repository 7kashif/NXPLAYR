package com.nxplayr.fsl.ui.fragments.userfootballleague.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.LeaguesListpOJO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class LanguageListDataModel:ViewModel() {

    lateinit var langauheResponse: LiveData<List<LeaguesListpOJO>>
    lateinit var mContext: Context
    var json: String? = null
    var from: String = ""

    fun getlanguageVList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<LeaguesListpOJO>> {

        this.json = json

        this.mContext = context
        this.from = from

        langauheResponse = getExploreVideoApi()

        return langauheResponse
    }

    private fun getExploreVideoApi(): LiveData<List<LeaguesListpOJO>> {

        val data = MutableLiveData<List<LeaguesListpOJO>>()
        viewModelScope.launch(Dispatchers.IO) {
            val call = RestClient.get()!!.userLanguageList(json!!)

            call.enqueue(object : RestCallback<List<LeaguesListpOJO>>(mContext) {
                override fun Success(response: Response<List<LeaguesListpOJO>>) {
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