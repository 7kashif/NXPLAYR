package com.nxplayr.fsl.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class LanguageLabelPojo : Serializable{

    @SerializedName("status")
    @Expose
    val status: Boolean?=false
    @SerializedName("info")
    @Expose
    var info: String? = ""
    @SerializedName("data")
    @Expose
    var data: List<LanguageLabelData>? = listOf()

    inner class LanguageLabelData : Serializable {

        @SerializedName("lng13")
        @Expose
        val lng13: String?=""
        @SerializedName("lng15")
        @Expose
        val lng15: String?=""
        @SerializedName("lng17")
        @Expose
        val lng17: String?=""
        @SerializedName("lng19")
        @Expose
        val lng19: String?=""
        @SerializedName("lng21")
        @Expose
        val lng21: String?=""
        @SerializedName("lng21plus")
        @Expose
        val lng21plus: String?=""
        @SerializedName("lngAboutGroup")
        @Expose
        val lngAboutGroup: String?=""
        @SerializedName("lngAboutThisGroup")
        @Expose
        val lngAboutThisGroup: String?=""
        @SerializedName("lngAboutUs")
        @Expose
        val lngAboutUs: String?=""
        @SerializedName("lngAccept")
        @Expose
        val lngAccept: String?=""
        @SerializedName("lngAcceptTC")
        @Expose
        val lngAcceptTC: String?=""
        @SerializedName("lngAdd")
        @Expose
        val lngAdd: String?=""
        @SerializedName("lngAddAlbum")
        @Expose
        val lngAddAlbum: String?=""
        @SerializedName("lngAddAttach")
        @Expose
        val lngAddAttach: String?=""
        @SerializedName("lngAddConnection")
        @Expose
        val lngAddConnection: String?=""
        @SerializedName("lngAddConnections")
        @Expose
        val lngAddConnections: String?=""
        @SerializedName("lngAddContactInformation")
        @Expose
        val lngAddContactInformation: String?=""
        @SerializedName("lngAddEducation")
        @Expose
        val lngAddEducation: String?=""
        @SerializedName("lngAddExperience")
        @Expose
        val lngAddExperience: String?=""
        @SerializedName("lngAddHashtag")
        @Expose
        val lngAddHashtag: String?=""
        @SerializedName("lngAddHashtags")
        @Expose
        val lngAddHashtags: String?=""
        @SerializedName("lngAddHobbies")
        @Expose
        val lngAddHobbies: String?=""
        @SerializedName("lngAddInterests")
        @Expose
        val lngAddInterests: String?=""
        @SerializedName("lngAddLanguages")
        @Expose
        val lngAddLanguages: String?=""
        @SerializedName("lngAddMedia")
        @Expose
        val lngAddMedia: String?=""
        @SerializedName("lngAddNetwork")
        @Expose
        val lngAddNetwork: String?=""
        @SerializedName("lngAddOrLinkToExternaldoc")
        @Expose
        val lngAddOrLinkToExternaldoc: String?=""
        @SerializedName("lngAddPhoto")
        @Expose
        val lngAddPhoto: String?=""
        @SerializedName("lngAddPlace")
        @Expose
        val lngAddPlace: String?=""
        @SerializedName("lngAddPlacePlaceHolder")
        @Expose
        val lngAddPlacePlaceHolder: String?=""
        @SerializedName("lngAddRecommendations")
        @Expose
        val lngAddRecommendations: String?=""
        @SerializedName("lngAddSkillsEndorsements")
        @Expose
        val lngAddSkillsEndorsements: String?=""
        @SerializedName("lngAddTo")
        @Expose
        val lngAddTo: String?=""
        @SerializedName("lngAddToAlbum")
        @Expose
        val lngAddToAlbum: String?=""
        @SerializedName("lngAddToCollection")
        @Expose
        val lngAddToCollection: String?=""
        @SerializedName("lngAddVideo")
        @Expose
        val lngAddVideo: String?=""
        @SerializedName("lngAddWishList")
        @Expose
        val lngAddWishList: String?=""
        @SerializedName("lngAdmins")
        @Expose
        val lngAdmins: String?=""
        @SerializedName("lngAgeGroup")
        @Expose
        val lngAgeGroup: String?=""
        @SerializedName("lngAgeGroupeDetail")
        @Expose
        val lngAgeGroupeDetail: String?=""
        @SerializedName("lngAgeGroupeQue")
        @Expose
        val lngAgeGroupeQue: String?=""
        @SerializedName("lngAgent")
        @Expose
        val lngAgent: String?=""
        @SerializedName("lngAlbumName")
        @Expose
        val lngAlbumName: String?=""
        @SerializedName("lngAlbums")
        @Expose
        val lngAlbums: String?=""
        @SerializedName("lngAll")
        @Expose
        val lngAll: String?=""
        @SerializedName("lngAllPostType")
        @Expose
        val lngAllPostType: String?=""
        @SerializedName("lngAllowConnectRequests")
        @Expose
        val lngAllowConnectRequests: String?=""
        @SerializedName("lngAllowPeopleToFollowMe")
        @Expose
        val lngAllowPeopleToFollowMe: String?=""
        @SerializedName("lngAlternativeEmailAddress")
        @Expose
        val lngAlternativeEmailAddress: String?=""
        @SerializedName("lngAlumni")
        @Expose
        val lngAlumni: String?=""
        @SerializedName("lngAnd")
        @Expose
        val lngAnd: String?=""
        @SerializedName("lngAppleLoginOSValidation")
        @Expose
        val lngAppleLoginOSValidation: String?=""
        @SerializedName("lngApply")
        @Expose
        val lngApply: String?=""
        @SerializedName("lngApplyInsta")
        @Expose
        val lngApplyInsta: String?=""
        @SerializedName("lngApplyInstaDetail")
        @Expose
        val lngApplyInstaDetail: String?=""
        @SerializedName("lngAreYouSure")
        @Expose
        val lngAreYouSure: String?=""
        @SerializedName("lngAt")
        @Expose
        val lngAt: String?=""
        @SerializedName("lngBasicDetail")
        @Expose
        val lngBasicDetail: String?=""
        @SerializedName("lngBestFootFeet")
        @Expose
        val lngBestFootFeet: String?=""
        @SerializedName("lngBio")
        @Expose
        val lngBio: String?=""
        @SerializedName("lngBirthPlace")
        @Expose
        val lngBirthPlace: String?=""
        @SerializedName("lngBlockUser")
        @Expose
        val lngBlockUser: String?=""
        @SerializedName("lngBlocked")
        @Expose
        val lngBlocked: String?=""
        @SerializedName("lngBlockedUsers")
        @Expose
        val lngBlockedUsers: String?=""
        @SerializedName("lngCB")
        @Expose
        val lngCB: String?=""
        @SerializedName("lngCF")
        @Expose
        val lngCF: String?=""
        @SerializedName("lngCM")
        @Expose
        val lngCM: String?=""
        @SerializedName("lngCamera")
        @Expose
        val lngCamera: String?=""
        @SerializedName("lngCancel")
        @Expose
        val lngCancel: String?=""
        @SerializedName("lngAgentName")
        @Expose
        val lngAgentName: String?=""
        @SerializedName("lngCancelRequest")
        @Expose
        val lngCancelRequest: String?=""
        @SerializedName("lngCaps")
        @Expose
        val lngCaps: String?=""
        @SerializedName("lngCategory")
        @Expose
        val lngCategory: String?=""
        @SerializedName("lngCategoryDetail")
        @Expose
        val lngCategoryDetail: String?=""
        @SerializedName("lngCategoryQue")
        @Expose
        val lngCategoryQue: String?=""
        @SerializedName("lngChange")
        @Expose
        val lngChange: String?=""
        @SerializedName("lngChangePassword")
        @Expose
        val lngChangePassword: String?=""
        @SerializedName("lngChangeRole")
        @Expose
        val lngChangeRole: String?=""
        @SerializedName("lngChannel")
        @Expose
        val lngChannel: String?=""
        @SerializedName("lngChat")
        @Expose
        val lngChat: String?=""
        @SerializedName("lngCheckNoDataFound")
        @Expose
        val lngCheckNoDataFound: String?=""
        @SerializedName("lngCheckNoInternet")
        @Expose
        val lngCheckNoInternet: String?=""
        @SerializedName("lngCheckSomthingWrong")
        @Expose
        val lngCheckSomthingWrong: String?=""
        @SerializedName("lngChooseFile")
        @Expose
        val lngChooseFile: String?=""
        @SerializedName("lngCity")
        @Expose
        val lngCity: String?=""
        @SerializedName("lngClearAll")
        @Expose
        val lngClearAll: String?=""
        @SerializedName("lngClose")
        @Expose
        val lngClose: String?=""
        @SerializedName("lngClubsSuggestions")
        @Expose
        val lngClubsSuggestions: String?=""
        @SerializedName("lngCm")
        @Expose
        val lngCm: String?=""
        @SerializedName("lngCollections")
        @Expose
        val lngCollections: String?=""
        @SerializedName("lngComment")
        @Expose
        val lngComment: String?=""
        @SerializedName("lngCommentLikes")
        @Expose
        val lngCommentLikes: String?=""
        @SerializedName("lngCommentReplyLikes")
        @Expose
        val lngCommentReplyLikes: String?=""
        @SerializedName("lngComments")
        @Expose
        val lngComments: String?=""
        @SerializedName("lngCommunityGuidelines")
        @Expose
        val lngCommunityGuidelines: String?=""
        @SerializedName("lngCompanies")
        @Expose
        val lngCompanies: String?=""
        @SerializedName("lngCompanyName")
        @Expose
        val lngCompanyName: String?=""
        @SerializedName("lngConfirmPassword")
        @Expose
        val lngConfirmPassword: String?=""
        @SerializedName("lngConnect")
        @Expose
        val lngConnect: String?=""
        @SerializedName("lngConnectCreatePost")
        @Expose
        val lngConnectCreatePost: String?=""
        @SerializedName("lngConnectDisconnect")
        @Expose
        val lngConnectDisconnect: String?=""
        @SerializedName("lngConnection")
        @Expose
        val lngConnection: String?=""
        @SerializedName("lngConnections")
        @Expose
        val lngConnections: String?=""
        @SerializedName("lngConnectionsPosts")
        @Expose
        val lngConnectionsPosts: String?=""
        @SerializedName("lngConnectionsSelected")
        @Expose
        val lngConnectionsSelected: String?=""
        @SerializedName("lngContactInformation")
        @Expose
        val lngContactInformation: String?=""
        @SerializedName("lngContactSync")
        @Expose
        val lngContactSync: String?=""
        @SerializedName("lngContactUs")
        @Expose
        val lngContactUs: String?=""
        @SerializedName("lngContentLanguage")
        @Expose
        val lngContentLanguage: String?=""
        @SerializedName("lngSelectContentLanguage")
        @Expose
        val lngSelectContentLanguage: String?=""
        @SerializedName("lngContinent")
        @Expose
        val lngContinent: String?=""
        @SerializedName("lngContinue")
        @Expose
        val lngContinue: String?=""
        @SerializedName("lngCopy")
        @Expose
        val lngCopy: String?=""
        @SerializedName("lngCopyrightPolicy")
        @Expose
        val lngCopyrightPolicy: String?=""
        @SerializedName("lngCountry")
        @Expose
        val lngCountry: String?=""
        @SerializedName("lngCreateAc")
        @Expose
        val lngCreateAc: String?=""
        @SerializedName("lngCreateEvent")
        @Expose
        val lngCreateEvent: String?=""
        @SerializedName("lngCreatePost")
        @Expose
        val lngCreatePost: String?=""
        @SerializedName("lngCreateProfileDetail")
        @Expose
        val lngCreateProfileDetail: String?=""
        @SerializedName("lngCreateProfileQue")
        @Expose
        val lngCreateProfileQue: String?=""
        @SerializedName("lngCurrentCity")
        @Expose
        val lngCurrentCity: String?=""
        @SerializedName("lngCurrentClub")
        @Expose
        val lngCurrentClub: String?=""
        @SerializedName("lngCurrentCountry")
        @Expose
        val lngCurrentCountry: String?=""
        @SerializedName("lngCurrentCountryLocation")
        @Expose
        val lngCurrentCountryLocation: String?=""
        @SerializedName("lngCurrentLocation")
        @Expose
        val lngCurrentLocation: String?=""
        @SerializedName("lngCurrentPassword")
        @Expose
        val lngCurrentPassword: String?=""
        @SerializedName("lngCurrentSkill")
        @Expose
        val lngCurrentSkill: String?=""
        @SerializedName("lngMaxSkillsMessage")
        @Expose
        val lngMaxSkillsMessage: String?=""
        @SerializedName("lngDOB")
        @Expose
        val lngDOB: String?=""
        @SerializedName("lngDate")
        @Expose
        val lngDate: String?=""
        @SerializedName("lngDecline")
        @Expose
        val lngDecline: String?=""
        @SerializedName("lngDegree")
        @Expose
        val lngDegree: String?=""
        @SerializedName("lngDelete")
        @Expose
        val lngDelete: String?=""
        @SerializedName("lngDeleteAccount")
        @Expose
        val lngDeleteAccount: String?=""
        @SerializedName("lngDeleteAccountMsg")
        @Expose
        val lngDeleteAccountMsg: String?=""
        @SerializedName("lngDeleteChatTitle")
        @Expose
        val lngDeleteChatTitle: String?=""
        @SerializedName("lngDeleteConversation")
        @Expose
        val lngDeleteConversation: String?=""
        @SerializedName("lngDeleteEducation")
        @Expose
        val lngDeleteEducation: String?=""
        @SerializedName("lngDeleteExperience")
        @Expose
        val lngDeleteExperience: String?=""
        @SerializedName("lngDeletelanguage")
        @Expose
        val lngDeletelanguage: String?=""
        @SerializedName("lngDescription")
        @Expose
        val lngDescription: String?=""
        @SerializedName("lngDetails")
        @Expose
        val lngDetails: String?=""
        @SerializedName("lngDisconnect")
        @Expose
        val lngDisconnect: String?=""
        @SerializedName("lngDocsVerification")
        @Expose
        val lngDocsVerification: String?=""
        @SerializedName("lngDocument")
        @Expose
        val lngDocument: String?=""
        @SerializedName("lngDocuments")
        @Expose
        val lngDocuments: String?=""
        @SerializedName("lngDonate")
        @Expose
        val lngDonate: String?=""
        @SerializedName("lngDonateNow")
        @Expose
        val lngDonateNow: String?=""
        @SerializedName("lngDonationClub")
        @Expose
        val lngDonationClub: String?=""
        @SerializedName("lngDonationDescription")
        @Expose
        val lngDonationDescription: String?=""
        @SerializedName("lngDone")
        @Expose
        val lngDone: String?=""
        @SerializedName("lngDontReceiveCode")
        @Expose
        val lngDontReceiveCode: String?=""
        @SerializedName("lngDownloadOn")
        @Expose
        val lngDownloadOn: String?=""
        @SerializedName("lngEdit")
        @Expose
        val lngEdit: String?=""
        @SerializedName("lngEditAlbumName")
        @Expose
        val lngEditAlbumName: String?=""
        @SerializedName("lngEditExperience")
        @Expose
        val lngEditExperience: String?=""
        @SerializedName("lngEditHashtags")
        @Expose
        val lngEditHashtags: String?=""
        @SerializedName("lngEditLanguages")
        @Expose
        val lngEditLanguages: String?=""
        @SerializedName("lngEditLink")
        @Expose
        val lngEditLink: String?=""
        @SerializedName("lngEditMedia")
        @Expose
        val lngEditMedia: String?=""
        @SerializedName("lngEducation")
        @Expose
        val lngEducation: String?=""
        @SerializedName("lngEmail")
        @Expose
        val lngEmail: String?=""
        @SerializedName("lngEmailAddress")
        @Expose
        val lngEmailAddress: String?=""
        @SerializedName("lngEmailMobile")
        @Expose
        val lngEmailMobile: String?=""
        @SerializedName("lngEng")
        @Expose
        val lngEng: String?=""
        @SerializedName("lngEnterAlbumName")
        @Expose
        val lngEnterAlbumName: String?=""
        @SerializedName("lngEnterDetails")
        @Expose
        val lngEnterDetails: String?=""
        @SerializedName("lngEnterLink")
        @Expose
        val lngEnterLink: String?=""
        @SerializedName("lngEnterSubAlbumName")
        @Expose
        val lngEnterSubAlbumName: String?=""
        @SerializedName("lngEnterTicketURL")
        @Expose
        val lngEnterTicketURL: String?=""
        @SerializedName("lngEnterTitle")
        @Expose
        val lngEnterTitle: String?=""
        @SerializedName("lngEvent")
        @Expose
        val lngEvent: String?=""
        @SerializedName("lngEventName")
        @Expose
        val lngEventName: String?=""
        @SerializedName("lngExplainWhatsWrongThisPost")
        @Expose
        val lngExplainWhatsWrongThisPost: String?=""
        @SerializedName("lngExplore")
        @Expose
        val lngExplore: String?=""
        @SerializedName("lngExploreAll")
        @Expose
        val lngExploreAll: String?=""
        @SerializedName("lngFAQ")
        @Expose
        val lngFAQ: String?=""
        @SerializedName("lngFName")
        @Expose
        val lngFName: String?=""
        @SerializedName("lngFamilyName")
        @Expose
        val lngFamilyName: String?=""
        @SerializedName("lngFeatured")
        @Expose
        val lngFeatured: String?=""
        @SerializedName("lngFeedHasBeenRemoved")
        @Expose
        val lngFeedHasBeenRemoved: String?=""
        @SerializedName("lngFemale")
        @Expose
        val lngFemale: String?=""
        @SerializedName("lngFile")
        @Expose
        val lngFile: String?=""
        @SerializedName("lngFilter")
        @Expose
        val lngFilter: String?=""
        @SerializedName("lngFirstName")
        @Expose
        val lngFirstName: String?=""
        @SerializedName("lngFollow")
        @Expose
        val lngFollow: String?=""
        @SerializedName("lngFollowUnfollow")
        @Expose
        val lngFollowUnfollow: String?=""
        @SerializedName("lngFollowers")
        @Expose
        val lngFollowers: String?=""
        @SerializedName("lngFollowing")
        @Expose
        val lngFollowing: String?=""
        @SerializedName("lngFootBallQue")
        @Expose
        val lngFootBallQue: String?=""
        @SerializedName("lngFootBallQueDetail")
        @Expose
        val lngFootBallQueDetail: String?=""
        @SerializedName("lngFootballAgeCategory")
        @Expose
        val lngFootballAgeCategory: String?=""
        @SerializedName("lngFootballLeague")
        @Expose
        val lngFootballLeague: String?=""
        @SerializedName("lngFootballLevel")
        @Expose
        val lngFootballLevel: String?=""
        @SerializedName("lngFootballType")
        @Expose
        val lngFootballType: String?=""
        @SerializedName("lngForSupport")
        @Expose
        val lngForSupport: String?=""
        @SerializedName("lngForgotDetail")
        @Expose
        val lngForgotDetail: String?=""
        @SerializedName("lngForgotPassword")
        @Expose
        val lngForgotPassword: String?=""
        @SerializedName("lngForgotTitle")
        @Expose
        val lngForgotTitle: String?=""
        @SerializedName("lngFrom")
        @Expose
        val lngFrom: String?=""
        @SerializedName("lngFullName")
        @Expose
        val lngFullName: String?=""
        @SerializedName("lngGK")
        @Expose
        val lngGK: String?=""
        @SerializedName("lngGallery")
        @Expose
        val lngGallery: String?=""
        @SerializedName("lngGender")
        @Expose
        val lngGender: String?=""
        @SerializedName("lngGenderQue")
        @Expose
        val lngGenderQue: String?=""
        @SerializedName("lngGenderQueDetail")
        @Expose
        val lngGenderQueDetail: String?=""
        @SerializedName("lngGeneral")
        @Expose
        val lngGeneral: String?=""
        @SerializedName("lngGeographical")
        @Expose
        val lngGeographical: String?=""
        @SerializedName("lngGeography")
        @Expose
        val lngGeography: String?=""
        @SerializedName("lngGetVIP")
        @Expose
        val lngGetVIP: String?=""
        @SerializedName("lngGiveMeStar")
        @Expose
        val lngGiveMeStar: String?=""
        @SerializedName("lngGiven")
        @Expose
        val lngGiven: String?=""
        @SerializedName("lngGoals")
        @Expose
        val lngGoals: String?=""
        @SerializedName("lngGrade")
        @Expose
        val lngGrade: String?=""
        @SerializedName("lngGroup")
        @Expose
        val lngGroup: String?=""
        @SerializedName("lngGroupName")
        @Expose
        val lngGroupName: String?=""
        @SerializedName("lngGroupRules")
        @Expose
        val lngGroupRules: String?=""
        @SerializedName("lngGroups")
        @Expose
        val lngGroups: String?=""
        @SerializedName("lngHashtags")
        @Expose
        val lngHashtags: String?=""
        @SerializedName("lngHeight")
        @Expose
        val lngHeight: String?=""
        @SerializedName("lngHiddenPosts")
        @Expose
        val lngHiddenPosts: String?=""
        @SerializedName("lngHidePost")
        @Expose
        val lngHidePost: String?=""
        @SerializedName("lngHobbies")
        @Expose
        val lngHobbies: String?=""
        @SerializedName("lngHobbiesAdded")
        @Expose
        val lngHobbiesAdded: String?=""
        @SerializedName("lngHobbiesFootBall")
        @Expose
        val lngHobbiesFootBall: String?=""
        @SerializedName("lngHome")
        @Expose
        val lngHome: String?=""
        @SerializedName("lngIAmCurrentlyStudyHere")
        @Expose
        val lngIAmCurrentlyStudyHere: String?=""
        @SerializedName("lngICurrentlyWorkHere")
        @Expose
        val lngICurrentlyWorkHere: String?=""
        @SerializedName("lngInApp")
        @Expose
        val lngInApp: String?=""
        @SerializedName("lngIndustry")
        @Expose
        val lngIndustry: String?=""
        @SerializedName("lngInfo")
        @Expose
        val lngInfo: String?=""
        @SerializedName("lngInstaPhoto")
        @Expose
        val lngInstaPhoto: String?=""
        @SerializedName("lngInstaPhotoDetail")
        @Expose
        val lngInstaPhotoDetail: String?=""
        @SerializedName("lngInterest")
        @Expose
        val lngInterest: String?=""
        @SerializedName("lngIntersted")
        @Expose
        val lngIntersted: String?=""
        @SerializedName("lngIntroDetail1")
        @Expose
        val lngIntroDetail1: String?=""
        @SerializedName("lngIntroDetail10")
        @Expose
        val lngIntroDetail10: String?=""
        @SerializedName("lngIntroDetail2")
        @Expose
        val lngIntroDetail2: String?=""
        @SerializedName("lngIntroDetail3")
        @Expose
        val lngIntroDetail3: String?=""
        @SerializedName("lngIntroDetail4")
        @Expose
        val lngIntroDetail4: String?=""
        @SerializedName("lngIntroDetail5")
        @Expose
        val lngIntroDetail5: String?=""
        @SerializedName("lngIntroDetail6")
        @Expose
        val lngIntroDetail6: String?=""
        @SerializedName("lngIntroDetail7")
        @Expose
        val lngIntroDetail7: String?=""
        @SerializedName("lngIntroDetail8")
        @Expose
        val lngIntroDetail8: String?=""
        @SerializedName("lngIntroDetail9")
        @Expose
        val lngIntroDetail9: String?=""
        @SerializedName("lngIntroTitle1")
        @Expose
        val lngIntroTitle1: String?=""
        @SerializedName("lngIntroTitle10")
        @Expose
        val lngIntroTitle10: String?=""
        @SerializedName("lngIntroTitle2")
        @Expose
        val lngIntroTitle2: String?=""
        @SerializedName("lngIntroTitle3")
        @Expose
        val lngIntroTitle3: String?=""
        @SerializedName("lngIntroTitle4")
        @Expose
        val lngIntroTitle4: String?=""
        @SerializedName("lngIntroTitle5")
        @Expose
        val lngIntroTitle5: String?=""
        @SerializedName("lngIntroTitle6")
        @Expose
        val lngIntroTitle6: String?=""
        @SerializedName("lngIntroTitle7")
        @Expose
        val lngIntroTitle7: String?=""
        @SerializedName("lngIntroTitle8")
        @Expose
        val lngIntroTitle8: String?=""
        @SerializedName("lngIntroTitle9")
        @Expose
        val lngIntroTitle9: String?=""
        @SerializedName("lngInvite")
        @Expose
        val lngInvite: String?=""
        @SerializedName("lngInviteContacts")
        @Expose
        val lngInviteContacts: String?=""
        @SerializedName("lngInvited")
        @Expose
        val lngInvited: String?=""
        @SerializedName("lngInvitedMembers")
        @Expose
        val lngInvitedMembers: String?=""
        @SerializedName("lngItem")
        @Expose
        val lngItem: String?=""
        @SerializedName("lngItems")
        @Expose
        val lngItems: String?=""
        @SerializedName("lngJersey")
        @Expose
        val lngJersey: String?=""
        @SerializedName("lngJobDescription")
        @Expose
        val lngJobDescription: String?=""
        @SerializedName("lngJobDetail")
        @Expose
        val lngJobDetail: String?=""
        @SerializedName("lngJobDutiesResponsibilities")
        @Expose
        val lngJobDutiesResponsibilities: String?=""
        @SerializedName("lngJobFunction")
        @Expose
        val lngJobFunction: String?=""
        @SerializedName("lngJobPurpose")
        @Expose
        val lngJobPurpose: String?=""
        @SerializedName("lngJobReference")
        @Expose
        val lngJobReference: String?=""
        @SerializedName("lngJobTitle")
        @Expose
        val lngJobTitle: String?=""
        @SerializedName("lngJoinMeOn")
        @Expose
        val lngJoinMeOn: String?=""
        @SerializedName("lngKg")
        @Expose
        val lngKg: String?=""
        @SerializedName("lngKnownAs")
        @Expose
        val lngKnownAs: String?=""
        @SerializedName("lngLB")
        @Expose
        val lngLB: String?=""
        @SerializedName("lngLM")
        @Expose
        val lngLM: String?=""
        @SerializedName("lngLName")
        @Expose
        val lngLName: String?=""
        @SerializedName("lngLS")
        @Expose
        val lngLS: String?=""
        @SerializedName("lngLanguage")
        @Expose
        val lngLanguage: String?=""
        @SerializedName("lngLanguageChange")
        @Expose
        val lngLanguageChange: String?=""
        @SerializedName("lngLanguageEdit")
        @Expose
        val lngLanguageEdit: String?=""
        @SerializedName("lngLanguages")
        @Expose
        val lngLanguages: String?=""
        @SerializedName("lngLastName")
        @Expose
        val lngLastName: String?=""
        @SerializedName("lngLatest")
        @Expose
        val lngLatest: String?=""
        @SerializedName("lngLeaveGroup")
        @Expose
        val lngLeaveGroup: String?=""
        @SerializedName("lngLeft")
        @Expose
        val lngLeft: String?=""
        @SerializedName("lngLike")
        @Expose
        val lngLike: String?=""
        @SerializedName("lngLikes")
        @Expose
        val lngLikes: String?=""
        @SerializedName("lngLink")
        @Expose
        val lngLink: String?=""
        @SerializedName("lngLinkMedia")
        @Expose
        val lngLinkMedia: String?=""
        @SerializedName("lngLinkWebsite")
        @Expose
        val lngLinkWebsite: String?=""
        @SerializedName("lngLinks")
        @Expose
        val lngLinks: String?=""
        @SerializedName("lngLocation")
        @Expose
        val lngLocation: String?=""
        @SerializedName("lngLogin")
        @Expose
        val lngLogin: String?=""
        @SerializedName("lngLogout")
        @Expose
        val lngLogout: String?=""
        @SerializedName("lngLogoutMsg")
        @Expose
        val lngLogoutMsg: String?=""
        @SerializedName("lngMMYYYY")
        @Expose
        val lngMMYYYY: String?=""
        @SerializedName("lngMail")
        @Expose
        val lngMail: String?=""
        @SerializedName("lngMale")
        @Expose
        val lngMale: String?=""
        @SerializedName("lngMedia")
        @Expose
        val lngMedia: String?=""
        @SerializedName("lngMembers")
        @Expose
        val lngMembers: String?=""
        @SerializedName("lngMentor")
        @Expose
        val lngMentor: String?=""
        @SerializedName("lngMenuGetVIP")
        @Expose
        val lngMenuGetVIP: String?=""
        @SerializedName("lngMessage")
        @Expose
        val lngMessage: String?=""
        @SerializedName("lngMobileNo")
        @Expose
        val lngMobileNo: String?=""
        @SerializedName("lngMostCommented")
        @Expose
        val lngMostCommented: String?=""
        @SerializedName("lngMostPopular")
        @Expose
        val lngMostPopular: String?=""
        @SerializedName("lngMostStarred")
        @Expose
        val lngMostStarred: String?=""
        @SerializedName("lngMostViewed")
        @Expose
        val lngMostViewed: String?=""
        @SerializedName("lngMoveToOtherCollection")
        @Expose
        val lngMoveToOtherCollection: String?=""
        @SerializedName("lngMoveToTricks")
        @Expose
        val lngMoveToTricks: String?=""
        @SerializedName("lngMoveToVideos")
        @Expose
        val lngMoveToVideos: String?=""
        @SerializedName("lngMsgDeleteAlbum")
        @Expose
        val lngMsgDeleteAlbum: String?=""
        @SerializedName("lngMsgDeleteComment")
        @Expose
        val lngMsgDeleteComment: String?=""
        @SerializedName("lngMsgDeletePost")
        @Expose
        val lngMsgDeletePost: String?=""
        @SerializedName("lngMsgDeleteSubAlbum")
        @Expose
        val lngMsgDeleteSubAlbum: String?=""
        @SerializedName("lngMsgNotification")
        @Expose
        val lngMsgNotification: String?=""
        @SerializedName("lngMyAccount")
        @Expose
        val lngMyAccount: String?=""
        @SerializedName("lngMyPostOnly")
        @Expose
        val lngMyPostOnly: String?=""
        @SerializedName("lngName")
        @Expose
        val lngName: String?=""
        @SerializedName("lngNationalTeam")
        @Expose
        val lngNationalTeam: String?=""
        @SerializedName("lngNearby")
        @Expose
        val lngNearby: String?=""
        @SerializedName("lngNeedMoreHelp")
        @Expose
        val lngNeedMoreHelp: String?=""
        @SerializedName("lngNewPassword")
        @Expose
        val lngNewPassword: String?=""
        @SerializedName("lngNewUser")
        @Expose
        val lngNewUser: String?=""
        @SerializedName("lngNext")
        @Expose
        val lngNext: String?=""
        @SerializedName("lngNo")
        @Expose
        val lngNo: String?=""
        @SerializedName("lngNoDataFound")
        @Expose
        val lngNoDataFound: String?=""
        @SerializedName("lngNoInternet")
        @Expose
        val lngNoInternet: String?=""
        @SerializedName("lngNoteRemember")
        @Expose
        val lngNoteRemember: String?=""
        @SerializedName("lngNotification")
        @Expose
        val lngNotification: String?=""
        @SerializedName("lngNotificationSettings")
        @Expose
        val lngNotificationSettings: String?=""
        @SerializedName("lngOneTouchAccess")
        @Expose
        val lngOneTouchAccess: String?=""
        @SerializedName("lngOrganization")
        @Expose
        val lngOrganization: String?=""
        @SerializedName("lngPTimeAnyTime")
        @Expose
        val lngPTimeAnyTime: String?=""
        @SerializedName("lngPTimeThisMonth")
        @Expose
        val lngPTimeThisMonth: String?=""
        @SerializedName("lngPTimeThisWeek")
        @Expose
        val lngPTimeThisWeek: String?=""
        @SerializedName("lngPTimeToday")
        @Expose
        val lngPTimeToday: String?=""
        @SerializedName("lngParentPermission")
        @Expose
        val lngParentPermission: String?=""
        @SerializedName("lngParentPermissionDetail")
        @Expose
        val lngParentPermissionDetail: String?=""
        @SerializedName("lngPartnerWithUs")
        @Expose
        val lngPartnerWithUs: String?=""
        @SerializedName("lngPassportNationality")
        @Expose
        val lngPassportNationality: String?=""
        @SerializedName("lngPassword")
        @Expose
        val lngPassword: String?=""
        @SerializedName("lngPayDescription")
        @Expose
        val lngPayDescription: String?=""
        @SerializedName("lngPayType")
        @Expose
        val lngPayType: String?=""
        @SerializedName("lngPending")
        @Expose
        val lngPending: String?=""
        @SerializedName("lngPhoto")
        @Expose
        val lngPhoto: String?=""
        @SerializedName("lngPhotoId")
        @Expose
        val lngPhotoId: String?=""
        @SerializedName("lngPhotos")
        @Expose
        val lngPhotos: String?=""
        @SerializedName("lngPhotosPermissionDeniedDesciption")
        @Expose
        val lngPhotosPermissionDeniedDesciption: String?=""
        @SerializedName("lngPhotosPermissionDeniedTitle")
        @Expose
        val lngPhotosPermissionDeniedTitle: String?=""
        @SerializedName("lngPitchPosition")
        @Expose
        val lngPitchPosition: String?=""
        @SerializedName("lngPitchPostion")
        @Expose
        val lngPitchPostion: String?=""
        @SerializedName("lngPlace")
        @Expose
        val lngPlace: String?=""
        @SerializedName("lngPlaceholderRecommendation")
        @Expose
        val lngPlaceholderRecommendation: String?=""
        @SerializedName("lngPopular")
        @Expose
        val lngPopular: String?=""
        @SerializedName("lngPosition")
        @Expose
        val lngPosition: String?=""
        @SerializedName("lngPost")
        @Expose
        val lngPost: String?=""
        @SerializedName("lngPostLink")
        @Expose
        val lngPostLink: String?=""
        @SerializedName("lngPostType")
        @Expose
        val lngPostType: String?=""
        @SerializedName("lngPostedBy")
        @Expose
        val lngPostedBy: String?=""
        @SerializedName("lngPreferredOutfitters")
        @Expose
        val lngPreferredOutfitters: String?=""
        @SerializedName("lngPresent")
        @Expose
        val lngPresent: String?=""
        @SerializedName("lngPreviousClub")
        @Expose
        val lngPreviousClub: String?=""
        @SerializedName("lngPrivacy")
        @Expose
        val lngPrivacy: String?=""
        @SerializedName("lngPrivacyPolicy")
        @Expose
        val lngPrivacyPolicy: String?=""
        @SerializedName("lngPrivate")
        @Expose
        val lngPrivate: String?=""
        @SerializedName("lngProSkills")
        @Expose
        val lngProSkills: String?=""
        @SerializedName("lngProceed")
        @Expose
        val lngProceed: String?=""
        @SerializedName("lngProfessionals")
        @Expose
        val lngProfessionals: String?=""
        @SerializedName("lngProficiency")
        @Expose
        val lngProficiency: String?=""
        @SerializedName("lngProfile")
        @Expose
        val lngProfile: String?=""
        @SerializedName("lngProfileDetail")
        @Expose
        val lngProfileDetail: String?=""
        @SerializedName("lngProfileQue")
        @Expose
        val lngProfileQue: String?=""
        @SerializedName("lngProfileRegisterDetail")
        @Expose
        val lngProfileRegisterDetail: String?=""
        @SerializedName("lngProfileRegisterTitle")
        @Expose
        val lngProfileRegisterTitle: String?=""
        @SerializedName("lngProfileSummary")
        @Expose
        val lngProfileSummary: String?=""
        @SerializedName("lngProfileTitle")
        @Expose
        val lngProfileTitle: String?=""
        @SerializedName("lngProfileView")
        @Expose
        val lngProfileView: String?=""
        @SerializedName("lngPublic")
        @Expose
        val lngPublic: String?=""
        @SerializedName("lngPublicationTime")
        @Expose
        val lngPublicationTime: String?=""
        @SerializedName("lngPush")
        @Expose
        val lngPush: String?=""
        @SerializedName("lngRB")
        @Expose
        val lngRB: String?=""
        @SerializedName("lngRM")
        @Expose
        val lngRM: String?=""
        @SerializedName("lngRS")
        @Expose
        val lngRS: String?=""
        @SerializedName("lngRankings")
        @Expose
        val lngRankings: String?=""
        @SerializedName("lngRateAgent")
        @Expose
        val lngRateAgent: String?=""
        @SerializedName("lngRatingReview")
        @Expose
        val lngRatingReview: String?=""
        @SerializedName("lngReceived")
        @Expose
        val lngReceived: String?=""
        @SerializedName("lngReceivedRequests")
        @Expose
        val lngReceivedRequests: String?=""
        @SerializedName("lngRecommend")
        @Expose
        val lngRecommend: String?=""
        @SerializedName("lngRecommendationRequest")
        @Expose
        val lngRecommendationRequest: String?=""
        @SerializedName("lngRecommendations")
        @Expose
        val lngRecommendations: String?=""
        @SerializedName("lngRemoveFile")
        @Expose
        val lngRemoveFile: String?=""
        @SerializedName("lngRemoveFromCollection")
        @Expose
        val lngRemoveFromCollection: String?=""
        @SerializedName("lngReplies")
        @Expose
        val lngReplies: String?=""
        @SerializedName("lngReply")
        @Expose
        val lngReply: String?=""
        @SerializedName("lngReport")
        @Expose
        val lngReport: String?=""
        @SerializedName("lngReportCopyrightInfrigement")
        @Expose
        val lngReportCopyrightInfrigement: String?=""
        @SerializedName("lngReportPostAd")
        @Expose
        val lngReportPostAd: String?=""
        @SerializedName("lngRequestRecommendations")
        @Expose
        val lngRequestRecommendations: String?=""
        @SerializedName("lngRequestVerification")
        @Expose
        val lngRequestVerification: String?=""
        @SerializedName("lngRequested")
        @Expose
        val lngRequested: String?=""
        @SerializedName("lngRequestsReceived")
        @Expose
        val lngRequestsReceived: String?=""
        @SerializedName("lngRequiredQualExp")
        @Expose
        val lngRequiredQualExp: String?=""
        @SerializedName("lngResetPassword")
        @Expose
        val lngResetPassword: String?=""
        @SerializedName("lngRetypePassword")
        @Expose
        val lngRetypePassword: String?=""
        @SerializedName("lngReviews")
        @Expose
        val lngReviews: String?=""
        @SerializedName("lngRight")
        @Expose
        val lngRight: String?=""
        @SerializedName("lngRisingStarts")
        @Expose
        val lngRisingStarts: String?=""
        @SerializedName("lngSMS")
        @Expose
        val lngSMS: String?=""
        @SerializedName("lngSave")
        @Expose
        val lngSave: String?=""
        @SerializedName("lngAddAgent")
        @Expose
        val lngAddAgent: String?=""
        @SerializedName("lngSavePost")
        @Expose
        val lngSavePost: String?=""
        @SerializedName("lngSavedPosts")
        @Expose
        val lngSavedPosts: String?=""
        @SerializedName("lngSchoolCollege")
        @Expose
        val lngSchoolCollege: String?=""
        @SerializedName("lngSearch")
        @Expose
        val lngSearch: String?=""
        @SerializedName("lngSearchCountry")
        @Expose
        val lngSearchCountry: String?=""
        @SerializedName("lngSearchLocation")
        @Expose
        val lngSearchLocation: String?=""
        @SerializedName("lngSearchPlace")
        @Expose
        val lngSearchPlace: String?=""
        @SerializedName("lngSearchSkills")
        @Expose
        val lngSearchSkills: String?=""
        @SerializedName("lngSelectAll")
        @Expose
        val lngSelectAll: String?=""
        @SerializedName("lngSelectAnySubAlbum")
        @Expose
        val lngSelectAnySubAlbum: String?=""
        @SerializedName("lngSelectCity")
        @Expose
        val lngSelectCity: String?=""
        @SerializedName("lngSelectClub")
        @Expose
        val lngSelectClub: String?=""
        @SerializedName("lngSelectConnectionRequestRecommendation")
        @Expose
        val lngSelectConnectionRequestRecommendation: String?=""
        @SerializedName("lngSelectConnectionType")
        @Expose
        val lngSelectConnectionType: String?=""
        @SerializedName("lngSelectCountry")
        @Expose
        val lngSelectCountry: String?=""
        @SerializedName("lngSelectCountryCode")
        @Expose
        val lngSelectCountryCode: String?=""
        @SerializedName("lngSelectDate")
        @Expose
        val lngSelectDate: String?=""
        @SerializedName("lngSelectEventType")
        @Expose
        val lngSelectEventType: String?=""
        @SerializedName("lngSelectFromDate")
        @Expose
        val lngSelectFromDate: String?=""
        @SerializedName("lngSelectGroup")
        @Expose
        val lngSelectGroup: String?=""
        @SerializedName("lngSelectModeQue")
        @Expose
        val lngSelectModeQue: String?=""
        @SerializedName("lngSelectModeQueDetail")
        @Expose
        val lngSelectModeQueDetail: String?=""
        @SerializedName("lngSelectOrganization")
        @Expose
        val lngSelectOrganization: String?=""
        @SerializedName("lngSelectPhoto")
        @Expose
        val lngSelectPhoto: String?=""
        @SerializedName("lngSelectPlace")
        @Expose
        val lngSelectPlace: String?=""
        @SerializedName("lngSelectPosition")
        @Expose
        val lngSelectPosition: String?=""
        @SerializedName("lngSelectPrivacy")
        @Expose
        val lngSelectPrivacy: String?=""
        @SerializedName("lngSelectProficiency")
        @Expose
        val lngSelectProficiency: String?=""
        @SerializedName("lngSelectProfileView")
        @Expose
        val lngSelectProfileView: String?=""
        @SerializedName("lngSelectRelationship")
        @Expose
        val lngSelectRelationship: String?=""
        @SerializedName("lngSelectStatus")
        @Expose
        val lngSelectStatus: String?=""
        @SerializedName("lngSelectTime")
        @Expose
        val lngSelectTime: String?=""
        @SerializedName("lngSelectToDate")
        @Expose
        val lngSelectToDate: String?=""
        @SerializedName("lngSelectVideo")
        @Expose
        val lngSelectVideo: String?=""
        @SerializedName("lngSelectYourRoleCompany")
        @Expose
        val lngSelectYourRoleCompany: String?=""
        @SerializedName("lngSelectedClub")
        @Expose
        val lngSelectedClub: String?=""
        @SerializedName("lngSelectedSkill")
        @Expose
        val lngSelectedSkill: String?=""
        @SerializedName("lngSend")
        @Expose
        val lngSend: String?=""
        @SerializedName("lngSendCodeAgain")
        @Expose
        val lngSendCodeAgain: String?=""
        @SerializedName("lngSendFeedback")
        @Expose
        val lngSendFeedback: String?=""
        @SerializedName("lngSendMessage")
        @Expose
        val lngSendMessage: String?=""
        @SerializedName("lngSendMsg")
        @Expose
        val lngSendMsg: String?=""
        @SerializedName("lngSentRequests")
        @Expose
        val lngSentRequests: String?=""
        @SerializedName("lngSetYourPrivacy")
        @Expose
        val lngSetYourPrivacy: String?=""
        @SerializedName("lngSetting")
        @Expose
        val lngSetting: String?=""
        @SerializedName("lngShare")
        @Expose
        val lngShare: String?=""
        @SerializedName("lngSharePost")
        @Expose
        val lngSharePost: String?=""
        @SerializedName("lngSharePrivateMsg")
        @Expose
        val lngSharePrivateMsg: String?=""
        @SerializedName("lngShareThisCard")
        @Expose
        val lngShareThisCard: String?=""
        @SerializedName("lngShareVia")
        @Expose
        val lngShareVia: String?=""
        @SerializedName("lngShowYourTalent")
        @Expose
        val lngShowYourTalent: String?=""
        @SerializedName("lngSignin")
        @Expose
        val lngSignin: String?=""
        @SerializedName("lngSignup")
        @Expose
        val lngSignup: String?=""
        @SerializedName("lngSignupApple")
        @Expose
        val lngSignupApple: String?=""
        @SerializedName("lngSignupFB")
        @Expose
        val lngSignupFB: String?=""
        @SerializedName("lngSignupInsta")
        @Expose
        val lngSignupInsta: String?=""
        @SerializedName("lngSignupLinkedIn")
        @Expose
        val lngSignupLinkedIn: String?=""
        @SerializedName("lngSkillAdded")
        @Expose
        val lngSkillAdded: String?=""
        @SerializedName("lngSkillExDataAnalysis")
        @Expose
        val lngSkillExDataAnalysis: String?=""
        @SerializedName("lngSkillsEndorsements")
        @Expose
        val lngSkillsEndorsements: String?=""
        @SerializedName("lngSkillsSuggestions")
        @Expose
        val lngSkillsSuggestions: String?=""
        @SerializedName("lngSkip")
        @Expose
        val lngSkip: String?=""
        @SerializedName("lngSomthingWrong")
        @Expose
        val lngSomthingWrong: String?=""
        @SerializedName("lngSortBy")
        @Expose
        val lngSortBy: String?=""
        @SerializedName("lngSports")
        @Expose
        val lngSports: String?=""
        @SerializedName("lngStatus")
        @Expose
        val lngStatus: String?=""
        @SerializedName("lngSubAlbumName")
        @Expose
        val lngSubAlbumName: String?=""
        @SerializedName("lngSubAlbums")
        @Expose
        val lngSubAlbums: String?=""
        @SerializedName("lngSubmit")
        @Expose
        val lngSubmit: String?=""
        @SerializedName("lngSuccess")
        @Expose
        val lngSuccess: String?=""
        @SerializedName("lngSuggestFeature")
        @Expose
        val lngSuggestFeature: String?=""
        @SerializedName("lngSendFeedbackPlaceholder")
        @Expose
        val lngSendFeedbackPlaceholder: String?=""
        @SerializedName("lngSuggestedHobbiesBased")
        @Expose
        val lngSuggestedHobbiesBased: String?=""
        @SerializedName("lngSuggestedSkillBased")
        @Expose
        val lngSuggestedSkillBased: String?=""
        @SerializedName("lngSuggestions")
        @Expose
        val lngSuggestions: String?=""
        @SerializedName("lngSync")
        @Expose
        val lngSync: String?=""
        @SerializedName("lngTC")
        @Expose
        val lngTC: String?=""
        @SerializedName("lngTalkExpert")
        @Expose
        val lngTalkExpert: String?=""
        @SerializedName("lngTellUsBriflyYourself")
        @Expose
        val lngTellUsBriflyYourself: String?=""
        @SerializedName("lngTellUsGroup")
        @Expose
        val lngTellUsGroup: String?=""
        @SerializedName("lngText")
        @Expose
        val lngText: String?=""
        @SerializedName("lngTicketURL")
        @Expose
        val lngTicketURL: String?=""
        @SerializedName("lngTime")
        @Expose
        val lngTime: String?=""
        @SerializedName("lngTitle")
        @Expose
        val lngTitle: String?=""
        @SerializedName("lngTo")
        @Expose
        val lngTo: String?=""
        @SerializedName("lngToday")
        @Expose
        val lngToday: String?=""
        @SerializedName("lngTomorrow")
        @Expose
        val lngTomorrow: String?=""
        @SerializedName("lngTraining")
        @Expose
        val lngTraining: String?=""
        @SerializedName("lngTranslation")
        @Expose
        val lngTranslation: String?=""
        @SerializedName("lngTranslationDetail")
        @Expose
        val lngTranslationDetail: String?=""
        @SerializedName("lngTricks")
        @Expose
        val lngTricks: String?=""
        @SerializedName("lngTrophiesNHonors")
        @Expose
        val lngTrophiesNHonors: String?=""
        @SerializedName("lngUnblock")
        @Expose
        val lngUnblock: String?=""
        @SerializedName("lngUnder")
        @Expose
        val lngUnder: String?=""
        @SerializedName("lngUndiscovered")
        @Expose
        val lngUndiscovered: String?=""
        @SerializedName("lngUndo")
        @Expose
        val lngUndo: String?=""
        @SerializedName("lngUnfollow")
        @Expose
        val lngUnfollow: String?=""
        @SerializedName("lngUnfriend")
        @Expose
        val lngUnfriend: String?=""
        @SerializedName("lngUnhideThisPost")
        @Expose
        val lngUnhideThisPost: String?=""
        @SerializedName("lngUnsave")
        @Expose
        val lngUnsave: String?=""
        @SerializedName("lngUnsaveThisPost")
        @Expose
        val lngUnsaveThisPost: String?=""
        @SerializedName("lngUpdate")
        @Expose
        val lngUpdate: String?=""
        @SerializedName("lngUpdateEducation")
        @Expose
        val lngUpdateEducation: String?=""
        @SerializedName("lngUpload")
        @Expose
        val lngUpload: String?=""
        @SerializedName("lngUploadEventImage")
        @Expose
        val lngUploadEventImage: String?=""
        @SerializedName("lngUsingGPS")
        @Expose
        val lngUsingGPS: String?=""
        @SerializedName("lngVerificationDetail")
        @Expose
        val lngVerificationDetail: String?=""
        @SerializedName("lngVerificationTitle")
        @Expose
        val lngVerificationTitle: String?=""
        @SerializedName("lngVersion")
        @Expose
        val lngVersion: String?=""
        @SerializedName("lngVideo")
        @Expose
        val lngVideo: String?=""
        @SerializedName("lngVideos")
        @Expose
        val lngVideos: String?=""
        @SerializedName("lngViewAll")
        @Expose
        val lngViewAll: String?=""
        @SerializedName("lngViewAllCompanies")
        @Expose
        val lngViewAllCompanies: String?=""
        @SerializedName("lngViewAllProfessionals")
        @Expose
        val lngViewAllProfessionals: String?=""
        @SerializedName("lngViewInterests")
        @Expose
        val lngViewInterests: String?=""
        @SerializedName("lngViewMore")
        @Expose
        val lngViewMore: String?=""
        @SerializedName("lngViewMoreComments")
        @Expose
        val lngViewMoreComments: String?=""
        @SerializedName("lngViewProfile")
        @Expose
        val lngViewProfile: String?=""
        @SerializedName("lngViews")
        @Expose
        val lngViews: String?=""
        @SerializedName("lngVote")
        @Expose
        val lngVote: String?=""
        @SerializedName("lngWantTranslated")
        @Expose
        val lngWantTranslated: String?=""
        @SerializedName("lngWantTranslatedDetail")
        @Expose
        val lngWantTranslatedDetail: String?=""
        @SerializedName("lngWeAreHelp")
        @Expose
        val lngWeAreHelp: String?=""
        @SerializedName("lngWeWouldhate")
        @Expose
        val lngWeWouldhate: String?=""
        @SerializedName("lngWebsite")
        @Expose
        val lngWebsite: String?=""
        @SerializedName("lngWeekend")
        @Expose
        val lngWeekend: String?=""
        @SerializedName("lngWeight")
        @Expose
        val lngWeight: String?=""
        @SerializedName("lngWelcomeBack")
        @Expose
        val lngWelcomeBack: String?=""
        @SerializedName("lngWhatTypeJob")
        @Expose
        val lngWhatTypeJob: String?=""
        @SerializedName("lngWhatisIssue")
        @Expose
        val lngWhatisIssue: String?=""
        @SerializedName("lngWhoAmI")
        @Expose
        val lngWhoAmI: String?=""
        @SerializedName("lngWhyAreYouLeaving")
        @Expose
        val lngWhyAreYouLeaving: String?=""
        @SerializedName("lngWhyReoprt")
        @Expose
        val lngWhyReoprt: String?=""
        @SerializedName("lngWorkExperience")
        @Expose
        val lngWorkExperience: String?=""
        @SerializedName("lngWriteAComment")
        @Expose
        val lngWriteAComment: String?=""
        @SerializedName("lngWriteAboutGroup")
        @Expose
        val lngWriteAboutGroup: String?=""
        @SerializedName("lngWriteAboutGroupRules")
        @Expose
        val lngWriteAboutGroupRules: String?=""
        @SerializedName("lngWriteAboutYourself")
        @Expose
        val lngWriteAboutYourself: String?=""
        @SerializedName("lngWriteHere")
        @Expose
        val lngWriteHere: String?=""
        @SerializedName("lngWriteReview")
        @Expose
        val lngWriteReview: String?=""
        @SerializedName("lngWriteSomething")
        @Expose
        val lngWriteSomething: String?=""
        @SerializedName("lngYears")
        @Expose
        val lngYears: String?=""
        @SerializedName("lngYes")
        @Expose
        val lngYes: String?=""
        @SerializedName("lngYourPositionPitch")
        @Expose
        val lngYourPositionPitch: String?=""
        @SerializedName("lngYourProfile")
        @Expose
        val lngYourProfile: String?=""
        @SerializedName("lngYourReviews")
        @Expose
        val lngYourReviews: String?=""
        @SerializedName("lngcontract")
        @Expose
        val lngcontract: String?=""
        @SerializedName("lngletGetStart")
        @Expose
        val lngletGetStart: String?=""
        @SerializedName("lngReferralCode")
        @Expose
        val lngReferralCode: String?=""
        @SerializedName("lngParentAlertTitle")
        @Expose
        val lngParentAlertTitle: String?=""
        @SerializedName("lngParentAlertSubTitle")
        @Expose
        val lngParentAlertSubTitle: String?=""
        @SerializedName("lngOK")
        @Expose
        val lngOK: String?=""
        @SerializedName("lngValidPasswordInfo")
        @Expose
        val lngValidPasswordInfo: String?=""
        @SerializedName("lngParentAggreementDetail")
        @Expose
        val lngParentAggreementDetail: String?=""
        @SerializedName("lngSeeMore")
        @Expose
        val lngSeeMore: String?=""
        @SerializedName("lngSeeLess")
        @Expose
        val lngSeeLess: String?=""
        @SerializedName("lngDeclaration")
        @Expose
        val lngDeclaration: String?=""
        @SerializedName("lngLookingForApportunities")
        @Expose
        val lngLookingForApportunities: String?=""
        @SerializedName("lngShareCardWebsite")
        @Expose
        val lngShareCardWebsite: String?=""
        @SerializedName("lngInviteReferralCode")
        @Expose
        val lngInviteReferralCode: String?=""
        @SerializedName("lngReferralBonusInfo")
        @Expose
        val lngReferralBonusInfo: String?=""
        @SerializedName("lngContactUsTitle1")
        @Expose
        val lngContactUsTitle1: String?=""
        @SerializedName("lngContactUsTitle2")
        @Expose
        val lngContactUsTitle2: String?=""
        @SerializedName("lngContactUsTitle3")
        @Expose
        val lngContactUsTitle3: String?=""
        @SerializedName("lngContactUsTitle4")
        @Expose
        val lngContactUsTitle4: String?=""
        @SerializedName("lngCommonQuestions")
        @Expose
        val lngCommonQuestions: String?=""
        @SerializedName("lngCommonQuestionsSubTitle")
        @Expose
        val lngCommonQuestionsSubTitle: String?=""
        @SerializedName("lngNeedMoreHelpSubTitle")
        @Expose
        val lngNeedMoreHelpSubTitle: String?=""
        @SerializedName("lngCUGeneralQueries")
        @Expose
        val lngCUGeneralQueries: String?=""
        @SerializedName("lngCUSuggestFeature")
        @Expose
        val lngCUSuggestFeature: String?=""
        @SerializedName("lngCUPartnerShip")
        @Expose
        val lngCUPartnerShip: String?=""
        @SerializedName("lngCUPress")
        @Expose
        val lngCUPress: String?=""
        @SerializedName("lngAllowPushNotifications")
        @Expose
        val lngAllowPushNotifications: String?=""
        @SerializedName("lngAllowPushNotificationsOnMessage")
        @Expose
        val lngAllowPushNotificationsOnMessage: String?=""
        @SerializedName("lngAllowPushNotificationsOffMessage")
        @Expose
        val lngAllowPushNotificationsOffMessage: String?=""
        @SerializedName("lngFromAnyone")
        @Expose
        val lngFromAnyone: String?=""
        @SerializedName("lngFromConnections")
        @Expose
        val lngFromConnections: String?=""
        @SerializedName("lngFromOff")
        @Expose
        val lngFromOff: String?=""
        @SerializedName("lngFromOn")
        @Expose
        val lngFromOn: String?=""
        @SerializedName("lngLikeFromAnyoneMessage")
        @Expose
        val lngLikeFromAnyoneMessage: String?=""
        @SerializedName("lngLikeFromOffMessage")
        @Expose
        val lngLikeFromOffMessage: String?=""
        @SerializedName("lngLikeFromConnectionMessage")
        @Expose
        val lngLikeFromConnectionMessage: String?=""
        @SerializedName("lngCommentsFromAnyoneMessage")
        @Expose
        val lngCommentsFromAnyoneMessage: String?=""
        @SerializedName("lngCommentsFromConnectionMessage")
        @Expose
        val lngCommentsFromConnectionMessage: String?=""
        @SerializedName("lngCommentsFromOffMessage")
        @Expose
        val lngCommentsFromOffMessage: String?=""
        @SerializedName("lngPostComments")
        @Expose
        val lngPostComments: String?=""
        @SerializedName("lngChatFromOnMessage")
        @Expose
        val lngChatFromOnMessage: String?=""
        @SerializedName("lngChatFromOffMessage")
        @Expose
        val lngChatFromOffMessage: String?=""
        @SerializedName("lngConnectionRequests")
        @Expose
        val lngConnectionRequests: String?=""
        @SerializedName("lngConnectionRequestFromOnMessage")
        @Expose
        val lngConnectionRequestFromOnMessage: String?=""
        @SerializedName("lngConnectionRequestFromOffMessage")
        @Expose
        val lngConnectionRequestFromOffMessage: String?=""
        @SerializedName("lngFollowersFromOnMessage")
        @Expose
        val lngFollowersFromOnMessage: String?=""
        @SerializedName("lngFollowersFromOffMessage")
        @Expose
        val lngFollowersFromOffMessage: String?=""
        @SerializedName("lngSuggestAFeaturePlaceholder")
        @Expose
        val lngSuggestAFeaturePlaceholder: String?=""
        @SerializedName("lngExpiryDate")
        @Expose
        val lngExpiryDate: String?=""
        @SerializedName("lngPostLikes")
        @Expose
        val lngPostLikes: String?=""
        @SerializedName("lngChatMessages")
        @Expose
        val lngChatMessages: String?=""
        @SerializedName("lngWriteMessage")
        @Expose
        val lngWriteMessage: String?=""
    }
}
