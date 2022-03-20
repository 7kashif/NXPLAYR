package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName


data class SkillsPojo(
    @SerializedName("data")
        var `data`: ArrayList<UsersSkils> = ArrayList(),
    @SerializedName("message")
        var message: String,
    @SerializedName("status")
        var status: String
)

data class UsersSkils(
        @SerializedName("skillID")
        var skillID: String,
        @SerializedName("skillName")
        var skillName: String,
        @SerializedName("userID")
        var userID: String,
        @SerializedName("userskillCreatedDate")
        var userskillCreatedDate: String,
        @SerializedName("userskillID")
        var userskillID: String
)