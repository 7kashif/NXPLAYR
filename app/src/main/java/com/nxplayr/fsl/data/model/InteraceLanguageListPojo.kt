package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class InteraceLanguageListPojo(
    @SerializedName("data")
        var `data`: List<LanguageListData> = listOf(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status:  Boolean?=false
) : Serializable

data class LanguageListData(
        @SerializedName("languageFlag")
        var languageFlag: String,
        @SerializedName("languageID")
        var languageID: String,
        @SerializedName("languageName")
        var languageName: String,
        @SerializedName("languageCode")
        var languageCode: String,
        var status: Boolean = false
) : Serializable
