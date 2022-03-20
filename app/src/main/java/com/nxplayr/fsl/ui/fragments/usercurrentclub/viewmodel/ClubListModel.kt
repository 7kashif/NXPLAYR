package com.nxplayr.fsl.ui.fragments.usercurrentclub.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.ClubListPojo
import retrofit2.Response

class ClubListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<ClubListPojo>>
    lateinit var mContext: Context
    var json: String? = null

    fun getClubList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<ClubListPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getClubListApi()

        return languageresponse
    }

    private fun getClubListApi(): LiveData<List<ClubListPojo>> {
        val data = MutableLiveData<List<ClubListPojo>>()

        var call = RestClient.get()!!.getClubList(json!!)
        call!!.enqueue(object : RestCallback<List<ClubListPojo>>(mContext) {
            override fun Success(response: Response<List<ClubListPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }

}
