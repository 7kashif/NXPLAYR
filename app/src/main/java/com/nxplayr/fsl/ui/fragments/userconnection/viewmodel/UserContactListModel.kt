package com.nxplayr.fsl.ui.fragments.userconnection.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.SuggestedFreindListPojo
import retrofit2.Call
import retrofit2.Response

class UserContactListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<SuggestedFreindListPojo>>
    lateinit var mContext: Context
    var json: String? = null
    var from: String = ""

    fun getContactList(
            context: Context,
            isShowing: Boolean,
            json: String, from: String
    ): LiveData<List<SuggestedFreindListPojo>> {
        this.json = json

        this.mContext = context
        this.from = from

        languageresponse = getContactListApi()

        return languageresponse
    }

    private fun getContactListApi(): LiveData<List<SuggestedFreindListPojo>> {
        val data = MutableLiveData<List<SuggestedFreindListPojo>>()

        var call: Call<List<SuggestedFreindListPojo>>? = null
        when (from) {

            "Contact_list" -> {
                call = RestClient.get()!!.userContactList(json!!)
            }

        }

        call?.enqueue(object : RestCallback<List<SuggestedFreindListPojo>>(mContext) {
            override fun Success(response: Response<List<SuggestedFreindListPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }
}