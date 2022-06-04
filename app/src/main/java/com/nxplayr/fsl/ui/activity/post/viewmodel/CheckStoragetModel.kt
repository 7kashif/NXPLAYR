package com.nxplayr.fsl.ui.activity.post.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CheckStoragePojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class CheckStoragetModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<CheckStoragePojo>>
    lateinit var mContext: Context
    var json: String?=null

    fun getCheckStorage(context: Context, json: String): LiveData<List<CheckStoragePojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getCheckStorageListApi()

        return languageresponse
    }

    private fun getCheckStorageListApi(): LiveData<List<CheckStoragePojo>> {
        val data = MutableLiveData<List<CheckStoragePojo>>()

        viewModelScope.launch(Dispatchers.IO) {
            var call = RestClient.get()!!.checkStorage(json!!)
            call!!.enqueue(object : RestCallback<List<CheckStoragePojo>>(mContext) {
                override fun Success(response: Response<List<CheckStoragePojo>>) {
                    data.value = response.body()
                }

                override fun failure() {
                    data.value = null
                }

            })
        }
        return data
    }

}