package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class PostCreatePojo(
    @SerializedName("data")
    var `data`: List<CreatePostData?>? = listOf(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: String = ""
) : Serializable

data class CreatePostData(
    @SerializedName("IsYouAreBlocked")
    var isYouAreBlocked: String = "",
    @SerializedName("IsYouBlocked")
    var isYouBlocked: String = "",
    @SerializedName("IsYouFollowing")
    var isYouFollowing: String = "",
    @SerializedName("IsYouLikedProfile")
    var isYouLikedProfile: String = "",
    @SerializedName("IsYouReceiveRequest")
    var isYouReceiveRequest: String = "",
    @SerializedName("IsYouSentRequest")
    var isYouSentRequest: String = "",
    @SerializedName("IsYourFriend")
    var isYourFriend: String = "",
    @SerializedName("IsYourRequestRejected")
    var isYourRequestRejected: String = "",
    @SerializedName("languageID")
    var languageID: String = "",
    @SerializedName("original_postCreatedDate")
    var originalPostCreatedDate: String = "",
    @SerializedName("originalPostID")
    var originalPostID: String = "",
    @SerializedName("original_userEmail")
    var originalUserEmail: String = "",
    @SerializedName("original_userFirstName")
    var originalUserFirstName: String = "",
    @SerializedName("original_userLastName")
    var originalUserLastName: String = "",
    @SerializedName("original_userMobile")
    var originalUserMobile: String = "",
    @SerializedName("original_userProfilePicture")
    var originalUserProfilePicture: String = "",
    @SerializedName("originaluserID")
    var originaluserID: String = "",
    @SerializedName("postCategory")
    var postCategory: String = "",
    @SerializedName("postComment")
    var postComment: String = "",
    @SerializedName("postCommentList")
    var postCommentList: ArrayList<CommentData>,
    @SerializedName("postCreateType")
    var postCreateType: String = "",
    @SerializedName("postCreatedDate")
    var postCreatedDate: String = "",
    @SerializedName("PostCreatedMinutesAgo")
    var postCreatedMinutesAgo: String = "",
    @SerializedName("postDescription")
    var postDescription: String = "",
    @SerializedName("postDisLike")
    var postDisLike: String = "",
    @SerializedName("postFavorite")
    var postFavorite: String = "",
    @SerializedName("postFor")
    var postFor: String = "",
    @SerializedName("postID")
    var postID: String = "",
    @SerializedName("postLatitude")
    var postLatitude: String = "",
    @SerializedName("postLike")
    var postLike: String = "",
    @SerializedName("postLongitude")
    var postLongitude: String = "",
    @SerializedName("postMediaType")
    var postMediaType: String = "",
    @SerializedName("postPrivacyType")
    var postPrivacyType: String = "",
    @SerializedName("connectionTypeIDs")
    var connectionTypeIDs: String = "",
    @SerializedName("postRatting")
    var postRatting: String = "",
    @SerializedName("postSerializedData")
    var postSerializedData: ArrayList<PostSerializedData>,
    @SerializedName("postShared")
    var postShared: String = "",
    @SerializedName("postStatus")
    var postStatus: String = "",
    @SerializedName("postType")
    var postType: String = "",
    @SerializedName("postUploadDate")
    var postUploadDate: String = "",
    @SerializedName("postViews")
    var postViews: String = "",
    @SerializedName("postLocation")
    var postLocation: String = "",
    @SerializedName("userFirstName")
    var userFirstName: String = "",
    @SerializedName("userID")
    var userID: String = "",
    @SerializedName("userLastName")
    var userLastName: String = "",
    @SerializedName("userProfilePicture")
    var userProfilePicture: String = "",
    @SerializedName("youpostLiked")
    var youpostLiked: String = "",
    @SerializedName("youpostShared")
    var youpostShared: String = "",
    @SerializedName("youpostViews")
    var youpostViews: String = "",
    @SerializedName("postWriteSomething")
    var postWriteSomething: String = "",
    @SerializedName("agegroupID")
    var agegroupID: String = "",
    @SerializedName("footballagecatID")
    var footballagecatID: String = "",
    @SerializedName("footbllevelID")
    var footbllevelID: String = "",
    @SerializedName("footbltypeID")
    var footbltypeID: String = "",
    @SerializedName("orgPostPostCreatedMinutesAgo")
    var orgPostPostCreatedMinutesAgo: String,
    @SerializedName("plyrposiID")
    var plyrposiID: String = "",
    @SerializedName("postAlbum")
    var postAlbum: ArrayList<PostALbum>,
    @SerializedName("postIsSaved")
    var postIsSaved: String = "",
    @SerializedName("posthashtag")
    var posthashtag: String,
    @SerializedName("userGender")
    var userGender: String = "",
    @SerializedName("userHomeCountryID")
    var userHomeCountryID: String = "",
    @SerializedName("postLanguage")
    var postLanguage: String = "",
    var isListView: Boolean = false
) : Serializable

data class PostSerialized(
    @SerializedName("albumName")
    var albumName: String = "",
    @SerializedName("albumType")
    var albumType: String = "",
    @SerializedName("albummedia")
    var albummedia: List<AlbummediaData>
) : Serializable

data class AlbummediaData(
    @SerializedName("albummediaFile")
    var albummediaFile: String = "",
    @SerializedName("albummediaFileType")
    var albummediaFileType: String = "",
    @SerializedName("albummediaThumbnail")
    var albummediaThumbnail: String = ""
) : Serializable
