package com.nxplayr.fsl.ui.activity.addhashtag.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.HashtagListPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class HashtagsListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<HashtagListPojo>>
    lateinit var mContext: Context
    var json: String? = null

    fun getHashtagList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<HashtagListPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getHashtagListApi()

        return languageresponse
    }

    private fun getHashtagListApi(): LiveData<List<HashtagListPojo>> {
        val data = MutableLiveData<List<HashtagListPojo>>()

        viewModelScope.launch(Dispatchers.IO) {
            var call = RestClient.get()!!.userHashtagsList(json!!)
            call!!.enqueue(object : RestCallback<List<HashtagListPojo>>(mContext) {
                override fun Success(response: Response<List<HashtagListPojo>>) {
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
