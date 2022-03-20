package com.nxplayr.fsl.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class CreateAlbumListPojo(

    @SerializedName("data")
        @Expose
        var data: List<AlbumDatum> = listOf(),
    @SerializedName("status")
        @Expose
        var status: String,
    @SerializedName("message")
        @Expose
        var message: String
) : Serializable

data class AlbumDatum(

    @SerializedName("exalbumID")
        @Expose
        var exalbumID: String,
    @SerializedName("userID")
        @Expose
        var userID: String,
    @SerializedName("exalbumName")
        @Expose
        var exalbumName: String,
    @SerializedName("exalbumCreatedDate")
        @Expose
        var exalbumCreatedDate: String,
    @SerializedName("AlbumsPostCount")
        @Expose
        var albumsPostCount: List<AlbumsPostCountList> = listOf(),
    @SerializedName("SubAlbums")
        @Expose
        var subAlbums: List<SubAlbum> = listOf()

) : Serializable

data class SubAlbum(
        @SerializedName("exsubalbumID")
        @Expose
        var exsubalbumID: String,
        @SerializedName("userID")
        @Expose
        var userID: String,
        @SerializedName("exalbumID")
        @Expose
        var exalbumID: String,
        @SerializedName("exsubalbumName")
        @Expose
        var exsubalbumName: String,
        @SerializedName("exsubalbumCreatedDate")
        @Expose
        var exsubalbumCreatedDate: String,
        @SerializedName("AlbumsPostCount")
        @Expose
        var subAlbumsPostCount: List<SubAlbumsPostCount> = listOf()

) : Serializable

data class SubAlbumsPostCount(
        @SerializedName("postCount")
        @Expose
        var postCount: String

) : Serializable

data class AlbumsPostCountList(
        @SerializedName("postCount")
        @Expose
        var postCount: String

) : Serializable
