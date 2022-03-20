package com.nxplayr.fsl.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.JobFunctionPojo
import retrofit2.Response

class JobFunctionListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<JobFunctionPojo>>
    lateinit var mContext: Context
    var json: String? = null

    fun getJobFunctionList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<JobFunctionPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getJobFunctionListApi()

        return languageresponse
    }

    private fun getJobFunctionListApi(): LiveData<List<JobFunctionPojo>> {
        val data = MutableLiveData<List<JobFunctionPojo>>()

        var call = RestClient.get()!!.userJobFunctionListProfile(json!!)
        call!!.enqueue(object : RestCallback<List<JobFunctionPojo>>(mContext) {
            override fun Success(response: Response<List<JobFunctionPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }

}
