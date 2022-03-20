package com.nxplayr.fsl.data.model
import com.google.gson.annotations.SerializedName



data class GeomobilitysPojo(
    @SerializedName("data")
    val `data`: List<GeomobilitysData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)

data class GeomobilitysData(
    @SerializedName("geomobilityCreatedDate")
    val geomobilityCreatedDate: String,
    @SerializedName("geomobilityID")
    val geomobilityID: String,
    @SerializedName("geomobilityName")
    val geomobilityName: String,
    @SerializedName("geomobilityRemark")
    val geomobilityRemark: String,
    @SerializedName("geomobilityStatus")
    val geomobilityStatus: String,
    var checked: Boolean=false
)