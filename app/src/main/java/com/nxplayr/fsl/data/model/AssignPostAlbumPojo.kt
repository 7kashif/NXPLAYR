package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName

data class AssignPostAlbumPojo(

        @SerializedName("status")
        var status: String?,
        @SerializedName("message")
        var message: String?
)