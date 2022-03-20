package com.nxplayr.fsl.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WebsitePojo(

    @SerializedName("data")
        @Expose
        var data: List<SiteList>? = listOf(),
    @SerializedName("status")
        @Expose
        var status: String? = "",
    @SerializedName("message")
        @Expose
        var message: String? = ""
): Serializable

data class SiteList (

        @SerializedName("userurlID")
        @Expose
        var userurlID: String? = "",
        @SerializedName("userID")
        @Expose
        var userID: String? = "",
        @SerializedName("userurlName")
        @Expose
        var userurlName: String? = "",
        @SerializedName("userurlLink")
        @Expose
        var userurlLink: String? = "",
        @SerializedName("userurlStatus")
        @Expose
        var userurlStatus: String? = ""

): Serializable