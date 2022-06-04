package com.nxplayr.fsl.ui.fragments.userfollowers.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.FollowingListPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class FollowersModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<FollowingListPojo>>
    lateinit var mContext: Context
    var json: String? = null
    var from: String = ""

    fun getFollowerList(
            context: Context,
            isShowing: Boolean,
            json: String, from: String
    ): LiveData<List<FollowingListPojo>> {
        this.json = json

        this.mContext = context
        this.from = from

        languageresponse = getFollowersApi()

        return languageresponse
    }

    private fun getFollowersApi(): LiveData<List<FollowingListPojo>> {
        val data = MutableLiveData<List<FollowingListPojo>>()
        viewModelScope.launch(Dispatchers.IO) {
        var call: Call<List<FollowingListPojo>>? = null
        when (from) {
            "FollowingList" -> {
                call = RestClient.get()!!.userFollowingList(json!!)

            }


        }

        call?.enqueue(object : RestCallback<List<FollowingListPojo>>(mContext) {
            override fun Success(response: Response<List<FollowingListPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })}

        return data
    }
}