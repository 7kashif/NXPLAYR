package com.nxplayr.fsl.ui.fragments.explorepost.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.RemoveCollectionPojo
import retrofit2.Response

class RemoveCollectionModel: ViewModel() {

    lateinit var removeCollectionResponse: LiveData<List<RemoveCollectionPojo>>
    lateinit var mContext: Context
    var json: String? = null
    var from: String = ""

    fun getRemoveCollectionData(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<RemoveCollectionPojo>> {

        this.json = json

        this.mContext = context
        this.from = from

        removeCollectionResponse = getRemoveCollectionApi()

        return removeCollectionResponse
    }

    private fun getRemoveCollectionApi(): LiveData<List<RemoveCollectionPojo>> {

        val data = MutableLiveData<List<RemoveCollectionPojo>>()
        val call = RestClient.get()!!.removeCollection(json!!)

        call.enqueue(object : RestCallback<List<RemoveCollectionPojo>>(mContext){
            override fun Success(response: Response<List<RemoveCollectionPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }
}