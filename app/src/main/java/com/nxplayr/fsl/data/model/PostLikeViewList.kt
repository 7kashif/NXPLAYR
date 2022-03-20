package com.nxplayr.fsl.data.model
import com.google.gson.annotations.SerializedName

data class PostLikeViewListItem(
    @SerializedName("data")
    val `data`: List<PostLikeViewListData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)

data class PostLikeViewListData(
    @SerializedName("IsYouAreBlocked")
    val isYouAreBlocked: String,
    @SerializedName("IsYouBlocked")
    val isYouBlocked: String,
    @SerializedName("IsYouFollowing")
    var isYouFollowing: String,
    @SerializedName("IsYouLikedProfile")
    val isYouLikedProfile: String,
    @SerializedName("IsYouReceiveRequest")
    val isYouReceiveRequest: String,
    @SerializedName("IsYouSentRequest")
    val isYouSentRequest: String,
    @SerializedName("IsYourFriend")
    val isYourFriend: String,
    @SerializedName("IsYourRequestRejected")
    val isYourRequestRejected: String,
    @SerializedName("userFirstName")
    val userFirstName: String,
    @SerializedName("userID")
    val userID: String,
    @SerializedName("userLastName")
    val userLastName: String,
    @SerializedName("userProfilePicture")
    val userProfilePicture: String
)