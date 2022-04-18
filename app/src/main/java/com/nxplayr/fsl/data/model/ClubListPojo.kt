package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ClubListPojo(
    @SerializedName("data")
        var `data`: ArrayList<ClubListData> = ArrayList(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
):Serializable

data class ClubListData(
        @SerializedName("clubCreatedDate")
        var clubCreatedDate: String,
        @SerializedName("clubID")
        var clubID: String,
        @SerializedName("clubName")
        var clubName: String,
        @SerializedName("clubRemarks")
        var clubRemarks: String,
        @SerializedName("clubStatus")
        var clubStatus: String,
        @SerializedName("userID")
        var userID: String,
        @SerializedName("userclubID")
        var userclubID: String,
        var selected: Boolean
):Serializable