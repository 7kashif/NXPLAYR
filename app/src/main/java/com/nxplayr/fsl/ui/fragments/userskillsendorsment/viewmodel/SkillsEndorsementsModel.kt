package com.nxplayr.fsl.ui.fragments.userskillsendorsment.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.SkillsPojo
import retrofit2.Call
import retrofit2.Response

class SkillsEndorsementsModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<SkillsPojo>>
    lateinit var mContext: Context
    var json: String ?= null
    var from:String=""

    fun getSkillsList(
            context: Context,
            isShowing: Boolean,
            json: String,from:String
    ): LiveData<List<SkillsPojo>> {
        this.json = json

        this.mContext = context
        this.from = from

        languageresponse = getSkillsListApi()

        return languageresponse
    }

    private fun getSkillsListApi(): LiveData<List<SkillsPojo>> {
        val data = MutableLiveData<List<SkillsPojo>>()

        var call : Call<List<SkillsPojo>>?=null
       when(from)
       {
           "Add"->{
               call= RestClient.get()!!.userAddSkillProfile(json!!)

           }
           "Edit"->{
               call= RestClient.get()!!.userEditSkillProfile(json!!)

           }
           "Delete"->{
               call= RestClient.get()!!.userDeleteSkillProfile(json!!)

           }
           "List"->{
               call= RestClient.get()!!.userSkillProfile(json!!)

           }
       }

        call?.enqueue(object : RestCallback<List<SkillsPojo>>(mContext) {
            override fun Success(response: Response<List<SkillsPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }
}