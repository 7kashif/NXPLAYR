package com.nxplayr.fsl.data.model
import com.google.gson.annotations.SerializedName


data class MultiplefileUpload(
    @SerializedName("data")
    var `data`: List<MultiplefileData>,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: String
)

data class MultiplefileData(
    @SerializedName("filename")
    var filename: String,
    @SerializedName("uploadstatus")
    var uploadstatus: String,

    var addImages:List<CreatePostPhotoPojo>?=null
)