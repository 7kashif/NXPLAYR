package com.nxplayr.fsl.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.UserDegreeListPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class DegreeListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<UserDegreeListPojo>>
    lateinit var mContext: Context
    var json: String? = null

    fun getDegreeList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<UserDegreeListPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getDegreeListApi()

        return languageresponse
    }

    private fun getDegreeListApi(): LiveData<List<UserDegreeListPojo>> {
        val data = MutableLiveData<List<UserDegreeListPojo>>()
        viewModelScope.launch(Dispatchers.IO) {
        var call = RestClient.get()!!.userDegreeListProfile(json!!)
        call!!.enqueue(object : RestCallback<List<UserDegreeListPojo>>(mContext) {
            override fun Success(response: Response<List<UserDegreeListPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })}

        return data
    }

}
