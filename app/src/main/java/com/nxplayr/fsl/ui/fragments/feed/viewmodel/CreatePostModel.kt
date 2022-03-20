package com.nxplayr.fsl.ui.fragments.feed.viewmodel

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.PostCreatePojo
import com.nxplayr.fsl.util.MyUtils
import retrofit2.Call
import retrofit2.Response

class CreatePostModel : ViewModel() {

    private var getResponse: LiveData<List<PostCreatePojo>>? = null
    private var mContext: Activity? = null
    private var json: String = ""
    private var from = ""

    fun apiFunction(context: Activity, json: String, from: String): LiveData<List<PostCreatePojo>> {
        this.mContext = context
        this.json = json
        this.from = from
        getResponse = apiREsponse()
        return getResponse!!
    }

    private fun apiREsponse(): LiveData<List<PostCreatePojo>> {
        val data = MutableLiveData<List<PostCreatePojo>>()
        var call: Call<List<PostCreatePojo>>? = null

        call = when (from) {
            "getPostList" -> {
                RestClient.get()!!.getPostList(json)
            }
            "editPost" -> {
                RestClient.get()!!.editPost(json)
            }
            "postShare" -> {
                RestClient.get()!!.sharePost(json)
            }
            else -> {
                RestClient.get()!!.createPost(json)
            }
        }

        call.enqueue(object : RestCallback<List<PostCreatePojo>>(mContext) {

            override fun Success(response: Response<List<PostCreatePojo>>) {

                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }
        })
        return data
    }

}