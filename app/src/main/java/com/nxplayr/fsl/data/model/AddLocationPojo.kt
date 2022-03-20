package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class AddLocationPojo(
    @SerializedName("data")
        var `data`: List<AddLocationData>,
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
):Serializable

data class AddLocationData(
        @SerializedName("cityID")
        var cityID: String,
        @SerializedName("cityName")
        var cityName: String,
        @SerializedName("countryID")
        var countryID: String,
        @SerializedName("countryName")
        var countryName: String,
        @SerializedName("userID")
        var userID: String,
        @SerializedName("userlocationID")
        var userlocationID: String,
        @SerializedName("userlocationPincode")
        var userlocationPincode: String,
        @SerializedName("userlocationType")
        var userlocationType: String
):Serializable