package com.nxplayr.fsl.ui.fragments.feedlikeviewlist.viewmodel

import android.app.Dialog
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CreatePostData
import com.nxplayr.fsl.data.model.PostlikePojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.util.*

/**
 * Created by dhavalkaka on 29/12/2017.
 */
class PostLike : ViewModel() {
    var mcontext: Context? = null
    var isShowPb: Boolean? = null
    var postLikeResponse: LiveData<List<PostlikePojo>>? = null
    var parameter: String? = null
    var pbDialog: Dialog? = null
    var pos: String? = null
    var action: String? = null
    var feedDatum: CreatePostData? = null
    fun postLike(context: Context?, parameter: String?, isShowPb: Boolean?, pos: String?, action: String?, feedDatum: CreatePostData?): LiveData<List<PostlikePojo>>? {
        mcontext = context
        this.isShowPb = isShowPb
        this.parameter = parameter
        this.pos = pos
        this.action = action
        this.feedDatum = feedDatum
        postLikeResponse = postLikeApi()
        return postLikeResponse
    }

    fun postLikeApi(): LiveData<List<PostlikePojo>> {
        val data = MutableLiveData<List<PostlikePojo>>()
        viewModelScope.launch(Dispatchers.IO) {
            val call: Call<List<PostlikePojo>> = RestClient.get()?.postlike(parameter)!!
            call.enqueue(object : RestCallback<List<PostlikePojo>>(mcontext) {
                override fun failure() {
                    val postlikePojo = PostlikePojo()
                    postlikePojo.message = action
                    postlikePojo.status = "false"
                    postlikePojo.postId = pos
                    postlikePojo.feedDatum = feedDatum
                    val postlikePojos: MutableList<PostlikePojo> = ArrayList()
                    postlikePojos.add(postlikePojo)
                    data.setValue(postlikePojos)
                }

                override fun Success(response: Response<List<PostlikePojo>>) {
                    if (response.body() != null) {
                        if (response.body()!![0].status.equals("true", ignoreCase = true)) {
                            val postlikePojos: MutableList<PostlikePojo> = ArrayList()
                            postlikePojos.addAll(response.body()!!)
                            postlikePojos[0].postId = pos
                            postlikePojos[0].feedDatum = feedDatum
                            data.setValue(postlikePojos)
                        } else {
                        }
                    }
                }
            })
        }
        return data
    }
}