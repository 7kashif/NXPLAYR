package com.nxplayr.fsl.ui.fragments.postcomment.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CommentPojo
import com.nxplayr.fsl.util.MyUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import retrofit2.Call
import retrofit2.Response

class CommentModel : ViewModel() {

    lateinit var userLogin: LiveData<List<CommentPojo>?>
    lateinit var mContext: Activity
    var from: Int = -1
    var jsonArray: String = ""
    var progress: Boolean = false

    fun apiFunction(
        context: Activity, progress1 : Boolean,jsonArray: String, apiName: Int
    ): LiveData<List<CommentPojo>?> {
        this.progress = progress1
        this.jsonArray = jsonArray
        this.mContext = context
        this.from = apiName
        userLogin = apiResponse()
        return userLogin
    }

    private fun apiResponse(): LiveData<List<CommentPojo>?> {

        if (progress){
            MyUtils.showProgressDialog(mContext,"Wait..")
        }

        val data = MutableLiveData<List<CommentPojo>>()
        viewModelScope.launch(Dispatchers.IO) {
        var call: Call<List<CommentPojo>>? = null
        /**
         *
         * 0 for comment list
         * 1 for comment add
         * 2 for comment edit
         * 3 for comment delete
         * 4 for comment hide
         * 5 for comment report
         * */
        when(from){
            0 -> call = RestClient.get()!!.coomentList(jsonArray)
            1 -> call = RestClient.get()!!.coomentAdd(jsonArray)
            2 -> call = RestClient.get()!!.coomentEdit(jsonArray)
            3 -> call = RestClient.get()!!.coomentDelete(jsonArray)
            5 -> call = RestClient.get()!!.reportComment(jsonArray)
        }
        if (call != null) {
            call.enqueue(object : RestCallback<List<CommentPojo>?>(mContext) {


                override fun Success(response: Response<List<CommentPojo>?>) {
                    if (progress){
                        MyUtils.dismissProgressDialog()
                    }
                    data.value = response.body()
                }
                override fun failure() {
                    if (progress){
                        MyUtils.dismissProgressDialog()
                    }
                    data.value = null
                }
            })
        }}
        return data
    }
}