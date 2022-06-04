package com.nxplayr.fsl.ui.fragments.explorepost.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.LikePostPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class LikePostModel:ViewModel() {

    lateinit var likePostRespone: LiveData<List<LikePostPojo>>
    lateinit var mContext: Context
    var json: String? = null
    var from: String = ""

    fun getPostLikeApi(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<LikePostPojo>> {

        this.json = json

        this.mContext = context
        this.from = from

        likePostRespone = getLikePostApi()

        return likePostRespone
    }

    private fun getLikePostApi(): LiveData<List<LikePostPojo>> {

        val data = MutableLiveData<List<LikePostPojo>>()
        viewModelScope.launch(Dispatchers.IO) {
            val call = RestClient.get()!!.userLikePost(json!!)

            call.enqueue(object : RestCallback<List<LikePostPojo>>(mContext) {
                override fun Success(response: Response<List<LikePostPojo>>) {
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