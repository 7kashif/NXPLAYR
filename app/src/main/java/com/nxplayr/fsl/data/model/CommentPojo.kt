package com.nxplayr.fsl.data.model


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CommentPojo(
    @SerializedName("data")
    val `data`: List<CommentData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("commentcount")
    val commentcount: String?
) : Serializable

data class CommentData(
    @SerializedName("commentComment")
    val commentComment: String,
    @SerializedName("commentDate")
    val commentDate: String,
    @SerializedName("commentFile")
    val commentFile: String,
    @SerializedName("commentID")
    val commentID: String,
    @SerializedName("commentMediaType")
    val commentMediaType: String,
    @SerializedName("commentReply")
    val commentReply: String,
    @SerializedName("commentType")
    val commentType: String,
    @SerializedName("isCommentLiked")
    var isCommentLiked: String,
    @SerializedName("IsYouAreBlocked")
    val isYouAreBlocked: String,
    @SerializedName("IsYouBlocked")
    val isYouBlocked: String,
    @SerializedName("IsYouFollowing")
    val isYouFollowing: String,
    @SerializedName("IsYouReceiveRequest")
    val isYouReceiveRequest: String,
    @SerializedName("IsYouSentRequest")
    val isYouSentRequest: String,
    @SerializedName("IsYourFriend")
    val isYourFriend: String,
    @SerializedName("IsYourRequestRejected")
    val isYourRequestRejected: String,
    @SerializedName("MinutesAgo")
    val minutesAgo: String,
    @SerializedName("postComment")
    val postComment: String,
    @SerializedName("postID")
    val postID: String,
    @SerializedName("postcommentLike")
    var postcommentLike: String,
    @SerializedName("postcommentrecentlikedusers")
    val postcommentrecentlikedusers: Any,
    @SerializedName("postcommentreply")
    var postcommentreply: ArrayList<Postcommentreply>,
    @SerializedName("userFirstName")
    val userFirstName: String,
    @SerializedName("userID")
    val userID: String,
    @SerializedName("userLastName")
    val userLastName: String,
    @SerializedName("userProfilePicture")
    val userProfilePicture: String,
    var isVisibleComment: Boolean,
    @SerializedName("userQBoxID")
    val userQBoxID: String?

) : Serializable




