package com.nxplayr.fsl.data.model
import com.google.gson.annotations.SerializedName



data class
ContractSitiuation(
    @SerializedName("data")
    val `data`: List<ContractSitiuationData>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
)

data class ContractSitiuationData(
    @SerializedName("contractsituationCreatedDate")
    val contractsituationCreatedDate: String,
    @SerializedName("contractsituationID")
    val contractsituationID: String,
    @SerializedName("contractsituationName")
    val contractsituationName: String,
    @SerializedName("contractsituationRemark")
    val contractsituationRemark: String,
    @SerializedName("contractsituationStatus")
    val contractsituationStatus: String,

    var userContractExpiryDate: String="",
    var checked: Boolean = false

)