package com.nxplayr.fsl.ui.fragments.usergeographical.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.SignupPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class UpdateResumeCallsViewModel: ViewModel() {
    var context: Activity?=null
    var from: String=""
    var json:String=""
     var users :LiveData<List<SignupPojo>>?=null
    var userId:String=""
    fun getUpdateResume(context: Activity, from: String, toString: String): LiveData<List<SignupPojo>> {
        this.from = from
        this.context = context
        this.json = toString
        users=fetchUsers()
        return users!!
    }
    private fun fetchUsers() : LiveData<List<SignupPojo>> {

        val data = MutableLiveData<List<SignupPojo>>()
        viewModelScope.launch(Dispatchers.IO) {
            var call: Call<List<SignupPojo>>? = null
            when (from) {
                "Add" -> {
                    call = RestClient.get()!!.getUpdateResume(json!!)
                }
                "Edit" -> {
                    call = RestClient.get()!!.getUpdateResume(json!!)
                }
            }
            call?.enqueue(object : RestCallback<List<SignupPojo>>(context) {
                override fun Success(response: Response<List<SignupPojo>>) {
                    data.value = response.body()
                }

                override fun failure() {
                    data.value = null
                }

            })
        }
        return data
    }
}



