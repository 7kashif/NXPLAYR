package com.nxplayr.fsl.ui.activity.onboarding.repository

import android.util.Log
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CommonPojo
import com.nxplayr.fsl.data.model.SignupPojo
import com.nxplayr.fsl.util.SingleLiveEvent
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class SignUpRepository {

    private val TAG: String? = "TEST API"
    private val apiService = RestClient.get()
    val resendOTP = SingleLiveEvent<MutableList<CommonPojo>>()
    val userLoginwithEmail = SingleLiveEvent<MutableList<SignupPojo>>()
    val userRegistration = SingleLiveEvent<MutableList<SignupPojo>>()
    val userInfoUpdate = SingleLiveEvent<MutableList<SignupPojo>>()
    val userUploadProfilePicture = SingleLiveEvent<MutableList<SignupPojo>>()
    val userupdateCoverPhoto = SingleLiveEvent<MutableList<SignupPojo>>()
    val userChangePassword = SingleLiveEvent<MutableList<SignupPojo>>()
    val userResetPass = SingleLiveEvent<MutableList<SignupPojo>>()
    val userForgetPass = SingleLiveEvent<MutableList<SignupPojo>>()
    val userVerifyOtp = SingleLiveEvent<MutableList<SignupPojo>>()
    val users_changeLanguage = SingleLiveEvent<MutableList<SignupPojo>>()
    val users_updatePrivacy = SingleLiveEvent<MutableList<SignupPojo>>()
    val changePlayerPosition = SingleLiveEvent<MutableList<SignupPojo>>()
    val updateEmailNotification = SingleLiveEvent<MutableList<SignupPojo>>()
    val updatePushNotification = SingleLiveEvent<MutableList<SignupPojo>>()
    val updateSmsNotification = SingleLiveEvent<MutableList<SignupPojo>>()
    val checkDublication = SingleLiveEvent<MutableList<SignupPojo>>()
    val socialLogin = SingleLiveEvent<MutableList<SignupPojo>>()
    val otherUserProfile = SingleLiveEvent<MutableList<SignupPojo>>()
    val profileParents = SingleLiveEvent<MutableList<SignupPojo>>()
    val userUpdateProfile = SingleLiveEvent<MutableList<SignupPojo>>()
    val parentsVerifyOtp = SingleLiveEvent<MutableList<SignupPojo>>()
    val quickblockdetails = SingleLiveEvent<MutableList<SignupPojo>>()
    val updateDeviceToken = SingleLiveEvent<MutableList<SignupPojo>>()
    val changeContentLanguage = SingleLiveEvent<MutableList<SignupPojo>>()
    val getSendVerification = SingleLiveEvent<MutableList<SignupPojo>>()
    val getPartnerWithUs = SingleLiveEvent<MutableList<SignupPojo>>()
    val getSendFeedback = SingleLiveEvent<MutableList<SignupPojo>>()
    val failure = SingleLiveEvent<String>()

//    suspend fun resendOTP(json: String) {
//        try {
//            val response = apiService?.resendOTP(json)
//            Log.d(TAG, "$response")
//            if (response != null) {
//                if (response.isSuccessful) {
//                    Log.d(TAG, "SUCCESS")
//                    Log.d(TAG, "${response.body()}")
//                    resendOTP.postValue(response.body())
//                } else {
//                    Log.d(TAG, "FAILURE")
//                    Log.d(TAG, "${response.body()}")
//                    failure.postValue(response.message())
//                }
//            }
//        } catch (e: UnknownHostException) {
//            e.message?.let { Log.e(TAG, it) }
//            failure.postValue(e.message)
//        } catch (e: SocketTimeoutException) {
//            e.message?.let { Log.e(TAG, it) }
//            failure.postValue(e.message)
//        } catch (e: Exception) {
//            e.message?.let { Log.e(TAG, it) }
//            failure.postValue(e.message)
//        }
//    }

    suspend fun userLoginWithEmail(json: String) {
        try {
            val response = apiService?.userLoginwithEmail(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    userLoginwithEmail.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun userRegistration(json: String) {
        try {
            val response = apiService?.userRegistration(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    userLoginwithEmail.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun userInfoUpdate(json: String) {
        try {
            val response = apiService?.userInfoUpdate(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    userInfoUpdate.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun userUploadProfilePicture(json: String) {
        try {
            val response = apiService?.userUploadProfilePicture(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    userUploadProfilePicture.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun userUpdateCoverPhoto(json: String) {
        try {
            val response = apiService?.userupdateCoverPhoto(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    userupdateCoverPhoto.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun userChangePassword(json: String) {
        try {
            val response = apiService?.userChangePassword(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    userChangePassword.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun userResetPass(json: String) {
        try {
            val response = apiService?.userResetPass(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    userResetPass.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun userForgetPass(json: String) {
        try {
            val response = apiService?.userForagatePass(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    userForgetPass.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun userVerifyOtp(json: String) {
        try {
            val response = apiService?.userVerifyOtp(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    userVerifyOtp.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun users_changeLanguage(json: String) {
        try {
            val response = apiService?.users_changeLanguage(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    users_changeLanguage.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun users_updatePrivacy(json: String) {
        try {
            val response = apiService?.users_updatePrivacy(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    users_updatePrivacy.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun changePlayerPosition(json: String) {
        try {
            val response = apiService?.changePlayerPosition(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    changePlayerPosition.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun updateEmailNotification(json: String) {
        try {
            val response = apiService?.updateEmailNotification(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    updateEmailNotification.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun updatePushNotification(json: String) {
        try {
            val response = apiService?.updatePushNotification(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    updatePushNotification.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun updateSmsNotification(json: String) {
        try {
            val response = apiService?.updateSmsNotification(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    updateSmsNotification.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun checkDublication(json: String) {
        try {
            val response = apiService?.checkDublication(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    checkDublication.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun socialLogin(json: String) {
        try {
            val response = apiService?.socialLogin(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    socialLogin.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun otherUserProfile(json: String) {
        try {
            val response = apiService?.otherUserProfile(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    otherUserProfile.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun profileParents(json: String) {
        try {
            val response = apiService?.profileParents(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    profileParents.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun userUpdateProfile(json: String) {
        try {
            val response = apiService?.userUpdateProfile(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    userUpdateProfile.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun parentsVerifyOtp(json: String) {
        try {
            val response = apiService?.parentsVerifyOtp(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    parentsVerifyOtp.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun quickblockdetails(json: String) {
        try {
            val response = apiService?.quickblockdetails(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    quickblockdetails.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun updateDeviceToken(json: String) {
        try {
            val response = apiService?.updateDeviceToken(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    updateDeviceToken.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun changeContentLanguage(json: String) {
        try {
            val response = apiService?.changeContentLanguage(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    changeContentLanguage.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun getSendVerification(json: String) {
        try {
            val response = apiService?.getSendVerification(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    getSendVerification.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun getPartnerWithUs(json: String) {
        try {
            val response = apiService?.getPartnerWithUs(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    getPartnerWithUs.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun getSendFeedback(json: String) {
        try {
            val response = apiService?.getSendFeedback(json)
            Log.d(TAG, "$response")
            if (response != null) {
                if (response.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response.body()}")
                    getSendFeedback.postValue(response.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response.body()}")
                    failure.postValue(response.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }
}