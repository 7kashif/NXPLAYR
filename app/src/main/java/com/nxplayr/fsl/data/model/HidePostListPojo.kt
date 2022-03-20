package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName


data class HidePostListPojo(
    @SerializedName("data")
        var `data`: List<HidePostData> = listOf(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
)

data class HidePostData(
    @SerializedName("postID")
        var postID: String,
    @SerializedName("postdata")
        var postdata: List<Postdata> = listOf(),
    @SerializedName("posthideDate")
        var posthideDate: String,
    @SerializedName("posthideID")
        var posthideID: String,
    @SerializedName("userID")
        var userID: String
)

data class Postdata(
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
    @SerializedName("languageID")
        var languageID: String,
    @SerializedName("original_postCreatedDate")
        var originalPostCreatedDate: String,
    @SerializedName("originalPostID")
        var originalPostID: String,
    @SerializedName("original_userEmail")
        var originalUserEmail: String,
    @SerializedName("original_userFirstName")
        var originalUserFirstName: String,
    @SerializedName("original_userLastName")
        var originalUserLastName: String,
    @SerializedName("original_userMobile")
        var originalUserMobile: String,
    @SerializedName("original_userProfilePicture")
        var originalUserProfilePicture: String,
    @SerializedName("originaluserID")
        var originaluserID: String,
    @SerializedName("postCategory")
        var postCategory: String,
    @SerializedName("postComment")
        var postComment: String,
    @SerializedName("postCommentList")
        var postCommentList: List<PostCommentHide>,
    @SerializedName("postCreateType")
        var postCreateType: String,
    @SerializedName("postCreatedDate")
        var postCreatedDate: String,
    @SerializedName("PostCreatedMinutesAgo")
        var postCreatedMinutesAgo: String,
    @SerializedName("postDescription")
        var postDescription: String,
    @SerializedName("postDisLike")
        var postDisLike: String,
    @SerializedName("postFavorite")
        var postFavorite: String,
    @SerializedName("postFor")
        var postFor: String,
    @SerializedName("postID")
        var postID: String,
    @SerializedName("postIsSaved")
        var postIsSaved: String,
    @SerializedName("postLatitude")
        var postLatitude: String,
    @SerializedName("postLike")
        var postLike: String,
    @SerializedName("postLocation")
        var postLocation: String,
    @SerializedName("postLongitude")
        var postLongitude: String,
    @SerializedName("postMediaType")
        var postMediaType: String,
    @SerializedName("postPrivacyType")
        var postPrivacyType: String,
    @SerializedName("postRatting")
        var postRatting: String,
    @SerializedName("postSerializedData")
        var postSerializedData: List<PostSerializedHideData> = listOf(),
    @SerializedName("postShared")
        var postShared: String,
    @SerializedName("postStatus")
        var postStatus: String,
    @SerializedName("postType")
        var postType: String,
    @SerializedName("postUploadDate")
        var postUploadDate: String,
    @SerializedName("postViews")
        var postViews: String,
    @SerializedName("postWriteSomething")
        var postWriteSomething: String,
    @SerializedName("userFirstName")
        var userFirstName: String,
    @SerializedName("userID")
        var userID: String,
    @SerializedName("userLastName")
        var userLastName: String,
    @SerializedName("userProfilePicture")
        var userProfilePicture: String,
    @SerializedName("youpostLiked")
        var youpostLiked: String,
    @SerializedName("youpostShared")
        var youpostShared: String,
    @SerializedName("youpostViews")
        var youpostViews: String
)

data class PostCommentHide(
        @SerializedName("commentComment")
        var commentComment: String,
        @SerializedName("commentDate")
        var commentDate: String,
        @SerializedName("commentFile")
        var commentFile: String,
        @SerializedName("commentID")
        var commentID: String,
        @SerializedName("commentMediaType")
        var commentMediaType: String,
        @SerializedName("commentReply")
        var commentReply: String,
        @SerializedName("commentType")
        var commentType: String,
        @SerializedName("isCommentLiked")
        var isCommentLiked: String,
        @SerializedName("IsYouAreBlocked")
        var isYouAreBlocked: String,
        @SerializedName("IsYouBlocked")
        var isYouBlocked: String,
        @SerializedName("IsYouFollowing")
        var isYouFollowing: String,
        @SerializedName("IsYouReceiveRequest")
        var isYouReceiveRequest: String,
        @SerializedName("IsYouSentRequest")
        var isYouSentRequest: String,
        @SerializedName("IsYourFriend")
        var isYourFriend: String,
        @SerializedName("IsYourRequestRejected")
        var isYourRequestRejected: String,
        @SerializedName("MinutesAgo")
        var minutesAgo: String,
        @SerializedName("postComment")
        var postComment: String,
        @SerializedName("postID")
        var postID: String,
        @SerializedName("postcommentLike")
        var postcommentLike: String,
        @SerializedName("postcommentrecentlikedusers")
        var postcommentrecentlikedusers: Any,
        @SerializedName("postcommentreply")
        var postcommentreply: List<Any>,
        @SerializedName("userFirstName")
        var userFirstName: String,
        @SerializedName("userID")
        var userID: String,
        @SerializedName("userLastName")
        var userLastName: String,
        @SerializedName("userProfilePicture")
        var userProfilePicture: String
)

data class PostSerializedHideData(
        @SerializedName("albumName")
        var albumName: String,
        @SerializedName("albumType")
        var albumType: String,
        @SerializedName("albummedia")
        var albummedia: List<AlbumHidemedia>
)

data class AlbumHidemedia(
        @SerializedName("albummediaFile")
        var albummediaFile: String,
        @SerializedName("albummediaFileType")
        var albummediaFileType: String,
        @SerializedName("albummediaThumbnail")
        var albummediaThumbnail: String
)