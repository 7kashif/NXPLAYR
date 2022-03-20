package com.nxplayr.fsl.data.model
import com.google.gson.annotations.SerializedName



data class VerificationCatPojo(
    @SerializedName("data")
    val `data`: List<VerificationCatData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)

data class VerificationCatData(
    @SerializedName("verificationcatCreatedDate")
    val verificationcatCreatedDate: String,
    @SerializedName("verificationcatID")
    val verificationcatID: String,
    @SerializedName("verificationcatName")
    val verificationcatName: String,
    @SerializedName("verificationcatStatus")
    val verificationcatStatus: String
)