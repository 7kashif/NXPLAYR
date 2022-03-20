package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName


data class ContactListPojo(
    @SerializedName("data")
        var `data`: List<ContactListData?>? = listOf(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
)

data class ContactListData(
        @SerializedName("IsYouFollowing")
        var isYouFollowing: String,
        @SerializedName("IsYourFriend")
        var isYourFriend: String,
        @SerializedName("TotalFriendCount")
        var totalFriendCount: String,
        @SerializedName("userFirstName")
        var userFirstName: String,
        @SerializedName("userID")
        var userID: String,
        @SerializedName("userLastName")
        var userLastName: Any,
        @SerializedName("userNickname")
        var userNickname: Any,
        @SerializedName("userProfilePicture")
        var userProfilePicture: Any,
        @SerializedName("usercontactCreatedDate")
        var usercontactCreatedDate: String,
        @SerializedName("usercontactEmail")
        var usercontactEmail: String,
        @SerializedName("usercontactFirstName")
        var usercontactFirstName: String,
        @SerializedName("usercontactFslUser")
        var usercontactFslUser: String,
        @SerializedName("usercontactID")
        var usercontactID: String,
        @SerializedName("usercontactLastName")
        var usercontactLastName: String,
        @SerializedName("usercontactPhone")
        var usercontactPhone: String,
        @SerializedName("usercontactSyncedFrom")
        var usercontactSyncedFrom: String,
        var checked: Boolean = false
)