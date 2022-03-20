package com.nxplayr.fsl.ui.fragments.postcomment.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.ReasonList
import com.nxplayr.fsl.util.MyUtils

import retrofit2.Response

class ReasonModel : ViewModel() {

    lateinit var userLogin: LiveData<List<ReasonList>?>
    lateinit var mContext: Activity
    var from: Int = -1
    var jsonArray: String = ""
    var progress: Boolean = false

    fun apiFunction(
        context: Activity, progress1 : Boolean,jsonArray: String
    ): LiveData<List<ReasonList>?> {
        this.progress = progress1
        this.jsonArray = jsonArray
        this.mContext = context
        userLogin = apiResponse()
        return userLogin
    }

    private fun apiResponse(): LiveData<List<ReasonList>?> {

        if (progress){
            MyUtils.showProgressDialog(mContext,"Wait..")
        }

        val data = MutableLiveData<List<ReasonList>>()
        var call = RestClient.get()!!.reasonList(jsonArray)

        if (call != null) {
            call.enqueue(object : RestCallback<List<ReasonList>?>(mContext) {


                override fun Success(response: Response<List<ReasonList>?>) {
                    if (progress){
                        MyUtils.dismissProgressDialog()
                    }
                    data.value = response.body()
                }
                override fun failure() {
                    if (progress){
                        MyUtils.dismissProgressDialog()
                    }
                    data.value = null
                }
            })
        }
        return data
    }
}