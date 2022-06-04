package com.nxplayr.fsl.ui.activity.onboarding.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.ui.activity.onboarding.repository.SignUpRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignupModelV2 : ViewModel() {

    private val repository = SignUpRepository()
    val resendOTP = repository.resendOTP
    val userLoginwithEmail = repository.userLoginwithEmail
    val userRegistration = repository.userRegistration
    val userInfoUpdate = repository.userInfoUpdate
    val userUploadProfilePicture = repository.userUploadProfilePicture
    val userupdateCoverPhoto = repository.userupdateCoverPhoto
    val userChangePassword = repository.userChangePassword
    val userResetPass = repository.userResetPass
    val userForgetPass = repository.userForgetPass
    val userVerifyOtp = repository.userVerifyOtp
    val users_changeLanguage = repository.users_changeLanguage
    val users_updatePrivacy = repository.users_updatePrivacy
    val changePlayerPosition = repository.changePlayerPosition
    val updateEmailNotification = repository.updateEmailNotification
    val updatePushNotification = repository.updatePushNotification
    val updateSmsNotification = repository.updateSmsNotification
    val checkDublication = repository.checkDublication
    val socialLogin = repository.socialLogin
    val otherUserProfile = repository.otherUserProfile
    val profileParents = repository.profileParents
    val userUpdateProfile = repository.userUpdateProfile
    val parentsVerifyOtp = repository.parentsVerifyOtp
    val quickblockdetails = repository.quickblockdetails
    val updateDeviceToken = repository.updateDeviceToken
    val changeContentLanguage = repository.changeContentLanguage
    val getSendVerification = repository.getSendVerification
    val getPartnerWithUs = repository.getPartnerWithUs
    val getSendFeedback = repository.getSendFeedback


//    fun resendOTP(json: String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.resendOTP(json)
//        }
//    }

    fun userLoginWithEmail(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.userLoginWithEmail(json)
        }
    }

    fun userRegistration(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.userRegistration(json)
        }
    }

    fun userInfoUpdate(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.userInfoUpdate(json)
        }
    }

    fun userUploadProfilePicture(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.userUploadProfilePicture(json)
        }
    }

    fun userupdateCoverPhoto(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.userUpdateCoverPhoto(json)
        }
    }

    fun userChangePassword(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.userChangePassword(json)
        }
    }

    fun userResetPass(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.userResetPass(json)
        }
    }

    fun userForgetPass(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.userForgetPass(json)
        }
    }

    fun userVerifyOtp(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.userVerifyOtp(json)
        }
    }

    fun users_changeLanguage(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.users_changeLanguage(json)
        }
    }

    fun users_updatePrivacy(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.users_updatePrivacy(json)
        }
    }

    fun changePlayerPosition(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.changePlayerPosition(json)
        }
    }

    fun updateEmailNotification(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateEmailNotification(json)
        }
    }

    fun updatePushNotification(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePushNotification(json)
        }
    }

    fun updateSmsNotification(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSmsNotification(json)
        }
    }

    fun checkDublication(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.checkDublication(json)
        }
    }

    fun socialLogin(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.socialLogin(json)
        }
    }

    fun otherUserProfile(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.otherUserProfile(json)
        }
    }

    fun profileParents(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.profileParents(json)
        }
    }

    fun userUpdateProfile(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.userUpdateProfile(json)
        }
    }

    fun parentsVerifyOtp(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.parentsVerifyOtp(json)
        }
    }

    fun quickblockdetails(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.quickblockdetails(json)
        }
    }

    fun updateDeviceToken(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateDeviceToken(json)
        }
    }

    fun changeContentLanguage(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.changeContentLanguage(json)
        }
    }

    fun getSendVerification(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getSendVerification(json)
        }
    }

    fun getPartnerWithUs(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPartnerWithUs(json)
        }
    }

    fun getSendFeedback(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getSendFeedback(json)
        }
    }
}