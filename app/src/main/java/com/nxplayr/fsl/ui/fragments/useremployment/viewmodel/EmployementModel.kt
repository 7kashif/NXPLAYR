package com.nxplayr.fsl.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.Employmentpojo
import retrofit2.Call
import retrofit2.Response

class EmployementModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<Employmentpojo>>
    lateinit var mContext: Context
    var json: String ?= null
    var from:String=""

    fun getEmployement(
            context: Context,
            isShowing: Boolean,
            json: String,from:String
    ): LiveData<List<Employmentpojo>> {
        this.json = json

        this.mContext = context
        this.from = from

        languageresponse = getEmployementApi()

        return languageresponse
    }

    private fun getEmployementApi(): LiveData<List<Employmentpojo>> {
        val data = MutableLiveData<List<Employmentpojo>>()

        var call : Call<List<Employmentpojo>>?=null
       when(from)
       {
           "Add"->{
               call= RestClient.get()!!.userAddEmployementProfile(json!!)

           }
           "Edit"->{
               call= RestClient.get()!!.userEditEmployementProfile(json!!)

           }
           "Delete"->{
               call= RestClient.get()!!.userDeleteEmployementProfile(json!!)

           }
           "List"->{
               call= RestClient.get()!!.userListEmployementProfile(json!!)


           }
       }

        call?.enqueue(object : RestCallback<List<Employmentpojo>>(mContext) {
            override fun Success(response: Response<List<Employmentpojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }
}