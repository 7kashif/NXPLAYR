package com.nxplayr.fsl.ui.fragments.blockeduser.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.BlockUserListPojo
import retrofit2.Response

class BlockUserListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<BlockUserListPojo>>
    lateinit var mContext: Context
    var json: String? = null

    fun getBlockUserList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<BlockUserListPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getBlockUserListApi()

        return languageresponse
    }

    private fun getBlockUserListApi(): LiveData<List<BlockUserListPojo>> {
        val data = MutableLiveData<List<BlockUserListPojo>>()

        var call = RestClient.get()!!.blockUserList(json!!)
        call!!.enqueue(object : RestCallback<List<BlockUserListPojo>>(mContext) {
            override fun Success(response: Response<List<BlockUserListPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }

}