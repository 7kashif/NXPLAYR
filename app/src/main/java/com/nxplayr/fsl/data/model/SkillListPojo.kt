package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class SkillListPojo(
    @SerializedName("data")
        var `data`: List<SkillList> = listOf(),
    @SerializedName("message")
        var message: String = "",
    @SerializedName("status")
        var status: String = ""
) : Serializable

data class SkillList(
        @SerializedName("skillCreatedDate")
        var skillCreatedDate: String = "",
        @SerializedName("userID")
        var userID: String = "",
        @SerializedName("skillID")
        var skillID: String = "",
        @SerializedName("userskillCreatedDate")
        var userskillCreatedDate: String = "",
        @SerializedName("userskillID")
        var userskillID: String = "",
        @SerializedName("skillName")
        var skillName: String = "",
        @SerializedName("skillRemarks")
        var skillRemarks: Any,
        @SerializedName("skillStatus")
        var skillStatus: String = "",
        var isSelect: Boolean = false
) : Serializable