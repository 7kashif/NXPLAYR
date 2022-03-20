package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName

data class CompanyListPojo(
    @SerializedName("data")
        var `data`: List<CompanyListData>,
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
)

data class CompanyListData(
        @SerializedName("companyCreatedDate")
        var companyCreatedDate: String,
        @SerializedName("companyID")
        var companyID: String,
        @SerializedName("companyName")
        var companyName: String,
        @SerializedName("companyRemarks")
        var companyRemarks: Any,
        @SerializedName("companyStatus")
        var companyStatus: String
)