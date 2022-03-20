package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AddClubPojo(
    @SerializedName("data")
        var `data`: List<ClubData> = listOf(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
):Serializable

data class ClubData(
        @SerializedName("clubID")
        var clubID: String,
        @SerializedName("clubName")
        var clubName: String,
        @SerializedName("userID")
        var userID: String,
        @SerializedName("userclubID")
        var userclubID: String
):Serializable