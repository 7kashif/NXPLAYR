package com.nxplayr.fsl.data.model
import com.google.gson.annotations.SerializedName



data class OutfittersPojo(
    @SerializedName("data")
    val `data`: List<OutfittersPojoData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)

data class OutfittersPojoData(
    @SerializedName("outfitterCreatedDate")
    val outfitterCreatedDate: String,
    @SerializedName("outfitterID")
    val outfitterID: String,
    @SerializedName("outfitterName")
    val outfitterName: String,
    @SerializedName("outfitterRemark")
    val outfitterRemark: String,
    @SerializedName("outfitterStatus")
    val outfitterStatus: String,
    @SerializedName("useroutfitterID")
    val useroutfitterID: String,
    @SerializedName("userID")
    val userID: String,
    var checked:Boolean=false
)