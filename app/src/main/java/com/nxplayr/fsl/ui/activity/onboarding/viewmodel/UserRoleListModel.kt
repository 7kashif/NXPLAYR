package com.nxplayr.fsl.ui.activity.onboarding.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.UserRoleListPojo
import retrofit2.Response

class UserRoleListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<UserRoleListPojo>>
    lateinit var mContext: Context
    var json: String? = null

    fun getUserRoleList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<UserRoleListPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = geUserRoleListApi()

        return languageresponse
    }

    private fun geUserRoleListApi(): LiveData<List<UserRoleListPojo>> {
        val data = MutableLiveData<List<UserRoleListPojo>>()

        var call = RestClient.get()?.userRoleList(json!!)
        call?.enqueue(object : RestCallback<List<UserRoleListPojo>>(mContext) {
            override fun Success(response: Response<List<UserRoleListPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }
        })


        return data
    }

}