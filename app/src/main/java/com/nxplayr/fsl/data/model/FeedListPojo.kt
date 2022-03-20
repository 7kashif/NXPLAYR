package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class FeedListPojo(
    @SerializedName("data")
    val `data`: List<TrendingFeedDatum>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
): Serializable

data class TrendingFeedDatum(
    @SerializedName("IsYouAreBlocked")
    val isYouAreBlocked: String,
    @SerializedName("IsYouBlocked")
    val isYouBlocked: String,
    @SerializedName("IsYouFollowing")
    val isYouFollowing: String,
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
    @SerializedName("languageID")
    val languageID: String,
    @SerializedName("original_postCreatedDate")
    val originalPostCreatedDate: String,
    @SerializedName("originalPostID")
    val originalPostID: String,
    @SerializedName("original_userEmail")
    val originalUserEmail: String,
    @SerializedName("original_userFirstName")
    val originalUserFirstName: String,
    @SerializedName("original_userLastName")
    val originalUserLastName: String,
    @SerializedName("original_userMobile")
    val originalUserMobile: String,
    @SerializedName("original_userProfilePicture")
    val originalUserProfilePicture: String,
    @SerializedName("originaluserID")
    val originaluserID: String,
    @SerializedName("postCategory")
    val postCategory: String,
    @SerializedName("postComment")
    val postComment: String,
    @SerializedName("postCommentList")
    val postCommentList: List<PostComment>,
    @SerializedName("postCreateType")
    val postCreateType: String,
    @SerializedName("postCreatedDate")
    val postCreatedDate: String,
    @SerializedName("PostCreatedMinutesAgo")
    val postCreatedMinutesAgo: String,
    @SerializedName("postDescription")
    val postDescription: String,
    @SerializedName("postDisLike")
    val postDisLike: String,
    @SerializedName("postFavorite")
    val postFavorite: String,
    @SerializedName("postFor")
    val postFor: String,
    @SerializedName("postID")
    val postID: String,
    @SerializedName("postLatitude")
    val postLatitude: String,
    @SerializedName("postLike")
    val postLike: String,
    @SerializedName("postLongitude")
    val postLongitude: String,
    @SerializedName("postMediaType")
    val postMediaType: String,
    @SerializedName("postPrivacyType")
    val postPrivacyType: String,
    @SerializedName("postRatting")
    val postRatting: String,
    @SerializedName("postSerializedData")
    val postSerializedData: List<PostSerializedData>,
    @SerializedName("postShared")
    val postShared: String,
    @SerializedName("postStatus")
    val postStatus: String,
    @SerializedName("postType")
    val postType: String,
    @SerializedName("postUploadDate")
    val postUploadDate: String,
    @SerializedName("postViews")
    val postViews: String,
    @SerializedName("userFirstName")
    val userFirstName: String,
    @SerializedName("userID")
    val userID: String,
    @SerializedName("userLastName")
    val userLastName: String,
    @SerializedName("userProfilePicture")
    val userProfilePicture: String,
    @SerializedName("youpostLiked")
    val youpostLiked: String,
    @SerializedName("youpostShared")
    val youpostShared: String,
    @SerializedName("youpostViews")
    val youpostViews: String
): Serializable

data class PostComment(
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
    val isCommentLiked: String,
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
    val postcommentLike: String,
    @SerializedName("postcommentrecentlikedusers")
    val postcommentrecentlikedusers: Any,
    @SerializedName("postcommentreply")
    val postcommentreply: List<Postcommentreply>,
    @SerializedName("userFirstName")
    val userFirstName: String,
    @SerializedName("userID")
    val userID: String,
    @SerializedName("userLastName")
    val userLastName: String,
    @SerializedName("userProfilePicture")
    val userProfilePicture: String
): Serializable

data class Postcommentreply(
    @SerializedName("commentID")
    val commentID: String,
    @SerializedName("commentReply")
    val commentReply: String,
    @SerializedName("commentreplyDate")
    val commentreplyDate: String,
    @SerializedName("commentreplyFile")
    val commentreplyFile: String,
    @SerializedName("commentreplyID")
    val commentreplyID: String,
    @SerializedName("commentreplyMediaType")
    val commentreplyMediaType: String,
    @SerializedName("commentreplyReply")
    var commentreplyReply: String,
    @SerializedName("commentreplyType")
    val commentreplyType: String,
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
    @SerializedName("postID")
    val postID: String,
    @SerializedName("userFirstName")
    val userFirstName: String,
    @SerializedName("userID")
    val userID: String,
    @SerializedName("userLastName")
    val userLastName: String,
    @SerializedName("userProfilePicture")
    val userProfilePicture: String,
    @SerializedName("isCommentLiked")
    var isCommentLiked: String?,
    @SerializedName("replylikedCount")
    var replylikedCount: String?
): Serializable


data class PostSerializedData(
        @SerializedName("albumName")
        val albumName: String = "",
        @SerializedName("albumType")
        val albumType: String = "",
        @SerializedName("albummedia")
        val albummedia: List<Albummedia>
) : Serializable

data class Albummedia(
        @SerializedName("albummediaFile")
        val albummediaFile: String = "",
        @SerializedName("albummediaThumbnail")
        val albummediaThumbnail: String = "",
        @SerializedName("albummediaFileSize")
        val albummediaFileSize: String = "",
        var duration: Long = 0,
        var isEditPost: Boolean = false,
        var isPlaying: Boolean = false
) : Serializable

data class Posttag(
        @SerializedName("postID")
        val postID: String = "",
        @SerializedName("posttagID")
        val posttagID: String = "",
        @SerializedName("userFirstName")
        val userFirstName: String = "",
        @SerializedName("userID")
        val userID: String = "",
        @SerializedName("userLastName")
        val userLastName: String = "",
        @SerializedName("userMentionID")
        val userMentionID: String = ""
) : Serializable