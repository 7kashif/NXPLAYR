package com.nxplayr.fsl.ui.fragments.postcomment.viewmodel

import android.app.Dialog
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.PostViewPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

/**
 * Created by dhavalkaka on 29/12/2017.
 */
class PostView : ViewModel() {

    var mcontext: Context? = null
    var isShowPb: Boolean? = null
    var postLikeResponse: LiveData<List<PostViewPojo>>? = null
    var parameter: String? = null
    var pbDialog: Dialog? = null

    fun postview(context: Context?, parameter: String?): LiveData<List<PostViewPojo>>? {
        this.mcontext = context
        this.parameter = parameter
        postLikeResponse = postviewApi()
        return postLikeResponse
    }

    fun postviewApi(): LiveData<List<PostViewPojo>> {
        val data = MutableLiveData<List<PostViewPojo>>()
        viewModelScope.launch(Dispatchers.IO) {
        val call: Call<List<PostViewPojo>> = RestClient.get()?.postview(parameter)!!
        call.enqueue(object : RestCallback<List<PostViewPojo>?>(mcontext) {
            override fun failure() {
                data.value = null
            }

            override fun Success(response: Response<List<PostViewPojo>?>) {
                data.value = response.body()
            }

        })}
        return data
    }
}