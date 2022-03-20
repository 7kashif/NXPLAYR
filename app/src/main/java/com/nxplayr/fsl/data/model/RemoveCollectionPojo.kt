package com.nxplayr.fsl.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RemoveCollectionPojo(

        @SerializedName("status")
        @Expose
        var status: String,
        @SerializedName("message")
        @Expose
        var message: String
) : Serializable