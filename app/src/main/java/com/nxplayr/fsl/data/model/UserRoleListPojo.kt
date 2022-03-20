package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class UserRoleListPojo(
    @SerializedName("data")
    var `data`: List<UserRoleListData> = listOf(),
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: String = ""
):Serializable

data class UserRoleListData(
    @SerializedName("apputypeCreatedDate")
    var apputypeCreatedDate: String = "",
    @SerializedName("apputypeID")
    var apputypeID: String = "",
    @SerializedName("apputypeImage")
    var apputypeImage: String = "",
    @SerializedName("apputypeName")
    var apputypeName: String = "",
    @SerializedName("apputypeRemarks")
    var apputypeRemarks: String = "",
    @SerializedName("apputypeStatus")
    var apputypeStatus: String = "",
    var checked: Boolean? = false
):Serializable