package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName

data class LikeListPojo(
    @SerializedName("data")
        var `data`: List<LikeListData>,
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
)

data class LikeListData(
        @SerializedName("IsYouAreBlocked")
        var isYouAreBlocked: String,
        @SerializedName("IsYouBlocked")
        var isYouBlocked: String,
        @SerializedName("IsYouFollowing")
        var isYouFollowing: String,
        @SerializedName("IsYouLikedProfile")
        var isYouLikedProfile: String,
        @SerializedName("IsYouReceiveRequest")
        var isYouReceiveRequest: String,
        @SerializedName("IsYouSentRequest")
        var isYouSentRequest: String,
        @SerializedName("IsYourFriend")
        var isYourFriend: String,
        @SerializedName("IsYourRequestRejected")
        var isYourRequestRejected: String,
        @SerializedName("userFirstName")
        var userFirstName: String,
        @SerializedName("userID")
        var userID: String,
        @SerializedName("userLastName")
        var userLastName: String,
        @SerializedName("userProfilePicture")
        var userProfilePicture: String
)