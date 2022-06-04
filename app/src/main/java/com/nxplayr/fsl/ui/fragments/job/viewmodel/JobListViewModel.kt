package com.nxplayr.fsl.ui.fragments.job.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.JobListPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class JobListViewModel:ViewModel() {
    var languageresponse: LiveData<List<JobListPojo>>? = null
    lateinit var mContext: Context
    var json: String? = null
    var from: String? = null

    fun getNotificationList(
        context: Context,
        json: String, from: String
    ): LiveData<List<JobListPojo>> {
        this.json = json
        this.mContext = context
        this.from = from
        languageresponse = getJobListApi()
        return languageresponse!!
    }

    private fun getJobListApi(): LiveData<List<JobListPojo>> {
        val data = MutableLiveData<List<JobListPojo>>()
        viewModelScope.launch(Dispatchers.IO) {
        var call : Call<List<JobListPojo>>?=null
            when(from){
                "Save"->{
                    call= RestClient.get()!!.getSaveJobList(json!!)
                }
                "SaveJob"->{
                    call= RestClient.get()!!.getSaveJob(json!!)
                }
                "RemoveJob"->{
                    call= RestClient.get()!!.getRemoveSaveJob(json!!)
                }
                "List"->{
                    call= RestClient.get()!!.getJobList(json!!)

                }
            }

        call?.enqueue(object : RestCallback<List<JobListPojo>>(mContext) {
            override fun Success(response: Response<List<JobListPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })}

        return data
    }

}