package com.nxplayr.fsl.ui.fragments.userhobbies.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.HobbiesListPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class HobbiesListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<HobbiesListPojo>>
    lateinit var mContext: Context
    var json: String? = null

    fun getHobbiesList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<HobbiesListPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getHobbiesListApi()

        return languageresponse
    }

    private fun getHobbiesListApi(): LiveData<List<HobbiesListPojo>> {
        val data = MutableLiveData<List<HobbiesListPojo>>()
        viewModelScope.launch(Dispatchers.IO) {
        var call = RestClient.get()!!.userHobbiesList(json!!)
        call!!.enqueue(object : RestCallback<List<HobbiesListPojo>>(mContext) {
            override fun Success(response: Response<List<HobbiesListPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })}

        return data
    }

}
