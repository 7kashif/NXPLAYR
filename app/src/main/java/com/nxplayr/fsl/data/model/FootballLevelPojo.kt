package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName


data class FootballLevelPojo(
    @SerializedName("data")
        var `data`: List<FootballLevelData> = listOf(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
)

data class FootballLevelData(
        @SerializedName("footbllevelCreatedDate")
        var footbllevelCreatedDate: String,
        @SerializedName("footbllevelID")
        var footbllevelID: String,
        @SerializedName("footbllevelName")
        var footbllevelName: String,
        @SerializedName("footbllevelRemarks")
        var footbllevelRemarks: String,
        @SerializedName("footbllevelStatus")
        var footbllevelStatus: String,
        var isSelect: Boolean = false
)