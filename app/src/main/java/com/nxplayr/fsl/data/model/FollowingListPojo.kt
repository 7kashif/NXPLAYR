package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName

data class FollowingListPojo(
    @SerializedName("count")
        var count: List<FollowingCount> = listOf(),
    @SerializedName("data")
        var `data`: List<FollowingListData> = listOf(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
)

data class FollowingCount(
        @SerializedName("followerCount")
        var followerCount: String,
        @SerializedName("followingCount")
        var followingCount: String
)

data class FollowingListData(
        @SerializedName("cityName")
        var cityName: Any,
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
        @SerializedName("userFirstName")
        var userFirstName: String,
        @SerializedName("userID")
        var userID: String,
        @SerializedName("userLastName")
        var userLastName: String,
        @SerializedName("userProfilePicture")
        var userProfilePicture: String
)