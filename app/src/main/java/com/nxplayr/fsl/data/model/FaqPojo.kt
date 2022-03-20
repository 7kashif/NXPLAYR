package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName


data class FaqPojo(
    @SerializedName("data")
        var `data`: List<FaqData>,
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
)

data class FaqData(
        @SerializedName("faqAnswer")
        var faqAnswer: String,
        @SerializedName("faqID")
        var faqID: String,
        @SerializedName("faqQuestion")
        var faqQuestion: String
)