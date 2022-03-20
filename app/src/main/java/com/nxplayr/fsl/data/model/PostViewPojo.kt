package com.nxplayr.fsl.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PostViewPojo (
    @SerializedName("data")
    @Expose
    var data: List<PostViewData>? = null,

    @SerializedName("status")
    @Expose
    var status: String? = null,

    @SerializedName("message")
    @Expose
    var message: String? = null,
    var feedDatum: CreatePostData?=null
):Serializable
data  class PostViewData (
        @SerializedName("userID")
        @Expose
        var userID: String? = null,

        @SerializedName("userFullname")
        @Expose
        var userFullname: String? = null
):Serializable