package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ConnectionTypePojo(
    @SerializedName("data")
        var `data`: List<ConnectionListData> = listOf(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
) : Serializable

data class ConnectionListData(
        @SerializedName("conntypeCreatedDate")
        var conntypeCreatedDate: String,
        @SerializedName("conntypeDisplayOrder")
        var conntypeDisplayOrder: String,
        @SerializedName("conntypeID")
        var conntypeID: String,
        @SerializedName("conntypeName")
        var conntypeName: String,
        @SerializedName("conntypeRemarks")
        var conntypeRemarks: String,
        @SerializedName("conntypeStatus")
        var conntypeStatus: String
) : Serializable