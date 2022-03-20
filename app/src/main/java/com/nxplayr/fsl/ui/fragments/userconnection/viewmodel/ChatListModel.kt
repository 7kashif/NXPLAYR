package com.nxplayr.fsl.ui.fragments.userconnection.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.ChatList
import retrofit2.Response

class ChatListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<ChatList>>
    lateinit var mContext: Context
    var json: String? = null

    fun getConnectionTypeList(
            context: Context,
            json: String
    ): LiveData<List<ChatList>> {
        this.json = json

        this.mContext = context

        languageresponse = getConnectionListApi()

        return languageresponse
    }

    private fun getConnectionListApi(): LiveData<List<ChatList>> {
        val data = MutableLiveData<List<ChatList>>()

        val call = RestClient.get()!!.chatlist(json!!)
        call.enqueue(object : RestCallback<List<ChatList>>(mContext) {
            override fun Success(response: Response<List<ChatList>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }

}
