package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName

data class ReportReasonPojo(
    @SerializedName("data")
        var `data`: List<ReportReasonData> = listOf(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
)

data class ReportReasonData(
        @SerializedName("postreportreasonDescription")
        var postreportreasonDescription: String,
        @SerializedName("postreportreasonID")
        var postreportreasonID: String,
        @SerializedName("postreportreasonName")
        var postreportreasonName: String,
        var isChecked: Boolean = false
)