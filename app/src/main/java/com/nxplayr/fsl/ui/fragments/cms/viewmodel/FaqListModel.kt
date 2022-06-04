package com.nxplayr.fsl.ui.fragments.cms.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.FaqPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class FaqListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<FaqPojo>>
    lateinit var mContext: Context
    var json: String? = null

    fun getFaqList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<FaqPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getFaqListApi()

        return languageresponse
    }

    private fun getFaqListApi(): LiveData<List<FaqPojo>> {
        val data = MutableLiveData<List<FaqPojo>>()
        viewModelScope.launch(Dispatchers.IO) {
            var call = RestClient.get()!!.faqList(json!!)
            call!!.enqueue(object : RestCallback<List<FaqPojo>>(mContext) {
                override fun Success(response: Response<List<FaqPojo>>) {
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
