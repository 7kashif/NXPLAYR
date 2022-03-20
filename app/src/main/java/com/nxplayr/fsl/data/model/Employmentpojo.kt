package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Employmentpojo(
    @SerializedName("data")
        var `data`: List<EmploymentData> = listOf(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
):Serializable

data class EmploymentData(
    @SerializedName("cityID")
        var cityID: String,
    @SerializedName("cityName")
        var cityName: String,
    @SerializedName("companyID")
        var companyID: String,
    @SerializedName("companyName")
        var companyName: String,
    @SerializedName("countryID")
        var countryID: String,
    @SerializedName("countryName")
        var countryName: String,
    @SerializedName("jobfuncID")
        var jobfuncID: String,
    @SerializedName("jobfuncName")
        var jobfuncName: String,
    @SerializedName("languageID")
        var languageID: String,
    @SerializedName("media")
        var media: ArrayList<Media> = ArrayList(),
    @SerializedName("stateID")
        var stateID: String,
    @SerializedName("stateName")
        var stateName: String,
    @SerializedName("userID")
        var userID: String,
    @SerializedName("useremployementCityText")
        var useremployementCityText: String,
    @SerializedName("useremployementCountryText")
        var useremployementCountryText: String,
    @SerializedName("useremployementCreatedDate")
        var useremployementCreatedDate: String,
    @SerializedName("useremployementDescription")
        var useremployementDescription: String,
    @SerializedName("useremployementID")
        var useremployementID: String,
    @SerializedName("useremployementIsCurrent")
        var useremployementIsCurrent: String,
    @SerializedName("useremployementIsDeleted")
        var useremployementIsDeleted: String,
    @SerializedName("useremployementLatitude")
        var useremployementLatitude: String,
    @SerializedName("useremployementLongitude")
        var useremployementLongitude: String,
    @SerializedName("useremployementPeriodOfTimeFrom")
        var useremployementPeriodOfTimeFrom: String,
    @SerializedName("useremployementPeriodOfTimeTo")
        var useremployementPeriodOfTimeTo: String,
    @SerializedName("useremployementPrivacyData")
        var useremployementPrivacyData: String,
    @SerializedName("useremployementPrivacyType")
        var useremployementPrivacyType: String,
    @SerializedName("useremployementStatus")
        var useremployementStatus: String
):Serializable

data class Media(
        @SerializedName("userID")
        var userID: String,
        @SerializedName("useremployementID")
        var useremployementID: String,
        @SerializedName("userempmediaDescription")
        var userempmediaDescription: String,
        @SerializedName("userempmediaFile")
        var userempmediaFile: String,
        @SerializedName("userempmediaID")
        var userempmediaID: String,
        @SerializedName("userempmediaTitle")
        var userempmediaTitle: String,
        @SerializedName("userempmediaType")
        var userempmediaType: String
):Serializable