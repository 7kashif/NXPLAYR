package com.nxplayr.fsl.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LikePostPojo(

    @SerializedName("data")
        @Expose
        var data: List<LikeData> = listOf(),
    @SerializedName("status")
        @Expose
        var status: String,
    @SerializedName("message")
        @Expose
        var message: String
) : Serializable

data class LikeData(

        @SerializedName("postID")
        @Expose
        var postID: String,
        @SerializedName("postLike")
        @Expose
        var postLike: String
) : Serializable