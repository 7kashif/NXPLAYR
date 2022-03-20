package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName


data class ProficiencyPojo(
    @SerializedName("data")
        var `data`: List<ProficiencyData>,
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
)

data class  ProficiencyData(
        @SerializedName("profiencyCreatedDate")
        var profiencyCreatedDate: String,
        @SerializedName("profiencyID")
        var profiencyID: String,
        @SerializedName("profiencyName")
        var profiencyName: String,
        @SerializedName("profiencyRemark")
        var profiencyRemark: Any,
        @SerializedName("profiencyStatus")
        var profiencyStatus: String
)