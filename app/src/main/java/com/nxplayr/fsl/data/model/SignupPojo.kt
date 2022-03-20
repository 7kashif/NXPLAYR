package com.nxplayr.fsl.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.ArrayList


data class Club(
        @SerializedName("clubID")
        val clubID: String,
        @SerializedName("clubName")
        val clubName: String,
        @SerializedName("userID")
        val userID: String,
        @SerializedName("userclubID")
        val userclubID: String,
) : Serializable

data class Education(
    @SerializedName("degreeID")
        val degreeID: String,
    @SerializedName("degreeName")
        val degreeName: String,
    @SerializedName("languageID")
        val languageID: String,
    @SerializedName("media")
        val media: List<Media>,
    @SerializedName("universityID")
        val universityID: String,
    @SerializedName("universityName")
        val universityName: String,
    @SerializedName("userID")
        val userID: String,
    @SerializedName("usereducationCreatedDate")
        val usereducationCreatedDate: String,
    @SerializedName("usereducationDescription")
        val usereducationDescription: String,
    @SerializedName("usereducationGrade")
        val usereducationGrade: String,
    @SerializedName("usereducationID")
        val usereducationID: String,
    @SerializedName("usereducationIsDeleted")
        val usereducationIsDeleted: String,
    @SerializedName("usereducationPeriodOfTimeFrom")
        val usereducationPeriodOfTimeFrom: String,
    @SerializedName("usereducationPeriodOfTimeTo")
        val usereducationPeriodOfTimeTo: String,
    @SerializedName("usereducationPrivarcyData")
        val usereducationPrivarcyData: String,
    @SerializedName("usereducationPrivarcyType")
        val usereducationPrivarcyType: String,
    @SerializedName("usereducationStatus")
        val usereducationStatus: String,



) : Serializable

data class Employement(
    @SerializedName("cityID")
        val cityID: String,
    @SerializedName("cityName")
        val cityName: String,
    @SerializedName("companyID")
        val companyID: String,
    @SerializedName("companyName")
        val companyName: String,
    @SerializedName("countryID")
        val countryID: String,
    @SerializedName("countryName")
        val countryName: String,
    @SerializedName("jobfuncID")
        val jobfuncID: String,
    @SerializedName("jobfuncName")
        val jobfuncName: String,
    @SerializedName("languageID")
        val languageID: String,
    @SerializedName("media")
        val media: List<MediaX>,
    @SerializedName("stateID")
        val stateID: String,
    @SerializedName("stateName")
        val stateName: String,
    @SerializedName("userID")
        val userID: String,
    @SerializedName("useremployementCityText")
        val useremployementCityText: String,
    @SerializedName("useremployementCountryText")
        val useremployementCountryText: String,
    @SerializedName("useremployementCreatedDate")
        val useremployementCreatedDate: String,
    @SerializedName("useremployementDescription")
        val useremployementDescription: String,
    @SerializedName("useremployementID")
        val useremployementID: String,
    @SerializedName("useremployementIsCurrent")
        val useremployementIsCurrent: String,
    @SerializedName("useremployementIsDeleted")
        val useremployementIsDeleted: String,
    @SerializedName("useremployementLatitude")
        val useremployementLatitude: String,
    @SerializedName("useremployementLongitude")
        val useremployementLongitude: String,
    @SerializedName("useremployementPeriodOfTimeFrom")
        val useremployementPeriodOfTimeFrom: String,
    @SerializedName("useremployementPeriodOfTimeTo")
        val useremployementPeriodOfTimeTo: String,
    @SerializedName("useremployementPrivacyData")
        val useremployementPrivacyData: String,
    @SerializedName("useremployementPrivacyType")
        val useremployementPrivacyType: String,
    @SerializedName("useremployementStatus")
        val useremployementStatus: String
) : Serializable

data class Language(
        @SerializedName("languageID")
        val languageID: String,
        @SerializedName("languageName")
        val languageName: String,
        @SerializedName("profiencyID")
        val profiencyID: String,
        @SerializedName("profiencyName")
        val profiencyName: String,
        @SerializedName("userID")
        val userID: String,
        @SerializedName("userlanguageID")
        val userlanguageID: String
) : Serializable

