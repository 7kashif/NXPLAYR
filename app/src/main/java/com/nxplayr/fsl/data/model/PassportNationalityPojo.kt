package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class PassportNationalityPojo(
    @SerializedName("data")
        var `data`: List<PassportNationality> = listOf(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
):Serializable

data class PassportNationality(
        @SerializedName("countryID")
        var countryID: String,
        @SerializedName("countryName")
        var countryName: String,
        @SerializedName("userID")
        var userID: String,
        @SerializedName("userpassport")
        var userpassport: String,
        var checked:Boolean=false
):Serializable