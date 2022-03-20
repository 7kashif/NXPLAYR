package com.nxplayr.fsl.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PostlikePojo {
    @SerializedName("data")
    @Expose
    var data: List<Datum>? = null

    @SerializedName("status")
    @Expose
    var status: String? = null

    @SerializedName("message")
    @Expose
    var message: String? = null
    var postId: String? = null
    var feedDatum: CreatePostData? = null

    inner class Datum {
        @SerializedName("postLike")
        @Expose
        var postLike: String? = null

        @SerializedName("postDisLike")
        @Expose
        var postDisLike: String? = null

        @SerializedName("postrecentlikedusers")
        @Expose
        var postrecentlikedusers: List<Postrecentlikeduser>? = null

        @SerializedName("postrecentdislikedusers")
        @Expose
        var postrecentdislikedusers: List<Any>? = null

    }

    inner class Postrecentlikeduser {
        @SerializedName("userID")
        @Expose
        var userID: String? = null

        @SerializedName("userFullname")
        @Expose
        var userFullname: String? = null

    }
}