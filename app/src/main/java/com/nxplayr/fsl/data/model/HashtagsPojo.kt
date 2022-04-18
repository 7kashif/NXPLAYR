package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class HashtagsPojo(
    @SerializedName("data")
    var `data`: List<Hashtags> = listOf(),
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: String
) : Serializable

data class Hashtags(
    @SerializedName("hashtagID")
    var hashtagID: String,
    @SerializedName("hashtagName")
    var hashtagName: String,
    @SerializedName("userID")
    var userID: String,
    @SerializedName("userhashtagID")
    var userhashtagID: String
) : Serializable