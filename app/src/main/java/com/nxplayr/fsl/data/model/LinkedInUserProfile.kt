package com.nxplayr.fsl.data.model


import com.google.gson.annotations.SerializedName

data class LinkedInUserProfile(
    @SerializedName("firstName")
    var firstName: FirstName? = FirstName(),
    @SerializedName("id")
    var id: String? = "",
    @SerializedName("lastName")
    var lastName: LastName? = LastName(),
    @SerializedName("profilePicture")
    var profilePicture: ProfilePicture? = ProfilePicture()
)

data class LastName(
    @SerializedName("localized")
    var localized: Localized? = Localized(),
    @SerializedName("preferredLocale")
    var preferredLocale: PreferredLocale? = PreferredLocale()
)

data class PreferredLocale(
    @SerializedName("country")
    var country: String? = "",
    @SerializedName("language")
    var language: String? = ""
)

data class Localized(
    @SerializedName("en_US")
    var enUS: String? = ""
)

data class FirstName(
    @SerializedName("localized")
    var localized: Localized? = Localized(),
    @SerializedName("preferredLocale")
    var preferredLocale: PreferredLocale? = PreferredLocale()
)

data class ProfilePicture(
    @SerializedName("displayImage")
    var displayImage: String? = ""
)