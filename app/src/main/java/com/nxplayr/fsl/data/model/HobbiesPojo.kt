package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName


data class HobbiesPojo(
    @SerializedName("data")
        var `data`: List<Hobby> = listOf(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
)

data class Hobby(
        @SerializedName("hobbyID")
        var hobbyID: String = "",
        @SerializedName("hobbyName")
        var hobbyName: String = "",
        @SerializedName("userID")
        var userID: String = "",
        @SerializedName("userhobbiesDate")
        var userhobbiesDate: String = "",
        @SerializedName("userhobbiesID")
        var userhobbiesID: String = ""
)
