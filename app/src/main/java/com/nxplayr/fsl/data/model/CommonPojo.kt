package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName


data class CommonPojo(
        @SerializedName("message")
        var message: String = "",
        @SerializedName("status")
        var status: String = "",
        @SerializedName("data")
        var `data`: List<SignupData>
)