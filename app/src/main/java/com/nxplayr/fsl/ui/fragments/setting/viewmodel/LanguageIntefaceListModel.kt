package com.nxplayr.fsl.ui.fragments.setting.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.InteraceLanguageListPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class LanguageIntefaceListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<InteraceLanguageListPojo>>
    lateinit var mContext: Context
    var json: String? = null

    fun getLanguageList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<InteraceLanguageListPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getLanguageListApi()

        return languageresponse
    }

    private fun getLanguageListApi(): LiveData<List<InteraceLanguageListPojo>> {
        val data = MutableLiveData<List<InteraceLanguageListPojo>>()
        viewModelScope.launch(Dispatchers.IO) {
        var call = RestClient.get()!!.languageList(json!!)
        call!!.enqueue(object : RestCallback<List<InteraceLanguageListPojo>>(mContext) {
            override fun Success(response: Response<List<InteraceLanguageListPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })}

        return data
    }

}