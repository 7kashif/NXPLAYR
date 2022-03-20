package com.nxplayr.fsl.ui.fragments.postcomment.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.ReportReasonPojo
import retrofit2.Call
import retrofit2.Response

class ReportReasonsModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<ReportReasonPojo>>
    lateinit var mContext: Context
    var json: String? = null
    var from: String = ""

    fun getReportReasonsList(
            context: Context,
            isShowing: Boolean,
            json: String, from: String
    ): LiveData<List<ReportReasonPojo>> {
        this.json = json

        this.mContext = context
        this.from = from

        languageresponse = getReportReasonsListApi()

        return languageresponse
    }

    private fun getReportReasonsListApi(): LiveData<List<ReportReasonPojo>> {
        val data = MutableLiveData<List<ReportReasonPojo>>()

        var call: Call<List<ReportReasonPojo>>? = null
        when (from) {
            "ReportList" -> {
                call = RestClient.get()!!.getreportReasonList(json!!)

            }
        }

        call?.enqueue(object : RestCallback<List<ReportReasonPojo>>(mContext) {
            override fun Success(response: Response<List<ReportReasonPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }
}