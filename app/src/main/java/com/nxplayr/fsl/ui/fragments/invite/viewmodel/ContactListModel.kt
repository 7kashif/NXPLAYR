package com.nxplayr.fsl.ui.fragments.invite.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.ContactListPojo
import retrofit2.Call
import retrofit2.Response

class ContactListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<ContactListPojo>>
    lateinit var mContext: Context
    var json: String? = null
    var from: String = ""

    fun getContactList(
            context: Context,
            isShowing: Boolean,
            json: String, from: String
    ): LiveData<List<ContactListPojo>> {
        this.json = json

        this.mContext = context
        this.from = from

        languageresponse = getContactListApi()

        return languageresponse
    }

    private fun getContactListApi(): LiveData<List<ContactListPojo>> {
        val data = MutableLiveData<List<ContactListPojo>>()

        var call: Call<List<ContactListPojo>>? = null
        when (from) {

            "contact_list" -> {
                call = RestClient.get()!!.getContactList(json!!)
            }

        }

        call?.enqueue(object : RestCallback<List<ContactListPojo>>(mContext) {
            override fun Success(response: Response<List<ContactListPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }
}