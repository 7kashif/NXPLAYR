package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class CountryListPojo(
    @SerializedName("data")
        var `data`: List<CountryListData> = listOf(),
    @SerializedName("message")
        var message: String = "",
    @SerializedName("status")
        var status: String = ""
) : Serializable

data class CountryListData(
        @SerializedName("countryCreatedDate")
        var countryCreatedDate: String = "",
        @SerializedName("countryCurrencyCode")
        var countryCurrencyCode: String = "",
        @SerializedName("countryDialCode")
        var countryDialCode: String = "",
        @SerializedName("countryFlagImage")
        var countryFlagImage: String = "",
        @SerializedName("countryID")
        var countryID: String = "",
        @SerializedName("countryISO2Code")
        var countryISO2Code: String = "",
        @SerializedName("countryISO3Code")
        var countryISO3Code: String = "",
        @SerializedName("countryName")
        var countryName: String = "",
        @SerializedName("countryRemark")
        var countryRemark: String = "",
        @SerializedName("countryStatus")
        var countryStatus: String = "",
        var checked: Boolean = false,
        var isServerChecked:Boolean= false
) : Serializable

