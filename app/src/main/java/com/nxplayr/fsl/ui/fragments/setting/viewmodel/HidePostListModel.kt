package com.nxplayr.fsl.ui.fragments.setting.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.HidePostListPojo
import retrofit2.Call
import retrofit2.Response

class HidePostListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<HidePostListPojo>>
    lateinit var mContext: Context
    var json: String? = null
    var from: String = ""

    fun gethidePostList(
            context: Context,
            isShowing: Boolean,
            json: String,
            from: String
    ): LiveData<List<HidePostListPojo>> {
        this.mContext = context
        this.json = json
        this.from = from
        languageresponse = gethidePostListApi()

        return languageresponse
    }

    private fun gethidePostListApi(): LiveData<List<HidePostListPojo>> {
        val data = MutableLiveData<List<HidePostListPojo>>()

        var call: Call<List<HidePostListPojo>>? = null
        when (from) {

            "hidePostList" -> {
                call = RestClient.get()!!.hidePostList(json!!)
            }
//            "blockUserList" -> {
//                call = RestClient.get()!!.blockUserList(json!!)
//            }

        }


        call!!.enqueue(object : RestCallback<List<HidePostListPojo>>(mContext) {
            override fun Success(response: Response<List<HidePostListPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }

}
