package com.nxplayr.fsl.data.model
import com.google.gson.annotations.SerializedName

data class ChatList(
    @SerializedName("count")
    val count: List<ChatCount>?,
    @SerializedName("data")
    val `data`: List<ChatListData>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?
)

data class  ChatCount(
    @SerializedName("count")
    val count: Int?
)

data class  ChatListData(
    @SerializedName("IsYouAreBlocked")
    val isYouAreBlocked: String?,
    @SerializedName("IsYouBlocked")
    val isYouBlocked: String?,
    @SerializedName("userFirstName")
    val userFirstName: String?,
    @SerializedName("userID")
    val userID: String?,
    @SerializedName("userLastName")
    val userLastName: String?,
    @SerializedName("userProfilePicture")
    val userProfilePicture: String?,
    @SerializedName("userQBoxID")
    val userQBoxID: String?
)