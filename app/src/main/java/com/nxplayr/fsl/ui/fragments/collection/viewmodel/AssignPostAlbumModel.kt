package com.nxplayr.fsl.ui.fragments.collection.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.AssignPostAlbumPojo
import retrofit2.Response

class AssignPostAlbumModel :ViewModel() {

    lateinit var assignPostsponse: LiveData<List<AssignPostAlbumPojo>>
    lateinit var mContext: Context
    var json: String? = null
    var from: String = ""

    fun getAssignPostAlbum(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<AssignPostAlbumPojo>> {
        this.json = json

        this.mContext = context
        this.from = from

        assignPostsponse = getAssignPostAlbumApi()

        return assignPostsponse
    }

    private fun getAssignPostAlbumApi(): LiveData<List<AssignPostAlbumPojo>> {

        val data = MutableLiveData<List<AssignPostAlbumPojo>>()
        val call = RestClient.get()!!.assignPostAlbum(json!!)

        call.enqueue(object : RestCallback<List<AssignPostAlbumPojo>>(mContext){
            override fun Success(response: Response<List<AssignPostAlbumPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }
}