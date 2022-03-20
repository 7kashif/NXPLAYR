package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName


data class LanguagesPojo(
    @SerializedName("data")
        var `data`: List<LanguageList>,
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
)

data class LanguageList(
        @SerializedName("languageID")
        var languageID: String,
        @SerializedName("languageName")
        var languageName: String,
        var status: Boolean = false
)