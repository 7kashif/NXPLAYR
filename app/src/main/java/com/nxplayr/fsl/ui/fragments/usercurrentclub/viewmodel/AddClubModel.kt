package com.nxplayr.fsl.ui.fragments.usercurrentclub.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.AddClubPojo
import retrofit2.Call
import retrofit2.Response

class AddClubModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<AddClubPojo>>
    lateinit var mContext: Context
    var json: String ?= null
    var from:String=""

    fun getClubList(
            context: Context,
            isShowing: Boolean,
            json: String,from:String
    ): LiveData<List<AddClubPojo>> {
        this.json = json

        this.mContext = context
        this.from = from

        languageresponse = getClubListApi()

        return languageresponse
    }

    private fun getClubListApi(): LiveData<List<AddClubPojo>> {
        val data = MutableLiveData<List<AddClubPojo>>()

        var call : Call<List<AddClubPojo>>?=null
       when(from)
       {
           "Add"->{
               call= RestClient.get()!!.addClubList(json!!)

           }
           "Edit"->{
               call= RestClient.get()!!.editClubList(json!!)

           }
           "Delete"->{
               call= RestClient.get()!!.deleteClubList(json!!)

           }
           "List"->{
               call= RestClient.get()!!.clubList(json!!)

           }
       }

        call?.enqueue(object : RestCallback<List<AddClubPojo>>(mContext) {
            override fun Success(response: Response<List<AddClubPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }
}