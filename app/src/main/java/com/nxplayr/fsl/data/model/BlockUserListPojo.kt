package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class BlockUserListPojo(
    @SerializedName("data")
        var `data`: List<BlockUserData> = listOf(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
) : Serializable

data class BlockUserData(
        @SerializedName("userFirstName")
        var userFirstName: String,
        @SerializedName("userID")
        var userID: String,
        @SerializedName("userLastName")
        var userLastName: String,
        @SerializedName("userProfilePicture")
        var userProfilePicture: String
) : Serializable