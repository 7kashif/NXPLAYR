package com.nxplayr.fsl.ui.fragments.userfollowers.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CommonPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response

class CommonStatusModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<CommonPojo>>
    lateinit var mContext: Context
    var json: String? = null
    var name = ""

    fun getCommonStatus(
            context: Context,
            isShowing: Boolean,
            json: String,
            name: String

    ): LiveData<List<CommonPojo>> {
        this.json = json
        this.name = name

        this.mContext = context
        this.name = name
        languageresponse = commonStatusApi()

        return languageresponse
    }

    private fun commonStatusApi(): LiveData<List<CommonPojo>> {
        val data = MutableLiveData<List<CommonPojo>>()
        viewModelScope.launch(Dispatchers.IO) {
        var call: Call<List<CommonPojo>>? = null
//        if (name.equals("forgot_pass"))
//            call = RestClient.get()!!.userForagatePass(json!!)
//        else if (name.equals("otp_verify"))
//            call = RestClient.get()!!.userVerifyOtp(json!!)
        if (name.equals("resend_otp"))
            call = RestClient.get()!!.resendOTP(json!!)
        if (name.equals("parents_resend_otp"))
            call = RestClient.get()!!.parentResendOTP(json!!)
        else if (name.equals("userFollow"))
            call = RestClient.get()!!.userFollow(json!!)
        else if (name.equals("userUnfollow"))
            call = RestClient.get()!!.userUnFollow(json!!)
        else if (name.equals("user_Like"))
            call = RestClient.get()!!.userLike(json!!)
        else if (name.equals("user_Unlike"))
            call = RestClient.get()!!.userUnlike(json!!)
        else if (name.equals("user_Report_Reason"))
            call = RestClient.get()!!.userPostReportReason(json!!)
        else if (name.equals("user_delete_Notification"))
            call = RestClient.get()!!.deleteNotification(json!!)
        else if (name.equals("deleteAccount"))
            call = RestClient.get()!!.deleteAccount(json!!)
        else if (name.equals("savePost"))
            call = RestClient.get()!!.savePost(json!!)
        else if (name.equals("removeSavePost"))
            call = RestClient.get()!!.removesavePost(json!!)
        else if (name.equals("hidePost"))
            call = RestClient.get()!!.hidePost(json!!)
        else if (name.equals("removehidePost"))
            call = RestClient.get()!!.removehidePost(json!!)
        else if (name.equals("deletePostList"))
            call = RestClient.get()!!.deletePostList(json!!)
        else if (name.equals("user_friend_report"))
            call = RestClient.get()!!.userfriendReportReason(json!!)
        else if (name.equals("user_comment_report"))
            call = RestClient.get()!!.usercommentReportReason(json!!)
         else if (name.equals("suggestionsFeature"))
            call = RestClient.get()!!.suggestionsFeature(json!!)

        call?.enqueue(object : RestCallback<List<CommonPojo>>(mContext) {
            override fun Success(response: Response<List<CommonPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })}


        return data
    }
}