package com.nxplayr.fsl.data.model
import com.google.gson.annotations.SerializedName


data class UserUniversityListPojo(
    @SerializedName("data")
    var `data`: List<UniversityListData>,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: String
)

data class UniversityListData(
    @SerializedName("universityCreatedDate")
    var universityCreatedDate: String,
    @SerializedName("universityID")
    var universityID: String,
    @SerializedName("universityName")
    var universityName: String,
    @SerializedName("universityRemarks")
    var universityRemarks: String,
    @SerializedName("universityStatus")
    var universityStatus: String
)