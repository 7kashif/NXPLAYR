package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SuggestedFreindListPojo(
    @SerializedName("count")
        var count: List<SuggestedFriendCount?>?= listOf(),
    @SerializedName("data")
        var `data`: List<SuggestedFriendData?>? = listOf(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
):Serializable

data class SuggestedFriendCount(
        @SerializedName("Acquaintances")
        var acquaintances: String,
        @SerializedName("All")
        var all: String,
        @SerializedName("Friends")
        var friends: Int,
        @SerializedName("IsYouAreBlocked")
        var isYouAreBlocked: String,
        @SerializedName("IsYouBlocked")
        var isYouBlocked: String,
        @SerializedName("IsYouFollowing")
        var isYouFollowing: String,
        @SerializedName("IsYouSentRequest")
        var isYouSentRequest: String,
        @SerializedName("IsYourFriend")
        var isYourFriend: String,
        @SerializedName("IsYourRequestRejected")
        var isYourRequestRejected: String,
        @SerializedName("Professionals")
        var professionals: Int,
        @SerializedName("TotalFriendCount")
        var totalFriendCount: String,
        @SerializedName("userFirstName")
        var userFirstName: String,
        @SerializedName("userHomeCityName")
        var userHomeCityName: Any,
        @SerializedName("userID")
        var userID: String,
        @SerializedName("userLastName")
        var userLastName: String,
        @SerializedName("userProfilePicture")
        var userProfilePicture: String
):Serializable

data class SuggestedFriendData(
        @SerializedName("IsYouAreBlocked")
        var isYouAreBlocked: String,
        @SerializedName("IsYouBlocked")
        var isYouBlocked: String,
        @SerializedName("IsYouFollowing")
        var isYouFollowing: String,
        @SerializedName("IsYouSentRequest")
        var isYouSentRequest: String,
        @SerializedName("IsYourFriend")
        var isYourFriend: String,
        @SerializedName("IsYourRequestRejected")
        var isYourRequestRejected: String,
        @SerializedName("TotalFriendCount")
        var totalFriendCount: String,
        @SerializedName("userCountryCode")
        var userCountryCode: String,
        @SerializedName("userFirstName")
        var userFirstName: String,
        @SerializedName("userHomeCityName")
        var userHomeCityName: Any,
        @SerializedName("userID")
        var userID: String,
        @SerializedName("userLastName")
        var userLastName: String,
        @SerializedName("userMobile")
        var userMobile: String,
        @SerializedName("userProfilePicture")
        var userProfilePicture: String
):Serializable