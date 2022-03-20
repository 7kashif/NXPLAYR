package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class EducationPojo(
    @SerializedName("data")
        var `data`: List<EducationData> = listOf(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
):Serializable

data class EducationData(
    @SerializedName("degreeID")
        var degreeID: String,
    @SerializedName("degreeName")
        var degreeName: String,
    @SerializedName("languageID")
        var languageID: String,
    @SerializedName("media")
        var media: ArrayList<EducationMedia> = ArrayList(),
    @SerializedName("universityID")
        var universityID: String,
    @SerializedName("universityName")
        var universityName: String,
    @SerializedName("userID")
        var userID: String,
    @SerializedName("usereducationCreatedDate")
        var usereducationCreatedDate: String,
    @SerializedName("usereducationDescription")
        var usereducationDescription: String,
    @SerializedName("usereducationGrade")
        var usereducationGrade: String,
    @SerializedName("usereducationID")
        var usereducationID: String,
    @SerializedName("usereducationIsDeleted")
        var usereducationIsDeleted: String,
    @SerializedName("usereducationPeriodOfTimeFrom")
        var usereducationPeriodOfTimeFrom: String,
    @SerializedName("usereducationPeriodOfTimeTo")
        var usereducationPeriodOfTimeTo: String,
    @SerializedName("usereducationPrivarcyData")
        var usereducationPrivarcyData: String,
    @SerializedName("usereducationPrivarcyType")
        var usereducationPrivarcyType: String,
    @SerializedName("usereducationStatus")
        var usereducationStatus: String
):Serializable

data class EducationMedia(
        @SerializedName("userID")
        var userID: String,
        @SerializedName("usereducationID")
        var usereducationID: String,
        @SerializedName("useredumediaDescription")
        var useredumediaDescription: String,
        @SerializedName("useredumediaFile")
        var useredumediaFile: String,
        @SerializedName("useredumediaID")
        var useredumediaID: String,
        @SerializedName("useredumediaTitle")
        var useredumediaTitle: String,
        @SerializedName("useredumediaType")
        var useredumediaType: String
):Serializable