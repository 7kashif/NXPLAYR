package com.nxplayr.fsl.ui.activity.addcountry.viewmmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.AddLocationPojo
import retrofit2.Response

class AddLocationModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<AddLocationPojo>>
    lateinit var mContext: Context
    var json: String?=null

    fun getLocation(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<AddLocationPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getLocationApi()

        return languageresponse
    }

    private fun getLocationApi(): LiveData<List<AddLocationPojo>> {
        val data = MutableLiveData<List<AddLocationPojo>>()

        var call = RestClient.get()!!.addLocation(json!!)
        call!!.enqueue(object : RestCallback<List<AddLocationPojo>>(mContext) {
            override fun Success(response: Response<List<AddLocationPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }

}