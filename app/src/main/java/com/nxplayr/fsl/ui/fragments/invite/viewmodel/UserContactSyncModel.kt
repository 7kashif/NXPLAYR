package com.nxplayr.fsl.ui.fragments.invite.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.UserContactSyncPojo
import retrofit2.Call
import retrofit2.Response

class UserContactSyncModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<UserContactSyncPojo>>
    lateinit var mContext: Context
    var json: String? = null
    var from: String = ""

    fun getContactSync(
            context: Context,
            isShowing: Boolean,
            json: String, from: String
    ): LiveData<List<UserContactSyncPojo>> {
        this.json = json

        this.mContext = context
        this.from = from

        languageresponse = getContactSynctApi()

        return languageresponse
    }

    private fun getContactSynctApi(): LiveData<List<UserContactSyncPojo>> {
        val data = MutableLiveData<List<UserContactSyncPojo>>()

        var call: Call<List<UserContactSyncPojo>>? = null
        when (from) {
            "Contact_sync" -> {
                call = RestClient.get()!!.userContactSync(json!!)
            }

        }

        call?.enqueue(object : RestCallback<List<UserContactSyncPojo>>(mContext) {
            override fun Success(response: Response<List<UserContactSyncPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }
}