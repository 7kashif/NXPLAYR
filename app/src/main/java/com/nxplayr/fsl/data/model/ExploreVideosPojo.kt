package com.nxplayr.fsl.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ExploreVideosPojo(

    @SerializedName("data")
    @Expose
    var data: List<CreatePostData>? = listOf(),
    @SerializedName("status")
    @Expose
    var status: String? = "",
    @SerializedName("message")
    @Expose
    var message: String? = ""
    ): Serializable


data class PostALbum (

        @SerializedName("exalbumName")
        @Expose
        var exalbumName: String? = "",
        @SerializedName("exsubalbumName")
        @Expose
        var exsubalbumName: String? = "",
        @SerializedName("expostCreatedDate")
        @Expose
        var expostCreatedDate: String? = "",
        @SerializedName("exalbumID")
        @Expose
        var exalbumID: String? = "",
        @SerializedName("exsubalbumID")
        @Expose
        var exsubalbumID: String? = "",
        @SerializedName("expostID")
        @Expose
        var expostID: String? = ""

): Serializable