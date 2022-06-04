package com.nxplayr.fsl.ui.activity.onboarding.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CountryListPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class CountryListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<CountryListPojo>>
    lateinit var mContext: Context
    var json: String?=null

    fun getCountryList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<CountryListPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getCountryListApi()

        return languageresponse
    }

    private fun getCountryListApi(): LiveData<List<CountryListPojo>> {
        val data = MutableLiveData<List<CountryListPojo>>()
        viewModelScope.launch(Dispatchers.IO) {
            var call = RestClient.get()!!.countryList(json!!)
            call!!.enqueue(object : RestCallback<List<CountryListPojo>>(mContext) {
                override fun Success(response: Response<List<CountryListPojo>>) {
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