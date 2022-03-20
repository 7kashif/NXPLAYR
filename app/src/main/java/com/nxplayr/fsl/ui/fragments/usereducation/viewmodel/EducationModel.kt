package com.nxplayr.fsl.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.EducationPojo
import retrofit2.Call
import retrofit2.Response

class EducationModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<EducationPojo>>
    lateinit var mContext: Context
    var json: String ?= null
    var from:String=""

    fun getEducation(
            context: Context,
            isShowing: Boolean,
            json: String,from:String
    ): LiveData<List<EducationPojo>> {
        this.json = json

        this.mContext = context
        this.from = from

        languageresponse = getEducationApi()

        return languageresponse
    }

    private fun getEducationApi(): LiveData<List<EducationPojo>> {
        val data = MutableLiveData<List<EducationPojo>>()

        var call : Call<List<EducationPojo>>?=null
       when(from)
       {
           "Add"->{
               call= RestClient.get()!!.userAddEducationProfile(json!!)

           }
           "Edit"->{
               call= RestClient.get()!!.userEditEducationProfile(json!!)

           }
           "Delete"->{
               call= RestClient.get()!!.userDeleteEducationProfile(json!!)

           }
           "List"->{
               call= RestClient.get()!!.userListEducationProfile(json!!)


           }
       }

        call?.enqueue(object : RestCallback<List<EducationPojo>>(mContext) {
            override fun Success(response: Response<List<EducationPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }
}