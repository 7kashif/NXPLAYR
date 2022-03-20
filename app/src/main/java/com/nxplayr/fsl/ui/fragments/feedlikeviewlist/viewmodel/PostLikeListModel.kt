package com.nxplayr.fsl.ui.fragments.feedlikeviewlist.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.PostLikeViewListItem
import retrofit2.Call
import retrofit2.Response

class PostLikeListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<PostLikeViewListItem>>
    lateinit var mContext: Context
    var json: String? = null
    var from: String = ""

    fun getPostLikeViewList(
            context: Context,
            isShowing: Boolean,
            json: String, from: String
    ): LiveData<List<PostLikeViewListItem>> {
        this.json = json

        this.mContext = context
        this.from = from

        languageresponse = getFollowersApi()

        return languageresponse
    }

    private fun getFollowersApi(): LiveData<List<PostLikeViewListItem>> {
        val data = MutableLiveData<List<PostLikeViewListItem>>()

        var call: Call<List<PostLikeViewListItem>>? = null
        when (from) {
            "postViewList" -> {
                call = RestClient.get()!!.postViewList(json!!)

            }
            "postLikeList" -> {
                call = RestClient.get()!!.postLikeList(json!!)

            }


        }

        call?.enqueue(object : RestCallback<List<PostLikeViewListItem>>(mContext) {
            override fun Success(response: Response<List<PostLikeViewListItem>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }
}