package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName


data class HashtagListPojo(
    @SerializedName("data")
        var `data`: List<HashtagsList> = listOf(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
)

data class HashtagsList(
        @SerializedName("hashtagID")
        var hashtagID: String,
        @SerializedName("hashtagName")
        var hashtagName: String,
        @SerializedName("hashtagStatus")
        var hashtagStatus: String
)