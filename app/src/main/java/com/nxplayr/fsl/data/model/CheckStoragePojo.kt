package com.nxplayr.fsl.data.model


import com.google.gson.annotations.SerializedName

data class CheckStoragePojo(
    @SerializedName("message")
    val message: String,
    @SerializedName("spaceused")
    val spaceused: Int,
    @SerializedName("status")
    val status: String
)