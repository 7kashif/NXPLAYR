package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName

data class FootballAgeCategoryPojo(
    @SerializedName("data")
        var `data`: List<AgecategoryList> = listOf(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
)

data class AgecategoryList(
        @SerializedName("footballagecatCreatedDate")
        var footballagecatCreatedDate: String,
        @SerializedName("footballagecatID")
        var footballagecatID: String,
        @SerializedName("footballagecatName")
        var footballagecatName: String,
        @SerializedName("footballagecatRemarks")
        var footballagecatRemarks: String,
        @SerializedName("footballagecatStatus")
        var footballagecatStatus: String,
        var isSelect: Boolean = false

)