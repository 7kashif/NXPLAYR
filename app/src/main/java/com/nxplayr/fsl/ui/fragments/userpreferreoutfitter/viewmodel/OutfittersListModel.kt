package com.nxplayr.fsl.ui.fragments.userpreferreoutfitter.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.OutfittersPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class OutfittersListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<OutfittersPojo>>
    lateinit var mContext: Context
    var json: String?=null
    var s: String?=null
    fun getOutfittersList(
            context: Context,
            isShowing: Boolean,
            json: String,
            s: String
    ): LiveData<List<OutfittersPojo>> {
        this.json = json

        this.mContext = context
        this.s = s

        languageresponse = getOutfittersListApi()

        return languageresponse
    }

    private fun getOutfittersListApi(): LiveData<List<OutfittersPojo>> {
        val data = MutableLiveData<List<OutfittersPojo>>()
        viewModelScope.launch(Dispatchers.IO) {
            var call: Call<List<OutfittersPojo>>? = null
            when (s) {
                "Add" -> {
                    call = RestClient.get()!!.getAddOutfittersProfile(json!!)
                }
                else -> {
                    call = RestClient.get()!!.getuserOutfittersProfile(json!!)

                }
            }
            call?.enqueue(object : RestCallback<List<OutfittersPojo>>(mContext) {
                override fun Success(response: Response<List<OutfittersPojo>>) {
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