package com.nxplayr.fsl.data.model
import com.google.gson.annotations.SerializedName


data class UserDegreeListPojo(
    @SerializedName("data")
    var `data`: List<DegreeListData>,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: String
)

data class DegreeListData(
    @SerializedName("degreeCreatedDate")
    var degreeCreatedDate: String,
    @SerializedName("degreeID")
    var degreeID: String,
    @SerializedName("degreeName")
    var degreeName: String,
    @SerializedName("degreeRemarks")
    var degreeRemarks: Any,
    @SerializedName("degreeStatus")
    var degreeStatus: String
)