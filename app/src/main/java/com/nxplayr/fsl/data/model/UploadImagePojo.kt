package com.nxplayr.fsl.data.model


import com.google.gson.annotations.SerializedName

data class UploadImagePojo(

         @SerializedName("fileName")
        var fileName: String? = "",
        @SerializedName("message")
        val message: String? = "",
        @SerializedName("status")
        val status: String? = "",
         var fileSize: String? = "",

         )