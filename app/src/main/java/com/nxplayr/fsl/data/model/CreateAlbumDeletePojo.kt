package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CreateAlbumDeletePojo (

        @SerializedName("message")
        var message: String? = "",
        @SerializedName("status")
        var status: String? = ""
): Serializable