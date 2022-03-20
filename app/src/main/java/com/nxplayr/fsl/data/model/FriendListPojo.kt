package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName


data class FriendListPojo(
    @SerializedName("count")
        var count: List<FriendCount> = listOf(),
    @SerializedName("data")
        var `data`: List<FriendListData?>? = listOf(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String,
    @SerializedName("counts")
        val counts: List<Counts>?,

    )

data class FriendCount(
        @SerializedName("Acquaintances")
        var acquaintances: String,
        @SerializedName("All")
        var all: Int,
        @SerializedName("Friends")
        var friends: Int,
        @SerializedName("Professionals")
        var professionals: Int,
        @SerializedName("pendingCount")
        var pendingCount: String,
        @SerializedName("sentCount")
        var sentCount: String,
        @SerializedName("Family or Cousins")
        var familyOrCousins: String,
        )

data class FriendListData(
    @SerializedName("ConnectionType")
        var connectionType: List<ConnectionType>,
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
        var userProfilePicture: String,

    @SerializedName("cityName")
        var cityName: Any,
    @SerializedName("userEmail")
        var userEmail: String,
    @SerializedName("userFullname")
        var userFullname: String,
    @SerializedName("useremployementID")
        var useremployementID: Any,
    @SerializedName("userfriendReceiverID")
        var userfriendReceiverID: String,
    @SerializedName("userfriendSenderID")
        var userfriendSenderID: String,
    var checked: Boolean = false,
    @SerializedName("userQBoxID")
        val userQBoxID: String?,
)


data class ConnectionType(
        @SerializedName("conntypeID")
        var conntypeID: String?,
        @SerializedName("conntypeName")
        var conntypeName: String?
)

data class Counts(
    @SerializedName("Acquaintances")
        val acquaintances: String?,
    @SerializedName("all")
        val all: List<All>?,
    @SerializedName("Friends")
        val friends: String?,
    @SerializedName("Professionals")
        val professionals: String?,
)


data class All(
        @SerializedName("Acquaintances")
        val acquaintances: Int?,
        @SerializedName("All")
        val all: Int?,
        @SerializedName("Friends")
        val friends: String?,
        @SerializedName("Professionals")
        val professionals: String?
)

