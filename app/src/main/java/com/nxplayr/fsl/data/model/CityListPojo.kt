package com.nxplayr.fsl.data.model
import com.google.gson.annotations.SerializedName


data class CityListPojo(
    @SerializedName("data")
    var `data`: List<CityListData>,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: String
)

data class CityListData(
    @SerializedName("cityCreatedDate")
    var cityCreatedDate: String,
    @SerializedName("cityID")
    var cityID: String,
    @SerializedName("cityName")
    var cityName: String,
    @SerializedName("cityRemark")
    var cityRemark:  String="",
    @SerializedName("cityStatus")
    var cityStatus: String,
    @SerializedName("countryID")
    var countryID: String,
    @SerializedName("stateID")
    var stateID: String
)