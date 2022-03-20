package com.nxplayr.fsl.ui.fragments.userlanguage.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.LanguagesPojo
import retrofit2.Response

class LanguagesModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<LanguagesPojo>>
    lateinit var mContext: Context
    var json: String? = null

    fun getLanguageist(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<LanguagesPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getLanguageListApi()

        return languageresponse
    }

    private fun getLanguageListApi(): LiveData<List<LanguagesPojo>> {
        val data = MutableLiveData<List<LanguagesPojo>>()

        var call = RestClient.get()!!.userLanguageListProfile(json!!)
        call!!.enqueue(object : RestCallback<List<LanguagesPojo>>(mContext) {
            override fun Success(response: Response<List<LanguagesPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }

}
