package com.nxplayr.fsl.data.model
import com.google.gson.annotations.SerializedName


data class acadamicObj(
    @SerializedName("apiType")
    var apiType: String?="",
    @SerializedName("apiVersion")
    var apiVersion: String?="",
    @SerializedName("languageID")
    var languageID: String?="",
    @SerializedName("loginuserID")
    var loginuserID: String="",
    @SerializedName("page")
    var page: Int?=0,
    @SerializedName("pagesize")
    var pagesize: Int?=0
)