data class Passport(
        @SerializedName("countryID")
        val countryID: String,
        @SerializedName("countryName")
        val countryName: String,
        @SerializedName("userID")
        val userID: String,
        @SerializedName("userpassport")
        val userpassport: String
) : Serializable

data class Skill(
        @SerializedName("skillID")
        val skillID: String,
        @SerializedName("skillName")
        val skillName: String,
        @SerializedName("userID")
        val userID: String,
        @SerializedName("userskillCreatedDate")
        val userskillCreatedDate: String,
        @SerializedName("userskillID")
        val userskillID: String
) : Serializable

data class MediaX(
        @SerializedName("userID")
        val userID: String,
        @SerializedName("useremployementID")
        val useremployementID: String,
        @SerializedName("userempmediaDescription")
        val userempmediaDescription: String,
        @SerializedName("userempmediaFile")
        val userempmediaFile: String,
        @SerializedName("userempmediaID")
        val userempmediaID: String,
        @SerializedName("userempmediaTitle")
        val userempmediaTitle: String,
        @SerializedName("userempmediaType")
        val userempmediaType: String
) : Serializable

data class SignupPojo(
    @SerializedName("data")
        var `data`: List<SignupData>,
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String



) : Serializable

data class SignupData(
    @SerializedName("agegroupID")
        var agegroupID: String,
    @SerializedName("appuroleID")
        var appuroleID: String,
    @SerializedName("appuroleName")
        var appuroleName: Any,
    @SerializedName("apputypeID")
        var apputypeID: String,
    @SerializedName("apputypeName")
        var apputypeName: String,
    @SerializedName("clubID")
        var clubID: String,
    @SerializedName("clubName")
        var clubName: String,
    @SerializedName("clubs")
        var clubs: ArrayList<ClubData> = ArrayList(),
    @SerializedName("education")
        var education: ArrayList<EducationData> = ArrayList(),
    @SerializedName("employement")
        var employement: ArrayList<EmploymentData> = ArrayList(),
    @SerializedName("footballagecatID")
        var footballagecatID: String = "",
    @SerializedName("footballagecatName")
        var footballagecatName: String = "",
    @SerializedName("footbllevelID")
        var footbllevelID: String = "",
    @SerializedName("footbllevelName")
        var footbllevelName: String = "",
    @SerializedName("footbltypeID")
        var footbltypeID: String = "",
    @SerializedName("footbltypeName")
        var footbltypeName: String = "",
    @SerializedName("hashtags")
        var hashtags: ArrayList<Hashtags> = ArrayList(),
    @SerializedName("hobbies")
        var hobbies: ArrayList<Hobby> = ArrayList(),
    @SerializedName("languageID")
        var languageID: String = "",
    @SerializedName("languages")
        var languages: ArrayList<ProfileLanguageData> = ArrayList(),
    @SerializedName("location")
        var location: ArrayList<AddLocationData> = ArrayList(),
    @SerializedName("passport")
        var passport: ArrayList<PassportNationality> = ArrayList(),
    @SerializedName("skills")
        var skills: ArrayList<UsersSkils> = ArrayList(),
    @SerializedName("privacy")
        var privacy: ArrayList<Privacy> = ArrayList(),
    @SerializedName("settings")
        var settings: ArrayList<Setting> = ArrayList(),
    @SerializedName("specialityID")
        var specialityID: String = "",
    @SerializedName("TotalFollowerCount")
        var totalFollowerCount: String = "",
    @SerializedName("TotalFollowingCount")
        var totalFollowingCount: String = "",
    @SerializedName("TotalFriendCount")
        var totalFriendCount: String = "",
    @SerializedName("TotalPendingRequestCount")
        var totalPendingRequestCount: String = "",
    @SerializedName("TotalReceivedRequestCount")
        var totalReceivedRequestCount: String = "",
    @SerializedName("userAlternateEmail")
        var userAlternateEmail: String = "",
    @SerializedName("userAlternateMobile")
        var userAlternateMobile: String = "",
    @SerializedName("userBestFoot")
        var userBestFoot: String = "",
    @SerializedName("userBio")
        var userBio: String = "",
    @SerializedName("userCountryCode")
        var userCountryCode: String = "",
    @SerializedName("userCoverPhoto")
        var userCoverPhoto: String = "",
    @SerializedName("userCreatedDate")
        var userCreatedDate: String = "",
    @SerializedName("userDOB")
        var userDOB: String = "",
    @SerializedName("userDeviceID")
        var userDeviceID: String = "",
    @SerializedName("userDeviceType")
        var userDeviceType: String = "",
    @SerializedName("userEmail")
        var userEmail: String = "",
    @SerializedName("userFBID")
        var userFBID: String = "",
    @SerializedName("userFirstName")
        var userFirstName: String = "",
    @SerializedName("userFollowers")
        var userFollowers: String = "",
    @SerializedName("userFriendRequestPushBaseCount")
        var userFriendRequestPushBaseCount: String = "",
    @SerializedName("userFriends")
        var userFriends: String = "",
    @SerializedName("userFullname")
        var userFullname: String = "",
    @SerializedName("userGender")
        var userGender: String = "",
    @SerializedName("userGoogleID")
        var userGoogleID: String = "",
    @SerializedName("userHeight")
        var userHeight: String = "",
    @SerializedName("userHomeCityID")
        var userHomeCityID: String = "",
    @SerializedName("userHomeCityName")
        var userHomeCityName: String = "",
    @SerializedName("userHomeCountryID")
        var userHomeCountryID: String = "",
    @SerializedName("userHomeCountryName")
        var userHomeCountryName: String = "",
    @SerializedName("userID")
        var userID: String = "",
    @SerializedName("userLastName")
        var userLastName: String = "",
    @SerializedName("userLinkedinID")
        var userLinkedinID: String = "",
    @SerializedName("userMobile")
        var userMobile: String = "",
    @SerializedName("userNickname")
        var userNickname: String = "",
    @SerializedName("userOTP")
        var userOTP: String = "",
    @SerializedName("userOVerified")
        var userOVerified: String = "",
    @SerializedName("userPassword")
        var userPassword: String = "",
    @SerializedName("userProfilePicture")
        var userProfilePicture: String = "",
    @SerializedName("userPushBaseCount")
        var userPushBaseCount: String = "",
    @SerializedName("userReferKey")
        var userReferKey: String = "",
    @SerializedName("userStatus")
        var userStatus: String = "",
    @SerializedName("userWebsite")
        var userWebsite: String = "",
    @SerializedName("userWeight")
        var userWeight: String = "",
    @SerializedName("plyrposiID")
        var plyrposiID: String = "",
    @SerializedName("plyrposiName")
        var plyrposiName: String = "",
    @SerializedName("plyrposiTagss")
        var plyrposiTagss: String = "",
    @SerializedName("EmailNotification")
        var emailNotification: ArrayList<EmailNotification> = ArrayList(),
    @SerializedName("InAppSettings")
        var inAppSettings: ArrayList<InAppSetting> = ArrayList(),
    @SerializedName("IsYouAreBlocked")
        var isYouAreBlocked: String = "",
    @SerializedName("IsYouBlocked")
        var isYouBlocked: String = "",
    @SerializedName("IsYouFollowing")
        var isYouFollowing: String = "",
    @SerializedName("IsYouSentRequest")
        var isYouSentRequest: String = "",
    @SerializedName("IsYourFriend")
        var isYourFriend: String = "",
    @SerializedName("IsYourRequestRejected")
        var isYourRequestRejected: String = "",
    @SerializedName("MyPosts")
        var myPosts: List<Any>,
    @SerializedName("PushNotification")
        var pushNotification: ArrayList<PushNotification> = ArrayList(),
    @SerializedName("userInstaID")
        var userInstaID: Any,


    @SerializedName("contractsituationID")
        val contractsituationID: String = "",
    @SerializedName("contractsituationName")
        val contractsituationName: String = "",

    @SerializedName("geomobilityID")
        val geomobilityID: String = "",
    @SerializedName("geomobilityName")
        val geomobilityName: String = "",


    @SerializedName("leagueID")
        val leagueID: String = "",
    @SerializedName("leagueName")
        val leagueName: String = "",


    @SerializedName("outfitterIDs")
        val outfitterIDs: String = "",
    @SerializedName("outfitterNames")
        val outfitterNames: String = "",
    @SerializedName("previousclubName")
        val previousclubName: String,
    @SerializedName("teamcountryName")
        val teamcountryName: String = "",

    @SerializedName("useNationalGoals")
        val useNationalGoals: String = "",

    @SerializedName("userAppleID")
        val userAppleID: String = "",

    @SerializedName("userContractExpiryDate")
        val userContractExpiryDate: String = "",


    @SerializedName("userJersyNumber")
        val userJersyNumber: String = "",
    @SerializedName("userNationalCap")
        val userNationalCap: String = "",
    @SerializedName("userNationalCountryID")
        val userNationalCountryID: String = "",
    @SerializedName("userOrganisation")
        val userOrganisation: String = "",
    @SerializedName("userPosition")
        val userPosition: String = "",
    @SerializedName("userPreviousClubID")
        val userPreviousClubID: String = "",

    @SerializedName("userTitle")
        val userTitle: String = "",
    @SerializedName("userWhoAmI")
        val userWhoAmI: String = "",
    @SerializedName("usertrophies")
        val usertrophies: String = "",
    @SerializedName("footballleague")
        val footballleague: String = "",
    @SerializedName("websitetitle")
        val websitetitle: String = "",
    @SerializedName("websiteurl")
        val websiteurl: String = "",
    @SerializedName("userParentOTP")
        val userParentOTP: String,
    @SerializedName("userParetnOTPVerified")
        val userParetnOTPVerified: String,

    @SerializedName("userPhotoVerified")
        val userPhotoVerified: String,
    @SerializedName("userPlaceofBirth")
        val userPlaceofBirth: String="",
    @SerializedName("userProfileVerified")
        var userProfileVerified: String,
    @SerializedName("userProfileViewCount")
        val userProfileViewCount: String,

    @SerializedName("userSignedRefKey")
        val userSignedRefKey: String,
    @SerializedName("userParentMobile")
        val userParentMobile: String,

        //QuickBlock
    @SerializedName("userQBoxID")
        @Expose
        var userQBoxID: String? = "",
    @SerializedName("userContentLanguage")
        @Expose
        var userContentLanguage: String? = "",
    @SerializedName("userContentLanguageCode")
        @Expose
        var userContentLanguageCode: String? = "",
    @SerializedName("userContentLanguageID")
        @Expose
        var userContentLanguageID: String? = "",
    @SerializedName("userAgentName")
        @Expose
        var userAgentName: String? = "",





    @SerializedName("agegroupFrom")
    val agegroupFrom: String?,
    @SerializedName("agegroupTo")
    val agegroupTo: String?,
    @SerializedName("company")
    val company: List<Any>?,
    @SerializedName("IsYouGotRequest")
    val isYouGotRequest: String?,
    @SerializedName("jobinterestID")
    val jobinterestID: String?,
    @SerializedName("jobinterestName")
    val jobinterestName: Any?,
    @SerializedName("jobtypeIDs")
    val jobtypeIDs: String?,
    @SerializedName("userBusiness")
    val userBusiness: Any?,
    @SerializedName("userBusinessOverview")
    val userBusinessOverview: Any?,
    @SerializedName("userBusinessfollower")
    val userBusinessfollower: String?,
    @SerializedName("userJobtitle")
    val userJobtitle: Any?,
    @SerializedName("userMarketPlace")
    val userMarketPlace: Any?,
    @SerializedName("userParentEmail")
    val userParentEmail: Any?,
    @SerializedName("userTalent")
    val userTalent: Any?,
    @SerializedName("walletbalance")
    val walletbalance: String?


    ) : Serializable

