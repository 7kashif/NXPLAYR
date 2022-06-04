package com.nxplayr.fsl.ui.fragments.userlanguage.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.ProficiencyPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class ProficiencyModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<ProficiencyPojo>>
    lateinit var mContext: Context
    var json: String? = null

    fun getProficiencyList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<ProficiencyPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getProficiencyListApi()

        return languageresponse
    }

    private fun getProficiencyListApi(): LiveData<List<ProficiencyPojo>> {
        val data = MutableLiveData<List<ProficiencyPojo>>()
        viewModelScope.launch(Dispatchers.IO) {
        var call = RestClient.get()!!.userProficiencyListProfile(json!!)
        call!!.enqueue(object : RestCallback<List<ProficiencyPojo>>(mContext) {
            override fun Success(response: Response<List<ProficiencyPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })}

        return data
    }

}
