package com.nxplayr.fsl.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.UserUniversityListPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class UniversityListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<UserUniversityListPojo>>
    lateinit var mContext: Context
    var json: String? = null

    fun getUniversityList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<UserUniversityListPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getUniversityListApi()

        return languageresponse
    }

    private fun getUniversityListApi(): LiveData<List<UserUniversityListPojo>> {
        val data = MutableLiveData<List<UserUniversityListPojo>>()
        viewModelScope.launch(Dispatchers.IO) {
        var call = RestClient.get()!!.userUniversityListProfile(json!!)
        call!!.enqueue(object : RestCallback<List<UserUniversityListPojo>>(mContext) {
            override fun Success(response: Response<List<UserUniversityListPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })}

        return data
    }

}