data class EmailNotification(
        @SerializedName("uensComment")
        var uensComment: String = "",
        @SerializedName("uensCreatedDate")
        var uensCreatedDate: String = "",
        @SerializedName("uensFollowRequest")
        var uensFollowRequest: String = "",
        @SerializedName("uensFriendRequest")
        var uensFriendRequest: String = "",
        @SerializedName("uensID")
        var uensID: String = "",
        @SerializedName("uensLike")
        var uensLike: String = "",
        @SerializedName("uensVote")
        var uensVote: String = "",
        @SerializedName("userID")
        var userID: String = ""
) : Serializable



data class InAppSetting(
        @SerializedName("userID")
        var userID: String = "",
        @SerializedName("usnsComment")
        var usnsComment: String = "",
        @SerializedName("usnsCreatedDate")
        var usnsCreatedDate: String = "",
        @SerializedName("usnsFollowRequest")
        var usnsFollowRequest: String = "",
        @SerializedName("usnsFriendRequest")
        var usnsFriendRequest: String = "",
        @SerializedName("usnsID")
        var usnsID: String = "",
        @SerializedName("usnsLike")
        var usnsLike: String = "",
        @SerializedName("usnsVote")
        var usnsVote: String = ""
) : Serializable

data class Privacy(
        @SerializedName("userID")
        var userID: String = "",
        @SerializedName("userprivacysettingsACR")
        var userprivacysettingsACR: String = "",
        @SerializedName("userprivacysettingsAFME")
        var userprivacysettingsAFME: String = "",
        @SerializedName("userprivacysettingsDate")
        var userprivacysettingsDate: String = "",
        @SerializedName("userprivacysettingsID")
        var userprivacysettingsID: String = "",
        @SerializedName("userprivacysettingsWCSYFL")
        var userprivacysettingsWCSYFL: String = "",
        @SerializedName("userprivacysettingsWCSYFP")
        var userprivacysettingsWCSYFP: String = "",
        @SerializedName("userprivacysettingsWCSYI")
        var userprivacysettingsWCSYI: String = "",
        @SerializedName("userprivacysettingsWCSYM")
        var userprivacysettingsWCSYM: String = "",
        @SerializedName("userprivacysettingsWCTYIP")
        var userprivacysettingsWCTYIP: String = ""


) : Serializable

