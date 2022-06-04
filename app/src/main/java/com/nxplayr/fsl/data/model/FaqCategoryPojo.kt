package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName

data class FaqCategoryPojo(
    @SerializedName("data")
        var `data`: List<FaqCategoryData> = listOf(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
)

data class FaqCategoryData(
        @SerializedName("faqcategoryID")
        var faqcategoryID: String,
        @SerializedName("faqcategoryName")
        var faqcategoryName: String,
        @SerializedName("faqcategoryNameFrench")
        var faqcategoryNameFrench: String,
        @SerializedName("faqcategoryRemarks")
        var faqcategoryRemarks: String,
        @SerializedName("faqcategoryStatus")
        var faqcategoryStatus: String,
        @SerializedName("faqcategoryImage")
        var faqcategoryImage: String
)