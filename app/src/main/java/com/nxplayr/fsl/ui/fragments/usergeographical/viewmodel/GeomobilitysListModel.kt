package com.nxplayr.fsl.ui.fragments.usergeographical.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.GeomobilitysPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class GeomobilitysListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<GeomobilitysPojo>>
    lateinit var mContext: Context
    var json: String?=null
    var s: String?=null
    fun getGeomobilitysList(
            context: Context,
            isShowing: Boolean,
            json: String,
            s: String
    ): LiveData<List<GeomobilitysPojo>> {
        this.json = json

        this.mContext = context
        this.s = s

        languageresponse = getGeomobilitysListApi()

        return languageresponse
    }

    private fun getGeomobilitysListApi(): LiveData<List<GeomobilitysPojo>> {
        val data = MutableLiveData<List<GeomobilitysPojo>>()
        viewModelScope.launch(Dispatchers.IO) {
        var call = RestClient.get()!!.getgeomobilitys(json!!)
        call!!.enqueue(object : RestCallback<List<GeomobilitysPojo>>(mContext) {
            override fun Success(response: Response<List<GeomobilitysPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })}

        return data
    }

}