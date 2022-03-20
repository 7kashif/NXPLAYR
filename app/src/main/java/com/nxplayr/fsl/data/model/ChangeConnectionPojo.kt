package com.nxplayr.fsl.data.model
import com.google.gson.annotations.SerializedName


data class ChangeConnectionPojo(
    @SerializedName("counts")
    var counts: List<ConnectionsCount>,
    @SerializedName("data")
    var `data`: List<ConnecData>,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: String
)

data class ConnectionsCount(
    @SerializedName("Acquaintances")
    var acquaintances: String,
    @SerializedName("all")
    var all: List<AllCount>,
    @SerializedName("Friends")
    var friends: String,
    @SerializedName("Professionals")
    var professionals: String
)

data class ConnecData(
    @SerializedName("ConnectionType")
    var connectionType: List<ChangeConnectionType>,
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

data class AllCount(
    @SerializedName("Acquaintances")
    var acquaintances: Int,
    @SerializedName("All")
    var all: Int,
    @SerializedName("Friends")
    var friends: Int,
    @SerializedName("Professionals")
    var professionals: Int
)

data class ChangeConnectionType(
    @SerializedName("conntypeID")
    var conntypeID: String,
    @SerializedName("conntypeName")
    var conntypeName: String
)