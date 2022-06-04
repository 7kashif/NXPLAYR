package com.nxplayr.fsl.ui.fragments.userhobbies.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.HobbiesPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class HobbiesModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<HobbiesPojo>>
    lateinit var mContext: Context
    var json: String? = null
    var from: String = ""

    fun getHobbiesList(
            context: Context,
            isShowing: Boolean,
            json: String, from: String
    ): LiveData<List<HobbiesPojo>> {
        this.json = json

        this.mContext = context
        this.from = from

        languageresponse = getHobbiesListApi()

        return languageresponse
    }

    private fun getHobbiesListApi(): LiveData<List<HobbiesPojo>> {
        val data = MutableLiveData<List<HobbiesPojo>>()
        viewModelScope.launch(Dispatchers.IO) {
        var call: Call<List<HobbiesPojo>>? = null
        when (from) {
            "Add" -> {
                call = RestClient.get()!!.userAddHobbiesProfile(json!!)

            }
            "Edit" -> {
                call = RestClient.get()!!.userEditHobbiesProfile(json!!)

            }
            "Delete" -> {
                call = RestClient.get()!!.userDeleteHobbiesProfile(json!!)

            }
            "List" -> {
                call = RestClient.get()!!.userListHobbiesProfile(json!!)

            }
        }

        call?.enqueue(object : RestCallback<List<HobbiesPojo>>(mContext) {
            override fun Success(response: Response<List<HobbiesPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })}

        return data
    }
}