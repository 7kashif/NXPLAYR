package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ProfileLanguagePojo(
    @SerializedName("data")
        var `data`: List<ProfileLanguageData> = listOf(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
):Serializable

data class ProfileLanguageData(
        @SerializedName("languageID")
        var languageID: String,
        @SerializedName("languageName")
        var languageName: String,
        @SerializedName("profiencyID")
        var profiencyID: String,
        @SerializedName("profiencyName")
        var profiencyName: String,
        @SerializedName("userID")
        var userID: String,
        @SerializedName("userlanguageID")
        var userlanguageID: String
):Serializable