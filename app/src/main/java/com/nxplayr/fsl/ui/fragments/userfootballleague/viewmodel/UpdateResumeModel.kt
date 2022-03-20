package com.nxplayr.fsl.ui.fragments.userfootballleague.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.SignupPojo
import retrofit2.Response

class UpdateResumeModel:ViewModel() {

    lateinit var langauheResponse: LiveData<List<SignupPojo>>
    lateinit var mContext: Context
    var json: String? = null
    var from: String = ""

    fun getUpateResumeList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<SignupPojo>> {

        this.json = json

        this.mContext = context
        this.from = from

        langauheResponse = getUpdateResumeApi()

        return langauheResponse
    }

    private fun getUpdateResumeApi(): LiveData<List<SignupPojo>> {

        val data = MutableLiveData<List<SignupPojo>>()
        val call = RestClient.get()!!.userUpdateResumePost(json!!)

        call.enqueue(object : RestCallback<List<SignupPojo>>(mContext){
            override fun Success(response: Response<List<SignupPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }
}