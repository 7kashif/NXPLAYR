package com.nxplayr.fsl.ui.fragments.userhashtag.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.HashtagsPojo
import retrofit2.Call
import retrofit2.Response

class HashtagsModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<HashtagsPojo>>
    lateinit var mContext: Context
    var json: String ?= null
    var from:String=""

    fun getHashtagsList(
            context: Context,
            isShowing: Boolean,
            json: String,from:String
    ): LiveData<List<HashtagsPojo>> {
        this.json = json

        this.mContext = context
        this.from = from

        languageresponse = getHashtagsListApi()

        return languageresponse
    }

    private fun getHashtagsListApi(): LiveData<List<HashtagsPojo>> {
        val data = MutableLiveData<List<HashtagsPojo>>()

        var call : Call<List<HashtagsPojo>>?=null
       when(from)
       {
           "Add"->{
               call= RestClient.get()!!.userAddHashtagsProfile(json!!)

           }
           "Edit"->{
               call= RestClient.get()!!.userEditHashtagsProfile(json!!)

           }
           "Delete"->{
               call= RestClient.get()!!.userDeleteHashtagsProfile(json!!)

           }
           "List"->{
               call= RestClient.get()!!.userListHashtagsProfile(json!!)

           }
       }

        call?.enqueue(object : RestCallback<List<HashtagsPojo>>(mContext) {
            override fun Success(response: Response<List<HashtagsPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }
}