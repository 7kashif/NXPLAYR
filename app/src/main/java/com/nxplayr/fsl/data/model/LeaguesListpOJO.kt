package com.nxplayr.fsl.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LeaguesListpOJO(

    @SerializedName("data")
        @Expose
        var `data`: List<UserLanguageList>? = listOf(),
    @SerializedName("status")
        @Expose
        var status: String? = "",
    @SerializedName("message")
        @Expose
        var message: String? = ""
): Serializable

data class UserLanguageList (

        @SerializedName("leagueID")
        @Expose
        var leagueID: String? = "",
        @SerializedName("leagueName")
        @Expose
        var leagueName: String? = "",
        @SerializedName("leagueRemark")
        @Expose
        var leagueRemark: String? = "",
        @SerializedName("leagueStatus")
        @Expose
        var leagueStatus: String? = "",
        @SerializedName("leagueCreatedDate")
        @Expose
        var leagueCreatedDate: String? = "",
        var isSelect: Boolean = false
): Serializable