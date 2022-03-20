package com.nxplayr.fsl.ui.fragments.feedlikeviewlist.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.LikeListPojo
import retrofit2.Call
import retrofit2.Response

class UserLikeListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<LikeListPojo>>
    lateinit var mContext: Context
    var json: String? = null
    var from: String = ""

    fun getLikeList(
            context: Context,
            isShowing: Boolean,
            json: String, from: String
    ): LiveData<List<LikeListPojo>> {
        this.json = json

        this.mContext = context
        this.from = from

        languageresponse = getContactListApi()

        return languageresponse
    }

    private fun getContactListApi(): LiveData<List<LikeListPojo>> {
        val data = MutableLiveData<List<LikeListPojo>>()

        var call: Call<List<LikeListPojo>>? = null
        when (from) {
            "user_likeList" -> {
                call = RestClient.get()!!.userLikeList(json!!)
            }

        }

        call?.enqueue(object : RestCallback<List<LikeListPojo>>(mContext) {
            override fun Success(response: Response<List<LikeListPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }
}