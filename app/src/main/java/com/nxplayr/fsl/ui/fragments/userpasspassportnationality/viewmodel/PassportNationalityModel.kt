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
import retrofit2.Call
import retrofit2.Response

class PassportNationalityModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<PassportNationalityPojo>>
    lateinit var mContext: Context
    var json: String? = null
    var from: String = ""

    fun getNationalityList(
            context: Context,
            isShowing: Boolean,
            json: String, from: String
    ): LiveData<List<PassportNationalityPojo>> {
        this.json = json

        this.mContext = context
        this.from = from

        languageresponse = getNationalityListApi()

        return languageresponse
    }

    private fun getNationalityListApi(): LiveData<List<PassportNationalityPojo>> {
        val data = MutableLiveData<List<PassportNationalityPojo>>()
        viewModelScope.launch(Dispatchers.IO) {
            var call: Call<List<PassportNationalityPojo>>? = null
            when (from) {
                "Add" -> {
                    // call = RestClient.get()!!.addUserPassport(json!!)
                }
                "Edit" -> {
                    // call = RestClient.get()!!.editUserPassport(json!!)

                }
                "Delete" -> {
                    // call = RestClient.get()!!.deleteUserPassport(json!!)

                }
                "List" -> {
                    call = RestClient.get()!!.listUserPassport(json!!)

                }
            }

            call?.enqueue(object : RestCallback<List<PassportNationalityPojo>>(mContext) {
                override fun Success(response: Response<List<PassportNationalityPojo>>) {
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