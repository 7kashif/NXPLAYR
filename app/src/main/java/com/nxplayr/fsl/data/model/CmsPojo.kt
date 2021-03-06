package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName

data class CmsPojo(
    @SerializedName("data")
        var `data`: List<CMSPageData>,
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
)

data class CMSPageData(
        @SerializedName("url")
        var url: String,
        @SerializedName("cmspageID")
        var cmspageID: String,
        @SerializedName("cmspageContents")
        var cmspageContents: String,
        @SerializedName("cmspageFrenchContents")
        var cmspageFrenchContents: String,
        @SerializedName("cmspageName")
        var cmspageName: String
)