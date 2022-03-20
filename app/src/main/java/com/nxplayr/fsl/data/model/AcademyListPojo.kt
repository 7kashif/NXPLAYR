package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class AcademyListPojo(
    @SerializedName("data")
        var `data`: DataAcademy? = DataAcademy(),
    @SerializedName("status")
        var status: String?
)

data class DataAcademy(
        @SerializedName("count")
        var count: Int? = 0,
        @SerializedName("rows")
        var rows: List<RowAcademic>? = ArrayList<RowAcademic>()
) : Serializable

data class RowAcademic(
        @SerializedName("academyID")
        var academyID: Int?,
        @SerializedName("academyName")
        var academyName: String?,
        @SerializedName("academyRemarks")
        var academyRemarks: String?,
        @SerializedName("academyStatus")
        var academyStatus: String?
) : Serializable