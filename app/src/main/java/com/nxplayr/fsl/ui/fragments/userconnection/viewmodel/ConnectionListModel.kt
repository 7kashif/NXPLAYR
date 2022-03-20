package com.nxplayr.fsl.ui.fragments.userconnection.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.ConnectionTypePojo
import retrofit2.Response

class ConnectionListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<ConnectionTypePojo>>
    lateinit var mContext: Context
    var json: String? = null

    fun getConnectionTypeList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<ConnectionTypePojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getConnectionListApi()

        return languageresponse
    }

    private fun getConnectionListApi(): LiveData<List<ConnectionTypePojo>> {
        val data = MutableLiveData<List<ConnectionTypePojo>>()

        val call = RestClient.get()!!.connectionList(json!!)
        call.enqueue(object : RestCallback<List<ConnectionTypePojo>>(mContext) {
            override fun Success(response: Response<List<ConnectionTypePojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }

}
