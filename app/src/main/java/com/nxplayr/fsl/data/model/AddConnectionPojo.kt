package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName


data class AddConnectionPojo(
    @SerializedName("counts")
        var counts: List<Count> = listOf(),
    @SerializedName("data")
        var `data`: List<ConnectionData> = listOf(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
)

data class Count(
        @SerializedName("Acquaintances")
        var acquaintances: String,
        @SerializedName("all")
        var all: String,
        @SerializedName("Family or Cousins")
        var familyOrCousins: String,
        @SerializedName("Friends")
        var friends: String,
        @SerializedName("Professional")
        var professional: String
)

data class ConnectionData(
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
        @SerializedName("userFirstName")
        var userFirstName: String,
        @SerializedName("userID")
        var userID: String,
        @SerializedName("userLastName")
        var userLastName: String,
        @SerializedName("userProfilePicture")
        var userProfilePicture: String
)