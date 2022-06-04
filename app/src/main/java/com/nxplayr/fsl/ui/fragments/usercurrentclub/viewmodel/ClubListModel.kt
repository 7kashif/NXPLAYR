package com.nxplayr.fsl.ui.fragments.usercurrentclub.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.ClubListPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class ClubListModel : ViewModel() {

    lateinit var languageresponse: LiveData<ClubListPojo>
    lateinit var mContext: Context
    var json: String? = null

    fun getClubList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<ClubListPojo> {
        this.json = json

        this.mContext = context

        languageresponse = getClubListApi()

        return languageresponse
    }

    private fun getClubListApi(): LiveData<ClubListPojo> {
        val data = MutableLiveData<ClubListPojo>()
        viewModelScope.launch(Dispatchers.IO) {
        var call = RestClient.get()!!.getClubList(json!!)
        call.enqueue(object : RestCallback<ClubListPojo>(mContext) {
            override fun Success(response: Response<ClubListPojo>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })}

        return data
    }

}
