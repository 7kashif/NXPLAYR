package com.nxplayr.fsl.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CompanyListPojo
import retrofit2.Response

class CompanyListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<CompanyListPojo>>
    lateinit var mContext: Context
    var json: String? = null

    fun getCompanyList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<CompanyListPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getCompanyListApi()

        return languageresponse
    }

    private fun getCompanyListApi(): LiveData<List<CompanyListPojo>> {
        val data = MutableLiveData<List<CompanyListPojo>>()

        var call = RestClient.get()!!.userCompanyListProfile(json!!)
        call!!.enqueue(object : RestCallback<List<CompanyListPojo>>(mContext) {
            override fun Success(response: Response<List<CompanyListPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }

}
