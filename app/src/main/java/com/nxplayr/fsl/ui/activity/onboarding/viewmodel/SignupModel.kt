package com.nxplayr.fsl.ui.activity.onboarding.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.model.SignupPojo

class SignupModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<SignupPojo>>
    lateinit var mContext: Context
    var json: String? = null
    var name = ""
    var userMutableLiveData: MutableLiveData<List<SignupPojo>>? = null

    fun userRegistration(
        context: Context,
        isShowing: Boolean,
        json: String,
        name: String

    ): LiveData<List<SignupPojo>> {
        this.json = json

        this.mContext = context
        this.name = name
        languageresponse = userRegistrationApi()

        return languageresponse
    }

    private fun userRegistrationApi(): LiveData<List<SignupPojo>> {
        val data = MutableLiveData<List<SignupPojo>>()

//        viewModelScope.launch(Dispatchers.IO) {
//            var call: Response<List<SignupPojo>>? = null
//            if (name.equals("login"))
//                call = RestClient.get()!!.userLoginwithEmail(json!!)
//            else if (name.equals("signup"))
//                call = RestClient.get()!!.userRegistration(json!!)
//            else if (name.equals("update_profile"))
//                call = RestClient.get()!!.userInfoUpdate(json!!)
//            else if (name.equals("update_profile_picture"))
//                call = RestClient.get()!!.userUploadProfilePicture(json!!)
//            else if (name.equals("update_cover_photo"))
//                call = RestClient.get()!!.userupdateCoverPhoto(json!!)
//            else if (name.equals("changePassword"))
//                call = RestClient.get()!!.userChangePassword(json!!)
//            else if (name.equals("resetPassword"))
//                call = RestClient.get()!!.userResetPass(json!!)
//            else if (name.equals("forgot_pass"))
//                call = RestClient.get()!!.userForagatePass(json!!)
//            else if (name.equals("otp_verify"))
//                call = RestClient.get()!!.userVerifyOtp(json!!)
//            else if (name.equals("changeLanguage"))
//                call = RestClient.get()!!.users_changeLanguage(json!!)
//            else if (name.equals("updatePrivacy"))
//                call = RestClient.get()!!.users_updatePrivacy(json!!)
//            else if (name.equals("change_playerPosition"))
//                call = RestClient.get()!!.changePlayerPosition(json!!)
//            else if (name.equals("update_EmailNotification"))
//                call = RestClient.get()!!.updateEmailNotification(json!!)
//            else if (name.equals("update_PushNotification"))
//                call = RestClient.get()!!.updatePushNotification(json!!)
//            else if (name.equals("update_SmsNotification"))
//                call = RestClient.get()!!.updateSmsNotification(json!!)
//            else if (name.equals("checkDublication"))
//                call = RestClient.get()!!.checkDublication(json!!)
//            else if (name.equals("socialLogin"))
//                call = RestClient.get()!!.socialLogin(json!!)
//            else if (name.equals("other_userProfile"))
//                call = RestClient.get()!!.otherUserProfile(json!!)
//            else if (name.equals("parents_Profile"))
//                call = RestClient.get()!!.profileParents(json!!)
//            else if (name.equals("update_userProfile"))
//                call = RestClient.get()!!.userUpdateProfile(json!!)
//            else if (name.equals("parent_otp_verify"))
//                call = RestClient.get()!!.parentsVerifyOtp(json!!)
//            else if (name.equals("quickblockdetails"))
//                call = RestClient.get()!!.quickblockdetails(json!!)
//            else if (name.equals("updateDeviceToken", false)) {
//                call = RestClient.get()!!.updateDeviceToken(json!!)
//            } else if (name.equals("changeContentLanguage", false)) {
//                call = RestClient.get()!!.changeContentLanguage(json!!)
//            } else if (name.equals("sendVerification", false)) {
//                call = RestClient.get()!!.getSendVerification(json!!)
//            } else if (name.equals("partnerWithUs", false)) {
//                call = RestClient.get()!!.getPartnerWithUs(json!!)
//            } else if (name.equals("sendFeedback", false)) {
//                call = RestClient.get()!!.getSendFeedback(json!!)
//            }
////        else if(name.equals("resend_otp"))
////            call = RestClient.get()!!.resendOTP(json!!)
//
//            call!!.enqueue(object : RestCallback<List<SignupPojo>>(mContext) {
//                override fun Success(response: Response<List<SignupPojo>>) {
//                    data.value = response.body()
//                }
//
//                override fun failure() {
//                    data.value = null
//                }
//
//            })
//        }

        return data
    }
}