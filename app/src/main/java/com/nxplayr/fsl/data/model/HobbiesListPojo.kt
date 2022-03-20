package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName


data class HobbiesListPojo(
    @SerializedName("data")
        var `data`: List<HobbiesList> = listOf(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
)

data class HobbiesList(
        @SerializedName("hobbyCreatedDate")
        var hobbyCreatedDate: String,
        @SerializedName("hobbyID")
        var hobbyID: String,
        @SerializedName("hobbyName")
        var hobbyName: String,
        @SerializedName("hobbyRemarks")
        var hobbyRemarks: Any,
        @SerializedName("hobbyStatus")
        var hobbyStatus: String
)