data class Setting(
        @SerializedName("settingsMasterOtp")
        val settingsMasterOtp: String,
        @SerializedName("settingsMinorAge")
        val settingsMinorAge: String,
        @SerializedName("settingsSupportEmail")
        var settingsSupportEmail: String = "",
        @SerializedName("settingsSupportMobile")
        var settingsSupportMobile: String = "",
        @SerializedName("settingsSupportPhone")
        var settingsSupportPhone: String = ""
) : Serializable


data class Location(
    @SerializedName("cityID")
    val cityID: String?,
    @SerializedName("cityName")
    val cityName: String?,
    @SerializedName("countryID")
    val countryID: String?,
    @SerializedName("countryName")
    val countryName: Any?,
    @SerializedName("userID")
    val userID: String?,
    @SerializedName("userlocationID")
    val userlocationID: String?,
    @SerializedName("userlocationPincode")
    val userlocationPincode: String?,
    @SerializedName("userlocationType")
    val userlocationType: String?
)


data class PushNotification(
    @SerializedName("upnsChat")
    val upnsChat: String?,
    @SerializedName("upnsComment")
    val upnsComment: String?,
    @SerializedName("upnsCreatedDate")
    val upnsCreatedDate: String?,
    @SerializedName("upnsFollowRequest")
    val upnsFollowRequest: String?,
    @SerializedName("upnsFriendRequest")
    val upnsFriendRequest: String?,
    @SerializedName("upnsID")
    val upnsID: String?,
    @SerializedName("upnsLike")
    val upnsLike: String?,
    @SerializedName("upnsNotification")
    val upnsNotification: String?,
    @SerializedName("upnsTag")
    val upnsTag: String?,
    @SerializedName("userID")
    val userID: String?
):Serializable




