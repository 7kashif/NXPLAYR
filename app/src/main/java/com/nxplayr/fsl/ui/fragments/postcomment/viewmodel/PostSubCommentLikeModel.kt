package com.nxplayr.fsl.ui.fragments.postcomment.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CommonPojo

import retrofit2.Call
import retrofit2.Response

class PostSubCommentLikeModel : ViewModel() {

    lateinit var userLogin: LiveData<List<CommonPojo>?>
    lateinit var mContext: Activity
    var from: Int = -1
    var jsonArray: String = ""
    var type = ""

    fun apiCall(context: Activity, jsonArray: String, type: String): LiveData<List<CommonPojo>?> {
        this.jsonArray = jsonArray
        this.mContext = context
        this.type = type
        userLogin = apiResponse()
        return userLogin
    }

    private fun apiResponse(): LiveData<List<CommonPojo>?> {
        val data = MutableLiveData<List<CommonPojo>>()
        var call: Call<List<CommonPojo>>? = null
        call = RestClient.get()!!.getPostReplyCommentLike(jsonArray)


        call?.enqueue(object : RestCallback<List<CommonPojo>?>(mContext) {
            override fun Success(response: Response<List<CommonPojo>?>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }
        })
        return data
    }
}