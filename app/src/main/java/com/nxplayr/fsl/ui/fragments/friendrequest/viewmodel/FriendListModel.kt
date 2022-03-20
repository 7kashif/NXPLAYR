package com.nxplayr.fsl.ui.fragments.friendrequest.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.FriendListPojo
import retrofit2.Call
import retrofit2.Response

class FriendListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<FriendListPojo>>
    lateinit var mContext: Context
    var json: String? = null
    var from: String = ""

    fun getFriendList(
            context: Context,
            json: String, from: String
    ): LiveData<List<FriendListPojo>> {
        this.mContext = context
        this.json = json
        this.from = from
        languageresponse = getFriendListApi()

        return languageresponse
    }

    private fun getFriendListApi(): LiveData<List<FriendListPojo>> {
        val data = MutableLiveData<List<FriendListPojo>>()


        var call: Call<List<FriendListPojo>>? = null

        when (from) {

            "friend_list" -> {
                call = RestClient.get()!!.friendList(json!!)
            }
            "change_friendList" -> {
                call = RestClient.get()!!.changeConnection(json!!)
            }
            "chatTofriend" -> {
                call = RestClient.get()!!.chatTofriend(json!!)
            }

        }

        call?.enqueue(object : RestCallback<List<FriendListPojo>>(mContext) {
            override fun Success(response: Response<List<FriendListPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }

}
