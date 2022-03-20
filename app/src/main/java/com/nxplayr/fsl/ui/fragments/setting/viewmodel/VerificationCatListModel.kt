package com.nxplayr.fsl.ui.fragments.setting.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.VerificationCatPojo
import retrofit2.Response

class VerificationCatListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<VerificationCatPojo>>
    lateinit var mContext: Context
    var json: String?=null

    fun VerificationCatList(
            context: Context,
            json: String
    ): LiveData<List<VerificationCatPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = geVerificationCatListApi()

        return languageresponse
    }

    private fun geVerificationCatListApi(): LiveData<List<VerificationCatPojo>> {
        val data = MutableLiveData<List<VerificationCatPojo>>()

        var call = RestClient.get()!!.getVerificationCategory(json!!)
        call!!.enqueue(object : RestCallback<List<VerificationCatPojo>>(mContext) {
            override fun Success(response: Response<List<VerificationCatPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }

}