package com.nxplayr.fsl.data.model
import com.google.gson.annotations.SerializedName


data class ReplyComment(
    @SerializedName("data")
    var `data`: List<Postcommentreply>,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: String
)

