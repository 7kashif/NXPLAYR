package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class UserContactSyncPojo(
    @SerializedName("data")
        var `data`: ArrayList<ContactUserType> = ArrayList(),
    @SerializedName("message")
        var message: String = "",
    @SerializedName("status")
        var status: String = ""
) : Serializable

data class ContactUserType(
        @SerializedName("FslUsers")
        var fslUsers: List<Any>,
        @SerializedName("NormalUsers")
        var normalUsers: ArrayList<NormalUser>? = ArrayList()
) : Serializable

data class NormalUser(
        @SerializedName("userFirstName")
        var userFirstName: Any,
        @SerializedName("userID")
        var userID: String = "",
        @SerializedName("userLastName")
        var userLastName: Any,
        @SerializedName("userNickname")
        var userNickname: Any,
        @SerializedName("userProfilePicture")
        var userProfilePicture: Any,
        @SerializedName("usercontactCreatedDate")
        var usercontactCreatedDate: String = "",
        @SerializedName("usercontactEmail")
        var usercontactEmail: String = "",
        @SerializedName("usercontactFirstName")
        var usercontactFirstName: String = "",
        @SerializedName("usercontactFslUser")
        var usercontactFslUser: String = "",
        @SerializedName("usercontactID")
        var usercontactID: String = "",
        @SerializedName("usercontactLastName")
        var usercontactLastName: String = "",
        @SerializedName("usercontactPhone")
        var usercontactPhone: String = "",
        @SerializedName("usercontactSyncedFrom")
        var usercontactSyncedFrom: String = ""
) : Serializable