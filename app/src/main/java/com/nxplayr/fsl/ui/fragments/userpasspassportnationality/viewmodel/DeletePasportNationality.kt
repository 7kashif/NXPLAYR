package com.nxplayr.fsl.ui.fragments.userpasspassportnationality.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.PassportNationalityPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class DeletePasportNationality : ViewModel() {

    lateinit var deletePost: LiveData<List<PassportNationalityPojo>>
    lateinit var mContext: Context
    var json: String? = null

    fun getDeleteCollecation(
            context: Context,
            json: String
    ): LiveData<List<PassportNationalityPojo>> {
        this.json = json

        this.mContext = context

        deletePost = getDeletePostApi()

        return deletePost
    }

    private fun getDeletePostApi(): LiveData<List<PassportNationalityPojo>> {
        val data = MutableLiveData<List<PassportNationalityPojo>>()
        viewModelScope.launch(Dispatchers.IO) {
        var call = RestClient.get()!!.deleteUserPassport(json!!)
        call!!.enqueue(object : RestCallback<List<PassportNationalityPojo>>(mContext) {
            override fun Success(response: Response<List<PassportNationalityPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })}

        return data
    }

}