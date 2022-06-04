package com.nxplayr.fsl.ui.fragments.usercurrentclub.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.AddClubPojo
import com.nxplayr.fsl.data.model.ClubListPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class AddClubModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<ClubListPojo>>
    lateinit var mContext: Context
    var json: String ?= null
    var from:String=""

    fun getClubList(
            context: Context,
            isShowing: Boolean,
            json: String,from:String
    ): LiveData<List<ClubListPojo>> {
        this.json = json

        this.mContext = context
        this.from = from

        languageresponse = getClubListApi()

        return languageresponse
    }

    private fun getClubListApi(): LiveData<List<ClubListPojo>> {
        val data = MutableLiveData<List<ClubListPojo>>()
        viewModelScope.launch(Dispatchers.IO) {
        var call : Call<List<ClubListPojo>>?=null
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

        call?.enqueue(object : RestCallback<List<ClubListPojo>>(mContext) {
            override fun Success(response: Response<List<ClubListPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })}

        return data
    }
}