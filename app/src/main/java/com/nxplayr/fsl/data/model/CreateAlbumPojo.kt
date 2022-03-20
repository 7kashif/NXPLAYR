package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class CreateAlbumPojo(
    @SerializedName("data")
    var `data`: List<CreateAlbumData?>? = listOf(),
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("status")
    var status: String? = ""
) : Serializable

data class CreateAlbumData(
    @SerializedName("AlbumsPostCount")
    var albumsPostCount: List<AlbumsPostCount?>? = listOf(),
    @SerializedName("exalbumCreatedDate")
    var exalbumCreatedDate: String? = "",
    @SerializedName("exalbumID")
    var exalbumID: String? = "",
    @SerializedName("exalbumName")
    var exalbumName: String? = "",
    @SerializedName("SubAlbums")
    var subAlbums: List<Any?>? = listOf(),
    @SerializedName("userID")
    var userID: String? = ""
): Serializable

data class AlbumsPostCount(
    @SerializedName("postCount")
    var postCount: String? = ""
): Serializable

