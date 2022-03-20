package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class FootballLevelListPojo(
    @SerializedName("data")
    var `data`: List<FootballLevelListData> = listOf(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: String = ""
): Serializable

data class FootballLevelListData(
    @SerializedName("footbltypeCreatedDate")
    var footbltypeCreatedDate: String = "",
    @SerializedName("footbltypeID")
    var footbltypeID: String = "",
    @SerializedName("footbltypeImage")
    var footbltypeImage: String = "",
    @SerializedName("footbltypeName")
    var footbltypeName: String = "",
    @SerializedName("footbltypeRemarks")
    var footbltypeRemarks: String = "",
    @SerializedName("footbltypeStatus")
    var footbltypeStatus: String = "",
    var checked: Boolean = false

): Serializable