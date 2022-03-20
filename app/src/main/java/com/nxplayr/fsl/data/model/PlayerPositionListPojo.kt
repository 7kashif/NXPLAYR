package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName


data class PlayerPositionListPojo(
    @SerializedName("data")
        var `data`: List<PlayerPosData> = listOf(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
)

data class PlayerPosData(
        @SerializedName("plyrposiCreatedDate")
        var plyrposiCreatedDate: String,
        @SerializedName("plyrposiID")
        var plyrposiID: String,
        @SerializedName("plyrposiName")
        var plyrposiName: String,
        @SerializedName("plyrposiRemarks")
        var plyrposiRemarks: String,
        @SerializedName("plyrposiStatus")
        var plyrposiStatus: String,
        @SerializedName("plyrposiTagss")
        var plyrposiTagss: String,
        var isChecked: Boolean = false
)