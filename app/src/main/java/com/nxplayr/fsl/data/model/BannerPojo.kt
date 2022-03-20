package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class BannerPojo(
    @SerializedName("Banners")
        var  `data`: List<Banner> = listOf(),
    @SerializedName("message")
        var message: String = "",
    @SerializedName("status")
        var status: String = ""
) : Serializable

data class Banner(
        @SerializedName("bannerID")
        var bannerID: String = "",
        @SerializedName("bannerName")
        var bannerName: String = "",
        @SerializedName("bannerType")
        var bannerType: String = "",
        @SerializedName("bannerTypeID")
        var bannerTypeID: String = "",
        @SerializedName("bannerURL")
        var bannerURL: String = "",
        @SerializedName("bannerStatus")
        var bannerStatus: String = "",
        @SerializedName("bannerImage")
        var bannerImage: String = "",
        @SerializedName("bannerPosition")
        var bannerPosition: String = "",
        var checked: Boolean = false
) : Serializable

