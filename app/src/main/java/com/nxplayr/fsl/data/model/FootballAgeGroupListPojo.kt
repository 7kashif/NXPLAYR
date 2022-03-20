package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable



data class FootballAgeGroupListPojo(
    @SerializedName("data")
    var `data`: List<FootballAgeGroupListData> = listOf(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: String = ""
)

data class FootballAgeGroupListData(
    @SerializedName("agegroupCreatedDate")
    var agegroupCreatedDate: String = "",
    @SerializedName("agegroupFrom")
    var agegroupFrom: String = "",
    @SerializedName("agegroupID")
    var agegroupID: String = "",
    @SerializedName("agegroupRemark")
    var agegroupRemark: String = "",
    @SerializedName("agegroupStatus")
    var agegroupStatus: String = "",
    @SerializedName("agegroupTo")
    var agegroupTo: String = "",
    var status1: Boolean = false

) : Serializable