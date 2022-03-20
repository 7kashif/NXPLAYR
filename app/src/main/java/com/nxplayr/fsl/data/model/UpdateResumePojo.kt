package com.nxplayr.fsl.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UpdateResumePojo(

//        @SerializedName("data")
//        var `data`: List<UserBasicInfo?>? = listOf(),
        @SerializedName("message")
        var message: String,
        @SerializedName("status")
        var status: String
): Serializable


data class UserBasicInfo (

    @SerializedName("userID")
        @Expose
        var userID: String? = "",
    @SerializedName("userFirstName")
        @Expose
        var userFirstname: String? = "",
    @SerializedName("userLastName")
        @Expose
        var userLastname: String? = "",
    @SerializedName("userEmail")
        @Expose
        var userEmail: String? = "",
    @SerializedName("userCountryCode")
        @Expose
        var userCountryCode: String? = "",
    @SerializedName("userMobile")
        @Expose
        var userMobile: String? = "",
    @SerializedName("userGender")
        @Expose
        var usergender: String? = "",
    @SerializedName("userPassword")
        @Expose
        var userPassword: String? = "",
    @SerializedName("userProfilePicture")
        @Expose
        var userProfilepicture: String? = "",
    @SerializedName("languageID")
        @Expose
        var languageiD: String? = "",
    @SerializedName("userDeviceType")
        @Expose
        var userDeviceType: String? = "",
    @SerializedName("userDeviceID")
        @Expose
        var userDeviceID: String? = "",
    @SerializedName("userOVerified")
        @Expose
        var userOVerified: String? = "",
    @SerializedName("userStatus")
        @Expose
        var userStatus: String? = "",
    @SerializedName("userOTP")
        @Expose
        var userOTP: String? = "",
    @SerializedName("apputypeID")
        @Expose
        var apputypeID: String? = "",
    @SerializedName("appuroleID")
        @Expose
        var appuroleID: String? = "",
    @SerializedName("footbltypeID")
        @Expose
        var footbltypeid: String? = "",
    @SerializedName("specialityID")
        @Expose
        var specialityID: String? = "",
    @SerializedName("userFBID")
        @Expose
        var userFBID: String? = "",
    @SerializedName("userReferKey")
        @Expose
        var userReferKey: String? = "",
    @SerializedName("userGoogleID")
        @Expose
        var userGoogleID: Any? = "",
    @SerializedName("userInstaID")
        @Expose
        var userInstaID: String? = "",
    @SerializedName("userLinkedinID")
        @Expose
        var userLinkedinID: String? = "",
    @SerializedName("userAppleID")
        @Expose
        var userAppleID: String? = "",
    @SerializedName("agegroupID")
        @Expose
        var agegroupiD: String? = "",
    @SerializedName("userCoverPhoto")
        @Expose
        var userCoverPhoto: String? = "",
    @SerializedName("userBio")
        @Expose
        var userBio: String? = "",
    @SerializedName("userFullname")
        @Expose
        var userFullname: String? = "",
    @SerializedName("userBestFoot")
        @Expose
        var userBestFoot: String? = "",
    @SerializedName("footballagecatID")
        @Expose
        var footballagecatiD: String? = "",
    @SerializedName("userDOB")
        @Expose
        var userDOB: String? = "",
    @SerializedName("userHeight")
        @Expose
        var userHeight: String? = "",
    @SerializedName("userWeight")
        @Expose
        var userWeight: String? = "",
    @SerializedName("userCreatedDate")
        @Expose
        var userCreatedDate: String? = "",
    @SerializedName("userAlternateEmail")
        @Expose
        var userAlternateEmail: String? = "",
    @SerializedName("userWebsite")
        @Expose
        var userWebsite: String? = "",
    @SerializedName("userAlternateMobile")
        @Expose
        var userAlternateMobile: String? = "",
    @SerializedName("userNickname")
        @Expose
        var userNickname: String? = "",
    @SerializedName("footbllevelID")
        @Expose
        var footbllevelID: String? = "",
    @SerializedName("clubID")
        @Expose
        var clubID: String? = "",
    @SerializedName("userFriends")
        @Expose
        var userFriends: String? = "",
    @SerializedName("userFollowers")
        @Expose
        var userFollowers: String? = "",
    @SerializedName("userPushBaseCount")
        @Expose
        var userPushBaseCount: String? = "",
    @SerializedName("userFriendRequestPushBaseCount")
        @Expose
        var userFriendRequestPushBaseCount: String? = "",
    @SerializedName("userHomeCityID")
        @Expose
        var userHomeCityID: String? = "",
    @SerializedName("userHomeCountryID")
        @Expose
        var userHomeCountryID: String? = "",
    @SerializedName("plyrposiID")
        @Expose
        var plyrposiID: String? = "",
    @SerializedName("userTitle")
        @Expose
        var userTitle: String? = "",
    @SerializedName("userPosition")
        @Expose
        var userPosition: String? = "",
    @SerializedName("userOrganisation")
        @Expose
        var userOrganisation: String? = "",
    @SerializedName("userWhoAmI")
        @Expose
        var userWhoAmI: String? = "",
    @SerializedName("leagueID")
        @Expose
        var leagueID: String? = "",
    @SerializedName("contractsituationID")
        @Expose
        var contractsituationID: String? = "",
    @SerializedName("userContractExpiryDate")
        @Expose
        var userContractExpiryDate: String? = "",
    @SerializedName("userPreviousClubID")
        @Expose
        var userPreviousClubID: String? = "",
    @SerializedName("userJersyNumber")
        @Expose
        var userJersyNumber: String? = "",
    @SerializedName("outfitterIDs")
        @Expose
        var outfitterIDs: String? = "",
    @SerializedName("usertrophies")
        @Expose
        var usertrophies: String? = "",
    @SerializedName("geomobilityID")
        @Expose
        var geomobilityID: String? = "",
    @SerializedName("userNationalCountryID")
        @Expose
        var userNationalCountryID: String? = "",
    @SerializedName("userNationalCap")
        @Expose
        var userNationalCap: String? = "",
    @SerializedName("useNationalGoals")
        @Expose
        var useNationalGoals: String? = "",
    @SerializedName("clubName")
        @Expose
        var clubName: String? = "",
    @SerializedName("footbllevelName")
        @Expose
        var footbllevelName: String? = "",
    @SerializedName("footballagecatName")
        @Expose
        var footballagecatName: String? = "",
    @SerializedName("footbltypeName")
        @Expose
        var footbltypeName: String? = "",
    @SerializedName("appuroleName")
        @Expose
        var appuroleName: String? = "",
    @SerializedName("apputypeName")
        @Expose
        var apputypeName: String? = "",
    @SerializedName("userHomeCityName")
        @Expose
        var userHomeCityName: String? = "",
    @SerializedName("userHomeCountryName")
        @Expose
        var userHomeCountryName: String? = "",
    @SerializedName("previousclubName")
        @Expose
        var previousclubName: String? = "",
    @SerializedName("leagueName")
        @Expose
        var leagueName: String? = "",
    @SerializedName("contractsituationName")
        @Expose
        var contractsituationName: String? = "",
    @SerializedName("geomobilityName")
        @Expose
        var geomobilityName: String? = "",
    @SerializedName("teamcountryName")
        @Expose
        var teamcountryName: String? = "",
    @SerializedName("plyrposiName")
        @Expose
        var plyrposiName: String? = "",
    @SerializedName("plyrposiTagss")
        @Expose
        var plyrposiTagss: String? = "",
    @SerializedName("outfitterNames")
        @Expose
        var outfitterNames: String? = "",
    @SerializedName("languages")
        @Expose
        var languages: List<LanguagesIntro>? = listOf(),
    @SerializedName("employement")
        @Expose
        var employement: List<Employement>? = listOf(),
    @SerializedName("passport")
        @Expose
        var passport: List<Passport>? = listOf(),
    @SerializedName("location")
        @Expose
        var location: List<LocationInfo>? = listOf(),
    @SerializedName("hobbies")
        @Expose
        var hobbies: List<Hobbies>? = listOf(),
    @SerializedName("education")
        @Expose
        var education: List<Education>? = listOf(),
    @SerializedName("skills")
        @Expose
        var skills: List<Skills>? = listOf(),
    @SerializedName("clubs")
        @Expose
        var clubs: List<Clubs>? = listOf(),
    @SerializedName("hashtags")
        @Expose
        var hashtags: List<HashtagsData>? = listOf(),
    @SerializedName("MyPosts")
        @Expose
        var MyPosts: Any= "",
    @SerializedName("IsYourFriend")
        @Expose
        var IsYourFriend: String? = "",
    @SerializedName("IsYouFollowing")
        @Expose
        var IsYouFollowing: String? = "",
    @SerializedName("TotalFriendCount")
        @Expose
        var TotalFriendCount: String? = "",
    @SerializedName("IsYouSentRequest")
        @Expose
        var IsYouSentRequest: String? = "",
    @SerializedName("IsYourRequestRejected")
        @Expose
        var IsYourRequestRejected: String? = "",
    @SerializedName("IsYouBlocked")
        @Expose
        var IsYouBlocked: String? = "",
    @SerializedName("IsYouAreBlocked")
        @Expose
        var IsYouAreBlocked: String? = "",
    @SerializedName("TotalFollowingCount")
        @Expose
        var TotalFollowingCount: String? = "",
    @SerializedName("TotalFollowerCount")
        @Expose
        var TotalFollowerCount: String? = "",
    @SerializedName("TotalPendingRequestCount")
        @Expose
        var TotalPendingRequestCount: Any? = "",
    @SerializedName("TotalReceivedRequestCount")
        @Expose
        var TotalReceivedRequestCount: String? = "",
    @SerializedName("privacy")
        @Expose
        var privacy: List<Privacy>? = listOf(),
    @SerializedName("EmailNotification")
        @Expose
        var EmailNotification: Any = "",
    @SerializedName("PushNotification")
        @Expose
        var PushNotification: Any = "",
    @SerializedName("InAppSettings")
        @Expose
        var InAppSettings: Any = "",
    @SerializedName("settings")
        @Expose
        var settings: List<SettingData>? = listOf()
): Serializable

