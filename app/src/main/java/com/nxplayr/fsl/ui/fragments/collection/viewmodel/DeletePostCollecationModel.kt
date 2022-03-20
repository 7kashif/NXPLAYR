package com.nxplayr.fsl.ui.fragments.collection.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.DeleteCollecationPojo
import retrofit2.Response

class DeletePostCollecationModel : ViewModel() {

    lateinit var deletePost: LiveData<List<DeleteCollecationPojo>>
    lateinit var mContext: Context
    var json: String? = null

    fun getDeleteCollecation(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<DeleteCollecationPojo>> {
        this.json = json

        this.mContext = context

        deletePost = getDeletePostApi()

        return deletePost
    }

    private fun getDeletePostApi(): LiveData<List<DeleteCollecationPojo>> {
        val data = MutableLiveData<List<DeleteCollecationPojo>>()

        var call = RestClient.get()!!.deleteCollecationPost(json!!)
        call!!.enqueue(object : RestCallback<List<DeleteCollecationPojo>>(mContext) {
            override fun Success(response: Response<List<DeleteCollecationPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }

}