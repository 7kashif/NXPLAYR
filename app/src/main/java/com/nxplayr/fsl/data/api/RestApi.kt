package com.nxplayr.fsl.data.api


import com.google.gson.JsonObject
import com.nxplayr.fsl.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface RestApi {

    @FormUrlEncoded
    @POST("country/get-country-list")
    fun countryList(@Field("json") json: String): Call<List<CountryListPojo>>

    @FormUrlEncoded
    @POST("city/get-city-list")
    fun getcityList(@Field("json") json: String): Call<List<CityListPojo>>

    @FormUrlEncoded
    @POST("footballtype/get-footballtype-list")
    fun footballLevelList(@Field("json") json: String): Call<List<FootballLevelListPojo>>

    @FormUrlEncoded
    @POST("appuserrole/get-appuserrole-list")
    fun footballTypeList(@Field("json") json: String): Call<List<FootballTypeListPojo>>

    @FormUrlEncoded
    @POST("agegroup/get-agegroup-list")
    fun ageGroupList(@Field("json") json: String): Call<List<FootballAgeGroupListPojo>>


    @POST("academy/list")
    fun academyList(@Body json: JsonObject): Call<AcademyListPojo>

    @FormUrlEncoded
    @POST("appusertype/get-appusertype-list")
    fun userRoleList(@Field("json") json: String): Call<List<UserRoleListPojo>>


    //@Multipart
//    @POST("upload")
//    fun uploadAttachment(@Part filePart: MultipartBody.Part, @Part("FileField") filePath: okhttp3.RequestBody): Call<UploadImagePojo>

    @Multipart
    @POST("users/file-upload")
    fun uploadAttachment(
        @Part filePart: MultipartBody.Part,
        @Part("FilePath") filePath: RequestBody,
        @Part("json") json: RequestBody
    ): Call<List<UploadImagePojo>>

    @Multipart
    @POST("users/file-upload")
    suspend fun uploadAttachment1(
        @Part filePart: MultipartBody.Part,
        @Part("FilePath") filePath: RequestBody,
        @Part("json") json: RequestBody
    ): List<UploadImagePojo>


    //    @POST("users/user-login-mobile")
//    fun userLoginwithMobile(@Body json: JsonObject): Call<SignupPojo>


    @FormUrlEncoded
    @POST("cmspage/get-cmspage")
    fun cmsPageContent(@Field("json") json: String): Call<List<CmsPojo>>


    @FormUrlEncoded
    @POST("university/get-university-list")
    fun userUniversityListProfile(@Field("json") json: String): Call<List<UserUniversityListPojo>>

    @FormUrlEncoded
    @POST("degree/get-degree-list")
    fun userDegreeListProfile(@Field("json") json: String): Call<List<UserDegreeListPojo>>

    @FormUrlEncoded
    @POST("language/get-language-list")
    fun userLanguageListProfile(@Field("json") json: String): Call<List<LanguagesPojo>>

    @FormUrlEncoded
    @POST("profiency/get-profiency-list")
    fun userProficiencyListProfile(@Field("json") json: String): Call<List<ProficiencyPojo>>

    @FormUrlEncoded
    @POST("hashtags/get-hashtags-list")
    fun userHashtagsList(@Field("json") json: String): Call<List<HashtagListPojo>>

    @FormUrlEncoded
    @POST("userhashtags/add-userhashtags")
    fun userAddHashtagsProfile(@Field("json") json: String): Call<List<HashtagsPojo>>


    @FormUrlEncoded
    @POST("userhashtags/edit-userhashtags")
    fun userEditHashtagsProfile(@Field("json") json: String): Call<List<HashtagsPojo>>


    @FormUrlEncoded
    @POST("userhashtags/delete-userhashtags")
    fun userDeleteHashtagsProfile(@Field("json") json: String): Call<List<HashtagsPojo>>


    @FormUrlEncoded
    @POST("userhashtags/list-userhashtags")
    fun userListHashtagsProfile(@Field("json") json: String): Call<List<HashtagsPojo>>

    @FormUrlEncoded
    @POST("hobby/get-hobby-list")
    fun userHobbiesList(@Field("json") json: String): Call<List<HobbiesListPojo>>

    @FormUrlEncoded
    @POST("userhobbies/add-user-hobbies")
    fun userAddHobbiesProfile(@Field("json") json: String): Call<List<HobbiesPojo>>

    @FormUrlEncoded
    @POST("userhobbies/edit-user-hobbies")
    fun userEditHobbiesProfile(@Field("json") json: String): Call<List<HobbiesPojo>>

    @FormUrlEncoded
    @POST("userhobbies/delete-user-hobbies")
    fun userDeleteHobbiesProfile(@Field("json") json: String): Call<List<HobbiesPojo>>

    @FormUrlEncoded
    @POST("userhobbies/list-user-hobbies")
    fun userListHobbiesProfile(@Field("json") json: String): Call<List<HobbiesPojo>>

    @FormUrlEncoded
    @POST("skill/get-skill-list")
    fun userSkillListProfile(@Field("json") json: String): Call<List<SkillListPojo>>

    @FormUrlEncoded
    @POST("userskills/add-user-skill")
    fun userAddSkillProfile(@Field("json") json: String): Call<List<SkillsPojo>>

    @FormUrlEncoded
    @POST("userskills/edit-user-skill")
    fun userEditSkillProfile(@Field("json") json: String): Call<List<SkillsPojo>>

    @FormUrlEncoded
    @POST("userskills/delete-user-skill")
    fun userDeleteSkillProfile(@Field("json") json: String): Call<List<SkillsPojo>>

    @FormUrlEncoded
    @POST("userskills/list-user-skill")
    fun userSkillProfile(@Field("json") json: String): Call<List<SkillsPojo>>

    @FormUrlEncoded
    @POST("club/get-club-list")
    fun getClubList(@Field("json") json: String): Call<ClubListPojo>

    @FormUrlEncoded
    @POST("userclubs/add-userclubs")
    fun addClubList(@Field("json") json: String): Call<List<ClubListPojo>>

    @FormUrlEncoded
    @POST("userclubs/edit-userclubs")
    fun editClubList(@Field("json") json: String): Call<List<ClubListPojo>>

    @FormUrlEncoded
    @POST("userclubs/delete-userclubs")
    fun deleteClubList(@Field("json") json: String): Call<List<ClubListPojo>>

    @FormUrlEncoded
    @POST("userclubs/list-userclubs")
    fun clubList(@Field("json") json: String): Call<List<ClubListPojo>>

    @FormUrlEncoded
    @POST("footballagecategory/get-footballagecategory-list")
    fun getFootballAgeCategory(@Field("json") json: String): Call<List<FootballAgeCategoryPojo>>

    @FormUrlEncoded
    @POST("fooballlevel/get-fooballlevel-list")
    fun getFootballLevelList(@Field("json") json: String): Call<List<FootballLevelPojo>>

    @FormUrlEncoded
    @POST("userpassport/add-userpassport")
    suspend fun addUserPassport(@Field("json") json: String): List<PassportNationalityPojo>

    @FormUrlEncoded
    @POST("userpassport/edit-userpassport")
    suspend fun editUserPassport(@Field("json") json: String): List<PassportNationalityPojo>

    @FormUrlEncoded
    @POST("userpassport/delete-userpassport")
    fun deleteUserPassport(@Field("json") json: String): Call<List<PassportNationalityPojo>>

    @FormUrlEncoded
    @POST("userpassport/list-userpassport")
    fun listUserPassport(@Field("json") json: String): Call<List<PassportNationalityPojo>>

    @FormUrlEncoded
    @POST("userlocation/add-edit-user-location")
    fun addLocation(@Field("json") json: String): Call<List<AddLocationPojo>>

    @FormUrlEncoded
    @POST("user-contact/user-contact-sync")
    fun userContactSync(@Field("json") json: String): Call<List<UserContactSyncPojo>>

    @FormUrlEncoded
    @POST("user-contact/user-contact-list")
    fun getContactList(@Field("json") json: String): Call<List<ContactListPojo>>

    @FormUrlEncoded
    @POST("user-follower/user-follower")
    fun userFollowingList(@Field("json") json: String): Call<List<FollowingListPojo>>

    /* @FormUrlEncoded
     @POST("user-follower/user-follower")
     fun userFollowerList(@Field("json") json: String): Call<List<FollowingListPojo>>
 */
    @FormUrlEncoded
    @POST("user-follower/user-follower")
    fun userFollow(@Field("json") json: String): Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("user-follower/user-follower")
    fun userUnFollow(@Field("json") json: String): Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("postlike/post-like")
    fun userLikeList(@Field("json") json: String): Call<List<LikeListPojo>>

    @FormUrlEncoded
    @POST("postlike/post-like")
    fun userLike(@Field("json") json: String): Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("postlike/post-like")
    fun userUnlike(@Field("json") json: String): Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("post-report-reason/get-post-report-reason-list")
    fun getreportReasonList(@Field("json") json: String): Call<List<ReportReasonPojo>>

    @FormUrlEncoded
    @POST("user-post-report-reason/user-report-post")
    fun userPostReportReason(@Field("json") json: String): Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("user-friend/user-report")
    fun userfriendReportReason(@Field("json") json: String): Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("user-post-report-reason/userpost-comment-report")
    fun usercommentReportReason(@Field("json") json: String): Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("faq/faq-list")
    fun faqList(@Field("json") json: String): Call<List<FaqPojo>>

    @FormUrlEncoded
    @POST("faq/get-faqcategory-list")
    fun faqCategoryList(@Field("json") json: String): Call<List<FaqCategoryPojo>>


    @FormUrlEncoded
    @POST("playerposition/get-playerposition-list")
    fun playerPositionList(@Field("json") json: String): Call<List<PlayerPositionListPojo>>


    @FormUrlEncoded
    @POST("postsave/save-post")
    fun savePost(@Field("json") json: String): Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("postsave/save-post-list")
    fun savePostList(@Field("json") json: String): Call<List<SavePostListPojo>>

    @FormUrlEncoded
    @POST("postsave/remove-save-post")
    fun removesavePost(@Field("json") json: String): Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("posthide/hide-post")
    fun hidePost(@Field("json") json: String): Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("posthide/hide-post-list")
    fun hidePostList(@Field("json") json: String): Call<List<HidePostListPojo>>

    @FormUrlEncoded
    @POST("posthide/remove-post")
    fun removehidePost(@Field("json") json: String): Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("user-block/user-block")
    fun blockUserList(@Field("json") json: String): Call<List<BlockUserListPojo>>

    @FormUrlEncoded
    @POST("language/get-interface-language-list")
    fun languageList(@Field("json") json: String): Call<List<InteraceLanguageListPojo>>

    @FormUrlEncoded
    @POST("notification/get-notification-list")
    fun getNotificationList(@Field("json") json: String): Call<List<NotificationPojo>>

    @FormUrlEncoded
    @POST("notification/update-notification-read-status")
    fun updateNotification(@Field("json") json: String): Call<List<FeedListPojo>>

    @FormUrlEncoded
    @POST("notification/delete-notification")
    fun deleteNotification(@Field("json") json: String): Call<List<CommonPojo>>

    @Multipart
    @POST("apilog/file-upload-multiple")
    fun uploadAttachmentArray(
        @Part filePart: Array<MultipartBody.Part?>, /*@Part("FilePath") filePath: RequestBody,*/
        @Part("json") json: RequestBody
    ): Call<List<MultiplefileUpload>>

    @FormUrlEncoded
    @POST("users/delete-account")
    fun deleteAccount(@Field("json") json: String): Call<List<CommonPojo>>



    @FormUrlEncoded
    @POST("postviews/view-post")
    fun postview(@Field("json") json: String?): Call<List<PostViewPojo>>

    @FormUrlEncoded
    @POST("postlike/post-like")
    fun postlike(@Field("json") parameter: String?): Call<List<PostlikePojo>>

    @FormUrlEncoded
    @POST("postviews/post-view-list")
    fun postViewList(@Field("json") parameter: String?): Call<List<PostLikeViewListItem>>

    @FormUrlEncoded
    @POST("postlike/post-like")
    fun postLikeList(@Field("json") parameter: String?): Call<List<PostLikeViewListItem>>

    @FormUrlEncoded
    @POST("postcomment/post-comment-like")
    fun getPostCommentLike(@Field("json") json: String): retrofit2.Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("postcommentreply/post-comment-reply")
    fun getPostCommentReplyAdd(@Field("json") json: String): retrofit2.Call<List<ReplyComment>>

    @FormUrlEncoded
    @POST("postcomment/post-comment")
    fun coomentDelete(@Field("json") jsonLogin: String): Call<List<CommentPojo>>

    @FormUrlEncoded
    @POST("reason/get-reason-list")
    fun reasonList(@Field("json") jsonLogin: String): Call<List<ReasonList>>

    @FormUrlEncoded
    @POST("postcomment/post-comment-report")
    fun reportComment(@Field("json") jsonLogin: String): Call<List<CommentPojo>>

    @FormUrlEncoded
    @POST("postcomment/post-comment")
    fun coomentList(@Field("json") jsonLogin: String): Call<List<CommentPojo>>


    @FormUrlEncoded
    @POST("postcomment/post-comment")
    fun coomentAdd(@Field("json") jsonLogin: String): Call<List<CommentPojo>>

    @FormUrlEncoded
    @POST("postcomment/post-comment")
    fun coomentEdit(@Field("json") jsonLogin: String): Call<List<CommentPojo>>

    @FormUrlEncoded
    @POST("user-friend/user-friend-remove-connection-type")
    fun friendConnectionRemove(@Field("json") jsonLogin: String): Call<List<CommentPojo>>

    @FormUrlEncoded
    @POST("user-friend/user-friend-connection-type")
    fun addConnection(@Field("json") jsonLogin: String): Call<List<AddConnectionPojo>>

    @FormUrlEncoded
    @POST("user-friend/user-friend-remove-connection-type")
    fun removeConnecton(@Field("json") jsonLogin: String): Call<List<AddConnectionPojo>>


    @GET("v2/me")
    fun getUser(@Header("Authorization") auth: String): Call<LinkedInUserProfile>

    @GET("v2/emailAddress?q=members&projection=(elements*(handle~))")
    fun getUserEmail(@Header("Authorization") auth: String): Call<LinkedInEmailPojo>

    @FormUrlEncoded
    @POST("post/delete-post")
    fun deletePostList(@Field("json") json: String): Call<List<CommonPojo>>


    @FormUrlEncoded
    @POST("exploreralbumposts/create-sub-album")
    fun createSubAlbum(@Field("json") json: String): Call<List<CreateAlbumPojo>>

    @FormUrlEncoded
    @POST("exploreralbumposts/edit-sub-album")
    fun editSubAlbum(@Field("json") json: String): Call<List<EditAlbumDeletePojo>>

    @FormUrlEncoded
    @POST("exploreralbumposts/delete-sub-album")
    fun deleteSubAlbum(@Field("json") json: String): Call<List<CreateAlbumDeletePojo>>

    @FormUrlEncoded
    @POST("exploreralbumposts/remove-post-album")
    fun removeCollection(@Field("json") json: String): Call<List<RemoveCollectionPojo>>

    @FormUrlEncoded
    @POST("postlike/post-like")
    fun userLikePost(@Field("json") json: String): Call<List<LikePostPojo>>

    @FormUrlEncoded
    @POST("post/delete-post")
    fun deleteCollecationPost(@Field("json") json: String): Call<List<DeleteCollecationPojo>>

    @FormUrlEncoded
    @POST("contractsituations/get-contractsituations-list")
    fun getcontractsituationslist(@Field("json") json: String): Call<List<ContractSitiuation>>

    @FormUrlEncoded
    @POST("users/user-update-resume")
    fun getUpdateResume(@Field("json") json: String): Call<List<SignupPojo>>

    @FormUrlEncoded
    @POST("geomobilitys/get-geomobilitys-list")
    fun getgeomobilitys(@Field("json") json: String): Call<List<GeomobilitysPojo>>

    @FormUrlEncoded
    @POST("skill/get-skill-list")
    fun getuserSkillProfile(@Field("json") json: String): Call<List<SkillsPojo>>

    @FormUrlEncoded
    @POST("outfitters/get-outfitters-list")
    fun getuserOutfittersProfile(@Field("json") json: String): Call<List<OutfittersPojo>>

    @FormUrlEncoded
    @POST("useroutfitters/add-useroutfitters")
    fun getAddOutfittersProfile(@Field("json") json: String): Call<List<OutfittersPojo>>

    @FormUrlEncoded
    @POST("leagues/get-leagues-list")
    fun userLanguageList(@Field("json") json: String): Call<List<LeaguesListpOJO>>

    @FormUrlEncoded
    @POST("users/user-update-resume")
    fun userUpdateResumePost(@Field("json") json: String): Call<List<SignupPojo>>

    @FormUrlEncoded
    @POST("userurls/list-userurls")
    fun websiteList(@Field("json") json: String): Call<List<WebsitePojo>>

    @FormUrlEncoded
    @POST("userurls/add-userurls")
    fun addSiteList(@Field("json") json: String): Call<List<WebsitePojo>>

    @FormUrlEncoded
    @POST("userurls/delete-userurls")
    fun deleteWebsiteList(@Field("json") json: String): Call<List<WebsitePojo>>

    @FormUrlEncoded
    @POST("userurls/edit-userurls")
    fun editWebsiteList(@Field("json") json: String): Call<List<WebsitePojo>>


    @FormUrlEncoded
    @POST("users/parent-otp-resend")
    fun parentResendOTP(@Field("json") json: String): Call<List<CommonPojo>>


    @FormUrlEncoded
    @POST("suggestions/create-suggestions")
    fun suggestionsFeature(@Field("json") json: String): Call<List<CommonPojo>>


    @FormUrlEncoded
    @POST("users/check-storage")
    fun checkStorage(@Field("json") json: String): Call<List<CheckStoragePojo>>


    @FormUrlEncoded
    @POST("verificationcategory/get-verificationcategory-list")
    fun getVerificationCategory(@Field("json") json: String): Call<List<VerificationCatPojo>>


    @POST("language/translate/v2")
    fun getTranslate(
        @Body body: com.squareup.okhttp.RequestBody,
        @Header("content-type") type: String,
        @Header("accept-encoding") accept: String,
        @Header("x-rapidapi-key") rapidapi: String,
        @Header("x-rapidapi-host") host: String
    ): Call<GoogleTranslateObject>


    @FormUrlEncoded
    @POST("postcommentreply/post-comment-reply-like")
    fun getPostReplyCommentLike(@Field("json") json: String): retrofit2.Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("jobs/job-list")
    fun getJobList(@Field("json") json: String): retrofit2.Call<List<JobListPojo>>

    @FormUrlEncoded
    @POST("jobs/save-job-list")
    fun getSaveJobList(@Field("json") json: String): retrofit2.Call<List<JobListPojo>>

    @FormUrlEncoded
    @POST("jobs/save-job")
    fun getSaveJob(@Field("json") json: String): retrofit2.Call<List<JobListPojo>>

    @FormUrlEncoded
    @POST("jobs/remove-save-job")
    fun getRemoveSaveJob(@Field("json") json: String): retrofit2.Call<List<JobListPojo>>

    @FormUrlEncoded
    @POST("jobs/apply-job-list")
    fun getApplyJobList(@Field("json") json: String): retrofit2.Call<List<ApplyJoblist>>

    @FormUrlEncoded
    @POST("jobs/apply-job")
    fun getApplyJob(@Field("json") json: String): retrofit2.Call<List<ApplyJoblist>>


    /* NEW API WITH REPOSITORY */

    @FormUrlEncoded
    @POST("language/list-labels")
    suspend fun languageLabel(@Field("json") json: String): Response<MutableList<LanguageLabelPojo>>

    @FormUrlEncoded
    @POST("post/get-post-list")
    suspend fun getPostList(@Field("json") json: String): Response<MutableList<PostCreatePojo>>

    @FormUrlEncoded
    @POST("post/edit-post")
    suspend fun editPost(@Field("json") json: String): Response<MutableList<PostCreatePojo>>

    @FormUrlEncoded
    @POST("post/postshare")
    suspend fun sharePost(@Field("json") json: String): Response<MutableList<PostCreatePojo>>

    @FormUrlEncoded
    @POST("post/create-post")
    suspend fun createPost(@Field("json") json: String): Response<MutableList<PostCreatePojo>>

    @FormUrlEncoded
    @POST("post/get-post-list")
    suspend fun showExploreVideos(@Field("json") json: String): Response<MutableList<ExploreVideosPojo>>

    @FormUrlEncoded
    @POST("exploreralbumposts/get-banners")
    suspend fun showbanners(@Field("json") json: String): Response<MutableList<BannerPojo>>

    @FormUrlEncoded
    @POST("exploreralbumposts/get-albums")
    suspend fun createAlbumList(@Field("json") json: String): Response<MutableList<CreateAlbumListPojo>>

    @FormUrlEncoded
    @POST("exploreralbumposts/assign-post-album")
    suspend fun assignPostAlbum(@Field("json") json: String): Response<MutableList<AssignPostAlbumPojo>>

    @FormUrlEncoded
    @POST("exploreralbumposts/delete-album")
    suspend fun createAlbumDelete(@Field("json") json: String): Response<MutableList<CreateAlbumDeletePojo>>

    @FormUrlEncoded
    @POST("exploreralbumposts/create-album")
    suspend fun createAlbum(@Field("json") json: String): Response<MutableList<CreateAlbumPojo>>

    @FormUrlEncoded
    @POST("exploreralbumposts/edit-album")
    suspend fun editAlbum(@Field("json") json: String): Response<MutableList<EditAlbumDeletePojo>>

    @FormUrlEncoded
    @POST("user-friend/chat-list")
    suspend fun chatlist(@Field("json") json: String): Response<MutableList<ChatList>>

    @FormUrlEncoded
    @POST("users/otp-resend")
    fun resendOTP(@Field("json") json: String): Call<List<CommonPojo>>

    @FormUrlEncoded
    @POST("users/user-login-password")
    suspend fun userLoginwithEmail(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("users/user-registration")
    suspend fun userRegistration(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("users/user-update-profile-info")
    suspend fun userInfoUpdate(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("users/user-update-profile-picture")
    suspend fun userUploadProfilePicture(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("users/user-update-cover-photo")
    suspend fun userupdateCoverPhoto(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("users/change-password")
    suspend fun userChangePassword(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("users/reset-password")
    suspend fun userResetPass(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("users/user-forgot-password")
    suspend fun userForagatePass(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("users/otp-verification")
    suspend fun userVerifyOtp(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("users/change-language")
    suspend fun users_changeLanguage(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("users/update-privacy")
    suspend fun users_updatePrivacy(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("users/change-player-position")
    suspend fun changePlayerPosition(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("users/update-email-notifications")
    suspend fun updateEmailNotification(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("users/update-push-notifications")
    suspend fun updatePushNotification(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("users/update-sms-notifications")
    suspend fun updateSmsNotification(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("users/check-user-duplication")
    suspend fun checkDublication(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("users/user-social-login")
    suspend fun socialLogin(@Field("json") jsonLogin: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("users/get-other-user-profile")
    suspend fun otherUserProfile(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("users/user-parental-process")
    suspend fun profileParents(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("users/user-update-profile")
    suspend fun userUpdateProfile(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("users/parent-otp-verification")
    suspend fun parentsVerifyOtp(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("users/update-chat-id")
    suspend fun quickblockdetails(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("users/user-update-device-token")
    suspend fun updateDeviceToken(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("users/change-content-language")
    suspend fun changeContentLanguage(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("users/request-profile-verification")
    suspend fun getSendVerification(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("suggestions/create-partner-with-us")
    suspend fun getPartnerWithUs(@Field("json") json: String): Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("suggestions/create-feedback")
    suspend fun getSendFeedback(@Field("json") json: String): retrofit2.Response<MutableList<SignupPojo>>

    @FormUrlEncoded
    @POST("useremployement/add-user-employement")
    suspend fun userAddEmployementProfile(@Field("json") json: String): Response<MutableList<Employmentpojo>>

    @FormUrlEncoded
    @POST("useremployement/edit-user-employement")
    suspend fun userEditEmployementProfile(@Field("json") json: String): Response<MutableList<Employmentpojo>>

    @FormUrlEncoded
    @POST("useremployement/delete-user-employement")
    suspend fun userDeleteEmployementProfile(@Field("json") json: String): Response<MutableList<Employmentpojo>>

    @FormUrlEncoded
    @POST("useremployement/list-user-employement")
    suspend fun userListEmployementProfile(@Field("json") json: String): Response<MutableList<Employmentpojo>>

    @FormUrlEncoded
    @POST("company/get-company-list")
    suspend fun userCompanyListProfile(@Field("json") json: String): Response<MutableList<CompanyListPojo>>

    @FormUrlEncoded
    @POST("jobfunction/get-jobfunction-list")
    suspend fun userJobFunctionListProfile(@Field("json") json: String): Response<MutableList<JobFunctionPojo>>

    @FormUrlEncoded
    @POST("userlanguages/list-userlanguages")
    suspend fun userListLanguageProfile(@Field("json") json: String): Response<MutableList<ProfileLanguagePojo>>

    @FormUrlEncoded
    @POST("usereducation/list-users-education")
    suspend fun userListEducationProfile(@Field("json") json: String): Response<MutableList<EducationPojo>>

    @FormUrlEncoded
    @POST("userlanguages/delete-userlanguages")
    suspend fun userDeleteLanguageProfile(@Field("json") json: String): Response<MutableList<ProfileLanguagePojo>>

    @FormUrlEncoded
    @POST("userlanguages/edit-userlanguages")
    suspend fun userEditLanguageProfile(@Field("json") json: String): Response<MutableList<ProfileLanguagePojo>>

    @FormUrlEncoded
    @POST("userlanguages/add-userlanguages")
    suspend fun userAddLanguageProfile(@Field("json") json: String): Response<MutableList<ProfileLanguagePojo>>

    @FormUrlEncoded
    @POST("usereducation/delete-users-education")
    suspend fun userDeleteEducationProfile(@Field("json") json: String): Response<MutableList<EducationPojo>>

    @FormUrlEncoded
    @POST("usereducation/edit-users-education")
    suspend fun userEditEducationProfile(@Field("json") json: String): Response<MutableList<EducationPojo>>

    @FormUrlEncoded
    @POST("usereducation/add-users-education")
    suspend fun userAddEducationProfile(@Field("json") json: String): Response<MutableList<EducationPojo>>

    @FormUrlEncoded
    @POST("user-friend/user-friend")
    suspend fun friendList(@Field("json") json: String): Response<MutableList<FriendListPojo>>

    @FormUrlEncoded
    @POST("user-friend/user-friend-connection-type")
    suspend fun changeConnection(@Field("json") jsonLogin: String): Response<MutableList<FriendListPojo>>

    @FormUrlEncoded
    @POST("user-friend/add-chat-list")
    suspend fun chatTofriend(@Field("json") json: String): Response<MutableList<FriendListPojo>>

    @FormUrlEncoded
    @POST("connectiontype/get-connectiontype-list")
    suspend fun connectionList(@Field("json") jsonLogin: String): Response<MutableList<ConnectionTypePojo>>

    @FormUrlEncoded
    @POST("user-friend/user-friend")
    suspend fun userContactList(@Field("json") json: String): Response<MutableList<SuggestedFreindListPojo>>

}
