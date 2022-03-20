package com.nxplayr.fsl.ui.fragments.collection.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CreateAlbumListPojo
import retrofit2.Response

class CteateAlbumListModel: ViewModel() {

    lateinit var albumListResponse: LiveData<List<CreateAlbumListPojo>>
    lateinit var mContext: Context
    var json: String? = null
    var from: String = ""

    fun getAlbumDataList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<CreateAlbumListPojo>> {

        this.json = json

        this.mContext = context
        this.from = from

        albumListResponse = getCtreateAlbumListApi()

        return albumListResponse
    }

    private fun getCtreateAlbumListApi(): LiveData<List<CreateAlbumListPojo>> {

        val data = MutableLiveData<List<CreateAlbumListPojo>>()
        val call = RestClient.get()!!.createAlbumList(json!!)

        call.enqueue(object : RestCallback<List<CreateAlbumListPojo>>(mContext){
            override fun Success(response: Response<List<CreateAlbumListPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }
}