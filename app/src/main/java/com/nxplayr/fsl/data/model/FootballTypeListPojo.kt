package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable



data class FootballTypeListPojo(
    @SerializedName("data")
    var `data`: List<FootballTypeListData> = listOf(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: String = ""
):Serializable

data class FootballTypeListData(
    @SerializedName("appuroleCreatedDate")
    var appuroleCreatedDate: String = "",
    @SerializedName("appuroleID")
    var appuroleID: String = "",
    @SerializedName("appuroleImage")
    var appuroleImage: String = "",
    @SerializedName("appuroleName")
    var appuroleName: String = "",
    @SerializedName("appuroleRemarks")
    var appuroleRemarks: String = "",
    @SerializedName("appuroleStatus")
    var appuroleStatus: String = "",
    @SerializedName("apputypeID")
    var apputypeID: String = "",
    var checked: Boolean = false

):Serializable