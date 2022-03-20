package com.nxplayr.fsl.ui.fragments.feed.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.SavePostListPojo
import retrofit2.Call
import retrofit2.Response

class SavePostListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<SavePostListPojo>>
    lateinit var mContext: Context
    var json: String? = null
    var from: String = ""

    fun getsavePostList(
            context: Context,
            isShowing: Boolean,
            json: String,
            from: String
    ): LiveData<List<SavePostListPojo>> {
        this.mContext = context
        this.json = json
        this.from = from
        languageresponse = getsavePostListApi()

        return languageresponse
    }

    private fun getsavePostListApi(): LiveData<List<SavePostListPojo>> {
        val data = MutableLiveData<List<SavePostListPojo>>()

        var call: Call<List<SavePostListPojo>>? = null
        when (from) {
            "savePostList" -> {
                call = RestClient.get()!!.savePostList(json!!)
            }
        }
        call!!.enqueue(object : RestCallback<List<SavePostListPojo>>(mContext) {
            override fun Success(response: Response<List<SavePostListPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }

}