data class LanguagesIntro(
        @SerializedName("userlanguageID")
        @Expose
        var userlanguageID: String? = "",
        @SerializedName("languageID")
        @Expose
        var languageID: String? = "",
        @SerializedName("profiencyID")
        @Expose
        var profiencyID: String? = "",
        @SerializedName("userID")
        @Expose
        var userID: String? = "",
        @SerializedName("profiencyName")
        @Expose
        var profiencyName: String? = "",
        @SerializedName("languageName")
        @Expose
        var languageName: String? = ""
): Serializable

data class LocationInfo(
        @SerializedName("userlocationID")
        @Expose
        var userlocationID: String? = "",
        @SerializedName("userID")
        @Expose
        var userID: String? = "",
        @SerializedName("countryID")
        @Expose
        var countryID: String? = "",
        @SerializedName("userlocationPincode")
        @Expose
        var userlocationPincode: String? = "",
        @SerializedName("cityID")
        @Expose
        var cityID: String? = "",
        @SerializedName("userlocationType")
        @Expose
        var userlocationType: String? = "",
        @SerializedName("countryName")
        @Expose
        var countryName: String? = "",
        @SerializedName("cityName")
        @Expose
        var cityName: Any? = ""
):Serializable

data class Hobbies(

        @SerializedName("userhobbiesID")
        @Expose
        var userhobbiesID: String? = "",
        @SerializedName("userID")
        @Expose
        var userID: String? = "",
        @SerializedName("hobbyID")
        @Expose
        var hobbyID: String? = "",
        @SerializedName("userhobbiesDate")
        @Expose
        var userhobbiesDate: String? = "",
        @SerializedName("hobbyName")
        @Expose
        var hobbyName: String? = ""
):Serializable


