package com.nxplayr.fsl.ui.fragments.job.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.ApplyJoblist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class ApplyJobListViewModel:ViewModel() {
    var languageresponse: LiveData<List<ApplyJoblist>>? = null
    lateinit var mContext: Context
    var json: String? = null
    var from: String? = null

    fun getNotificationList(
        context: Context,
        json: String, from: String
    ): LiveData<List<ApplyJoblist>> {
        this.json = json
        this.mContext = context
        this.from = from
        languageresponse = getJobListApi()
        return languageresponse!!
    }

    private fun getJobListApi(): LiveData<List<ApplyJoblist>> {
        val data = MutableLiveData<List<ApplyJoblist>>()
        viewModelScope.launch(Dispatchers.IO) {
        var call : Call<List<ApplyJoblist>>?=null
            when(from){

                "Apply"-> {
                    call = RestClient.get()!!.getApplyJobList(json!!)
                }
                "ApplyJob"-> {
                    call = RestClient.get()!!.getApplyJob(json!!)
                }
            }

        call?.enqueue(object : RestCallback<List<ApplyJoblist>>(mContext) {
            override fun Success(response: Response<List<ApplyJoblist>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })}

        return data
    }

}