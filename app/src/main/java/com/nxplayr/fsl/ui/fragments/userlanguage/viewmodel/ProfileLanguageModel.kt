package com.nxplayr.fsl.ui.fragments.userlanguage.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.ProfileLanguagePojo
import retrofit2.Call
import retrofit2.Response

class ProfileLanguageModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<ProfileLanguagePojo>>
    lateinit var mContext: Context
    var json: String ?= null
    var from:String=""

    fun getLanguageList(
            context: Context,
            isShowing: Boolean,
            json: String,from:String
    ): LiveData<List<ProfileLanguagePojo>> {
        this.json = json

        this.mContext = context
        this.from = from

        languageresponse = getLanguageListApi()

        return languageresponse
    }

    private fun getLanguageListApi(): LiveData<List<ProfileLanguagePojo>> {
        val data = MutableLiveData<List<ProfileLanguagePojo>>()

        var call : Call<List<ProfileLanguagePojo>>?=null
       when(from)
       {
           "Add"->{
               call= RestClient.get()!!.userAddLanguageProfile(json!!)

           }
           "Edit"->{
               call= RestClient.get()!!.userEditLanguageProfile(json!!)

           }
           "Delete"->{
               call= RestClient.get()!!.userDeleteLanguageProfile(json!!)

           }
           "List"->{
               call= RestClient.get()!!.userListLanguageProfile(json!!)


           }
       }

        call?.enqueue(object : RestCallback<List<ProfileLanguagePojo>>(mContext) {
            override fun Success(response: Response<List<ProfileLanguagePojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }
}