data class Skills(

        @SerializedName("userskillID")
        @Expose
        var userskillID: String? = "",
        @SerializedName("userID")
        @Expose
        var userID: String? = "",
        @SerializedName("skillID")
        @Expose
        var skillID: String? = "",
        @SerializedName("userskillCreatedDate")
        @Expose
        var userskillCreatedDate: String? = "",
        @SerializedName("skillName")
        @Expose
        var skillName: String? = ""
):Serializable

data class Clubs(

        @SerializedName("userclubID")
        @Expose
        var userclubID: String? = "",
        @SerializedName("userID")
        @Expose
        var userID: String? = "",
        @SerializedName("clubID")
        @Expose
        var clubID: String? = "",
        @SerializedName("clubName")
        @Expose
        var clubName: String? = ""
):Serializable

data class HashtagsData(

        @SerializedName("userhashtagID")
        @Expose
        var userhashtagID: String? = "",
        @SerializedName("hashtagID")
        @Expose
        var hashtagID: String? = "",
        @SerializedName("userID")
        @Expose
        var userID: String? = "",
        @SerializedName("hashtagName")
        @Expose
        var hashtagName: String? = ""
):Serializable

data class SettingData(
        @SerializedName("settingsSupportEmail")
        @Expose
        var settingsSupportEmail: String? = "",
        @SerializedName("settingsSupportMobile")
        @Expose
        var settingsSupportMobile: String? = "",
        @SerializedName("settingsSupportPhone")
        @Expose
        var settingsSupportPhone: String? = ""
):Serializable